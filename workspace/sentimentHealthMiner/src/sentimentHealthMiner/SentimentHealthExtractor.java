package sentimentHealthMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;

/*
 * SentimentHealthExtractor
 *
 * Main extraction pipeline for Sentiment/Health Miner.
 *
 * First prototype:
 * - Read proposal-linked messages from allmessages table.
 * - Split message body into sentences.
 * - Detect sentiment categories, polarity, target, health signals and dimensions.
 * - Compute heuristic health support and health risk scores.
 * - Save high-scoring candidates into sentiment_health_candidates.
 *
 * NOTE:
 * This is intentionally lightweight for the first integration version.
 * Later versions can incorporate:
 * - Stanford CoreNLP sentence splitting,
 * - thread-level emotional trajectories,
 * - reply-network and reciprocity features,
 * - role-aware burden measures,
 * - temporal distance from decision date,
 * - proposal revision tracking,
 * - BERT/LLM multi-label sentiment and health classification.
 */
public class SentimentHealthExtractor {

    /*
     * Minimum support/risk threshold.
     * A sentence is stored if either support score or risk score reaches this value.
     */
    public static final double MINIMUM_SCORE = 1.2;

    public static void extractSentimentHealth(int proposalNumber, String proposalIdentifier) {

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        int savedCount = 0;

        try {

            connection = MysqlConnect.connect();
            statement = connection.createStatement();

            /*
             * IMPORTANT:
             * This query assumes DeMaP Miner stores proposal-linked messages
             * in the allmessages table. Adapt column names if needed.
             */
            String sql = "SELECT * FROM allmessages WHERE proposalNumber = " + proposalNumber;

            rs = statement.executeQuery(sql);

            while (rs.next()) {

                String messageId = getColumn(rs, new String[] {
                        "messageID", "messageId", "message_id", "emailMessageID", "emailMessageId"
                });

                String threadId = getColumn(rs, new String[] {
                        "threadID", "threadId", "thread_id", "conversationID", "conversationId"
                });

                String parentMessageId = getColumn(rs, new String[] {
                        "inReplyTo", "inReplyToMessageID", "inReplyToMessageId", "parentMessageID",
                        "parentMessageId", "parent_message_id"
                });

                String authorName = getColumn(rs, new String[] {
                        "fromName", "from_name", "senderName", "sender_name", "author", "msgFrom"
                });

                String authorEmail = getColumn(rs, new String[] {
                        "fromEmail", "from_email", "senderEmail", "sender_email", "email", "msgFromEmail"
                });

                String messageBody = getColumn(rs, new String[] {
                        "body", "clean_body", "message", "email", "text", "content"
                });

                String messageDate = getColumn(rs, new String[] {
                        "date", "messageDate", "message_date", "sentDate", "sent_date"
                });

                String finalDecision = getColumn(rs, new String[] {
                        "finalDecision", "final_decision", "proposalStatus", "proposal_status",
                        "pepStatus", "bipStatus", "status"
                });

                if (finalDecision == null || finalDecision.trim().length() == 0) {
                    finalDecision = "unknown";
                }

                if (messageBody == null || messageBody.trim().length() == 0) {
                    continue;
                }

                String normalizedBody = messageBody.replaceAll("\\s+", " ").trim();

                /*
                 * Split into sentences.
                 * Current implementation is intentionally simple.
                 */
                String[] sentences = normalizedBody.split("(?<=[.!?])\\s+");

                for (int i = 0; i < sentences.length; i++) {

                    String sentence = sentences[i].trim();

                    if (sentence.length() < 5) {
                        continue;
                    }

                    String role = SentimentHealthRoleMapper.mapRole(authorEmail, authorName);
                    String categories = SentimentCategoryDetector.detectCategories(sentence);
                    String polarity = SentimentPolarityDetector.detectPolarity(sentence, categories);
                    String target = SentimentTargetDetector.detectTarget(sentence);
                    String healthSignals = HealthSignalDetector.detectSignals(sentence, categories, polarity);

                    if ((categories.equals("none") || categories.length() == 0)
                            && (healthSignals.equals("none") || healthSignals.length() == 0)
                            && (polarity.equals("neutral") || polarity.equals("unknown"))) {
                        continue;
                    }

                    String primaryCategory = SentimentCategoryDetector.primaryCategory(categories);
                    String primaryHealthSignal = HealthSignalDetector.primarySignal(healthSignals);
                    String healthDimensions = HealthDimensionDetector.detectDimensions(healthSignals, categories, target);
                    String primaryHealthDimension = HealthDimensionDetector.primaryDimension(healthDimensions);

                    int intensityLevel = SentimentHealthIntensityDetector.detectIntensity(
                            sentence,
                            categories,
                            polarity,
                            healthSignals,
                            healthDimensions
                    );
                    String intensityLabel = SentimentHealthIntensityDetector.intensityLabel(intensityLevel);

                    double supportScore = SentimentHealthHeuristics.scoreSupport(
                            sentence,
                            role,
                            categories,
                            polarity,
                            target,
                            healthSignals,
                            healthDimensions,
                            intensityLevel
                    );

                    double riskScore = SentimentHealthHeuristics.scoreRisk(
                            sentence,
                            role,
                            categories,
                            polarity,
                            target,
                            healthSignals,
                            healthDimensions,
                            intensityLevel
                    );

                    if (supportScore >= MINIMUM_SCORE || riskScore >= MINIMUM_SCORE) {

                        SentimentHealthCandidate candidate = new SentimentHealthCandidate();

                        candidate.proposalIdentifier = proposalIdentifier;
                        candidate.proposalNumber = proposalNumber;

                        candidate.messageId = messageId;
                        candidate.threadId = threadId;
                        candidate.parentMessageId = parentMessageId;

                        candidate.authorName = authorName;
                        candidate.authorEmail = authorEmail;
                        candidate.authorRole = role;

                        candidate.messageDate = messageDate;
                        candidate.sentence = sentence;

                        candidate.sentimentCategories = categories;
                        candidate.primarySentimentCategory = primaryCategory;
                        candidate.sentimentPolarity = polarity;
                        candidate.sentimentTarget = target;

                        candidate.healthSignals = healthSignals;
                        candidate.primaryHealthSignal = primaryHealthSignal;
                        candidate.healthDimensions = healthDimensions;
                        candidate.primaryHealthDimension = primaryHealthDimension;

                        candidate.intensityLevel = intensityLevel;
                        candidate.intensityLabel = intensityLabel;

                        candidate.healthSupportScore = supportScore;
                        candidate.healthRiskScore = riskScore;
                        candidate.overallHealthLabel = SentimentHealthHeuristics.overallHealthLabel(
                                supportScore,
                                riskScore
                        );

                        candidate.finalDecision = finalDecision;
                        candidate.decisionRelationship = SentimentHealthOutcomeAnalyzer.analyzeRelationship(
                                polarity,
                                candidate.overallHealthLabel,
                                finalDecision,
                                healthDimensions
                        );

                        SentimentHealthDatabaseWriter.saveCandidate(connection, candidate);
                        savedCount++;

                        System.out.println("Saved sentiment/health candidate: " + sentence);
                    }
                }
            }

            System.out.println("Sentiment/Health extraction completed. Saved candidates: " + savedCount);

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
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception closeException) {
                closeException.printStackTrace();
            }
        }
    }

    private static String getColumn(ResultSet rs, String[] columnNames) {

        for (int i = 0; i < columnNames.length; i++) {
            try {
                String value = rs.getString(columnNames[i]);
                if (value != null && value.trim().length() > 0) {
                    return value;
                }
            } catch (Exception e) {
                // Try the next possible column name.
            }
        }

        return "";
    }
}
