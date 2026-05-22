package preferenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;

/*
 * PreferenceExtractor
 *
 * Main extraction pipeline for Preference Miner.
 *
 * Updated after checking the real DeMaP Miner database schema.
 *
 * Instead of reading raw email bodies from allmessages and splitting them again,
 * this version reads from the existing allsentences table.
 *
 * Useful real schema fields:
 * - allsentences.proposal
 * - allsentences.messageid
 * - allsentences.sentence
 * - allsentences.msgSubject
 * - allsentences.msgAuthorRole
 * - allsentences.dateDiff
 * - allsentences.isEnglishOrCode
 * - allsentences.positiveWordCount
 * - allsentences.negativeWordCount
 *
 * This makes Preference Miner closer to Rationale Miner because we reuse the
 * already-prepared sentence-level representation.
 */
public class PreferenceExtractor {

    public static final double MINIMUM_SCORE = 1.0;

    public static void extractPreferences(int proposalNumber, String proposalIdentifier) {

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {

            connection = MysqlConnect.connect();
            statement = connection.createStatement();

            String finalDecision = PreferenceDecisionResolver.getFinalDecision(
                    connection,
                    proposalNumber,
                    proposalIdentifier
            );

            /*
             * Use allsentences because the database already stores messages split
             * into sentence-level units.
             */
            String sql = "SELECT proposal, messageid, sentence, msgSubject, msgAuthorRole, "
                    + "dateDiff, isEnglishOrCode, positiveWordCount, negativeWordCount "
                    + "FROM allsentences "
                    + "WHERE proposal = " + proposalNumber + " "
                    + "AND sentence IS NOT NULL "
                    + "AND TRIM(sentence) <> ''";

            rs = statement.executeQuery(sql);

            int savedCount = 0;
            int checkedCount = 0;

            while (rs.next()) {

                checkedCount++;

                String messageId = String.valueOf(rs.getInt("messageid"));
                String sentence = rs.getString("sentence");
                String authorRole = rs.getString("msgAuthorRole");
                int daysBeforeDecision = rs.getInt("dateDiff");

                if (sentence == null || sentence.trim().length() < 5) {
                    continue;
                }

                sentence = sentence.trim();

                String polarity = PreferencePolarity.detectPolarity(sentence);

                double score = PreferenceHeuristics.scoreSentence(
                        sentence,
                        authorRole,
                        daysBeforeDecision
                );

                if (score >= MINIMUM_SCORE && !polarity.equals("neutral")) {

                    PreferenceCandidate candidate = new PreferenceCandidate();

                    candidate.proposalIdentifier = proposalIdentifier;
                    candidate.proposalNumber = proposalNumber;

                    candidate.messageId = messageId;
                    candidate.authorName = null;
                    candidate.authorEmail = null;
                    candidate.authorRole = authorRole;

                    candidate.messageDate = null;
                    candidate.sentence = sentence;

                    candidate.polarity = polarity;
                    candidate.score = score;

                    candidate.finalDecision = finalDecision;
                    candidate.alignsWithDecision = PreferenceAlignmentAnalyzer.aligns(
                            polarity,
                            finalDecision
                    );

                    PreferenceDatabaseWriter.saveCandidate(connection, candidate);
                    savedCount++;

                    System.out.println("Saved preference candidate: " + sentence);
                }
            }

            System.out.println("Preference extraction completed.");
            System.out.println("Sentences checked: " + checkedCount);
            System.out.println("Preference candidates saved: " + savedCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
