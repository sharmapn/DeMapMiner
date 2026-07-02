package influenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;

/*
 * InfluenceExtractor
 *
 * Main extraction pipeline for Influence Miner.
 *
 * First prototype:
 * - Read proposal messages from allmessages table.
 * - Split message body into sentences.
 * - Detect influence types, direction, scope and target.
 * - Compute heuristic influence score.
 * - Save high-scoring candidates into influence_candidates.
 *
 * NOTE:
 * This is intentionally lightweight for the first integration version.
 * The real system can later incorporate:
 * - better NLP sentence splitting,
 * - role-aware ranking from project metadata,
 * - temporal distance from actual decision date,
 * - reply-network features,
 * - proposal revision tracking,
 * - BERT/LLM multi-label influence detection.
 */
public class InfluenceExtractor {

    /*
     * Minimum score threshold.
     * Only sentences scoring above this value will be stored.
     */
    public static final double MINIMUM_SCORE = 1.0;

    public static void extractInfluence(int proposalNumber, String proposalIdentifier) {

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

                if (messageBody == null || messageBody.trim().length() == 0) {
                    continue;
                }

                /*
                 * Split into sentences.
                 * Current implementation is intentionally simple.
                 * Later versions should use Stanford CoreNLP sentence splitting.
                 */
                String[] sentences = messageBody.split("(?<=[.!?])\\s+");

                for (int i = 0; i < sentences.length; i++) {

                    String sentence = sentences[i].trim();

                    if (sentence.length() < 5) {
                        continue;
                    }

                    String role = InfluenceRoleMapper.mapRole(authorEmail, authorName);
                    String influenceTypes = InfluenceTypeDetector.detectTypes(sentence);

                    if (influenceTypes.equals("none")) {
                        continue;
                    }

                    String primaryInfluenceType = InfluenceTypeDetector.primaryType(influenceTypes);
                    String influenceDirection = InfluenceDirectionDetector.detectDirection(sentence);
                    String influenceScope = InfluenceScopeDetector.detectScope(sentence, role);
                    String influenceTarget = InfluenceTargetDetector.detectTarget(sentence);

                    /*
                     * Placeholder temporal distance.
                     * Later versions should compute real distance from decision date.
                     */
                    int daysBeforeDecision = 5;

                    double score = InfluenceHeuristics.scoreSentence(
                            sentence,
                            role,
                            influenceTypes,
                            influenceDirection,
                            influenceScope,
                            influenceTarget,
                            daysBeforeDecision
                    );

                    if (score >= MINIMUM_SCORE) {

                        InfluenceCandidate candidate = new InfluenceCandidate();

                        candidate.proposalIdentifier = proposalIdentifier;
                        candidate.proposalNumber = proposalNumber;

                        candidate.messageId = messageId;
                        candidate.authorName = authorName;
                        candidate.authorEmail = authorEmail;
                        candidate.authorRole = role;

                        candidate.messageDate = messageDate;
                        candidate.sentence = sentence;

                        candidate.influenceTypes = influenceTypes;
                        candidate.primaryInfluenceType = primaryInfluenceType;
                        candidate.influenceScope = influenceScope;
                        candidate.influenceDirection = influenceDirection;
                        candidate.influenceTarget = influenceTarget;
                        candidate.score = score;

                        /*
                         * Placeholder decision.
                         * Future versions should retrieve real proposal outcome.
                         */
                        candidate.finalDecision = "accepted";

                        candidate.alignsWithOutcome =
                                InfluenceOutcomeAlignmentAnalyzer.aligns(
                                        candidate.influenceDirection,
                                        candidate.finalDecision
                                );

                        InfluenceDatabaseWriter.saveCandidate(connection, candidate);

                        System.out.println("Saved influence candidate: " + sentence);
                    }
                }
            }

            System.out.println("Influence extraction completed.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception closeException) {
                closeException.printStackTrace();
            }
        }
    }
}
