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
 * First prototype:
 * - Read proposal messages from allmessages table.
 * - Split message body into sentences.
 * - Detect preference polarity.
 * - Compute heuristic score.
 * - Save high-scoring candidates into preference_candidates.
 *
 * NOTE:
 * This is intentionally lightweight for the first integration version.
 * The real system can later incorporate:
 * - NLP sentence splitting,
 * - role-aware ranking,
 * - SBS/MBS ranking,
 * - BERT/ML preference detection,
 * - temporal filtering.
 */
public class PreferenceExtractor {

    /*
     * Minimum score threshold.
     *
     * Only sentences scoring above this value will be stored.
     */
    public static final double MINIMUM_SCORE = 1.0;

    public static void extractPreferences(int proposalNumber, String proposalIdentifier) {

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {

            connection = MysqlConnect.connect();

            statement = connection.createStatement();

            /*
             * IMPORTANT:
             * This query assumes DeMaP Miner stores proposal-linked messages
             * in the allmessages table.
             *
             * You may need to adapt column names depending on the exact schema.
             */
            String sql = "SELECT * FROM allmessages WHERE proposalNumber = " + proposalNumber;

            rs = statement.executeQuery(sql);

            while (rs.next()) {

                String messageId = rs.getString("messageID");
                String authorName = rs.getString("fromName");
                String authorEmail = rs.getString("fromEmail");
                String messageBody = rs.getString("body");
                String messageDate = rs.getString("date");

                /*
                 * Split into sentences.
                 *
                 * Current implementation is intentionally simple.
                 * Later versions should use Stanford CoreNLP sentence splitting.
                 */
                String[] sentences = messageBody.split("\\.");

                for (String sentence : sentences) {

                    sentence = sentence.trim();

                    if (sentence.length() < 5) {
                        continue;
                    }

                    String role = PreferenceRoleMapper.mapRole(authorEmail, authorName);

                    String polarity = PreferencePolarity.detectPolarity(sentence);

                    /*
                     * Placeholder temporal distance.
                     * Later versions should compute real distance from decision date.
                     */
                    int daysBeforeDecision = 5;

                    double score = PreferenceHeuristics.scoreSentence(
                            sentence,
                            role,
                            daysBeforeDecision
                    );

                    if (score >= MINIMUM_SCORE) {

                        PreferenceCandidate candidate = new PreferenceCandidate();

                        candidate.proposalIdentifier = proposalIdentifier;
                        candidate.proposalNumber = proposalNumber;

                        candidate.messageId = messageId;
                        candidate.authorName = authorName;
                        candidate.authorEmail = authorEmail;
                        candidate.authorRole = role;

                        candidate.messageDate = messageDate;
                        candidate.sentence = sentence;

                        candidate.polarity = polarity;
                        candidate.score = score;

                        /*
                         * Placeholder decision.
                         * Future versions should retrieve real proposal outcome.
                         */
                        candidate.finalDecision = "accepted";

                        candidate.alignsWithDecision =
                                PreferenceAlignmentAnalyzer.aligns(
                                        polarity,
                                        candidate.finalDecision
                                );

                        PreferenceDatabaseWriter.saveCandidate(connection, candidate);

                        System.out.println("Saved preference candidate: " + sentence);
                    }
                }
            }

            System.out.println("Preference extraction completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
