package conflictMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;

/*
 * ConflictExtractor
 *
 * Main extraction pipeline for Conflict Miner.
 *
 * First prototype:
 * - Read proposal-linked messages from allmessages table.
 * - Split message body into sentences.
 * - Detect conflict types, stance, target, resolution, civility and intensity.
 * - Compute heuristic conflict score.
 * - Save high-scoring candidates into conflict_candidates.
 *
 * NOTE:
 * This is intentionally lightweight for the first integration version.
 * The real system can later incorporate:
 * - better NLP sentence splitting,
 * - reply-network/thread episode construction,
 * - role-aware ranking from project metadata,
 * - temporal distance from actual decision date,
 * - proposal revision tracking,
 * - BERT/LLM multi-label conflict detection.
 */
public class ConflictExtractor {

    /*
     * Minimum score threshold.
     * Only sentences scoring above this value will be stored.
     */
    public static final double MINIMUM_SCORE = 1.2;

    public static void extractConflict(int proposalNumber, String proposalIdentifier) {

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
             * in the allmessages table.
             *
             * You may need to adapt column names depending on the exact schema.
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
                 * Later versions should use Stanford CoreNLP sentence splitting.
                 */
                String[] sentences = normalizedBody.split("(?<=[.!?])\\s+");

                for (int i = 0; i < sentences.length; i++) {

                    String sentence = sentences[i].trim();

                    if (sentence.length() < 5) {
                        continue;
                    }

                    String role = ConflictRoleMapper.mapRole(authorEmail, authorName);
                    String conflictTypes = ConflictTypeDetector.detectTypes(sentence);
                    String stance = ConflictStanceDetector.detectStance(sentence);

                    if (conflictTypes.equals("none") && stance.equals("neutral")) {
                        continue;
                    }

                    if (conflictTypes.equals("none") && !stance.equals("neutral")) {
                        conflictTypes = "general_disagreement";
                    }

                    String primaryConflictType = ConflictTypeDetector.primaryType(conflictTypes);
                    String target = ConflictTargetDetector.detectTarget(sentence);
                    String resolutionStatus = ConflictResolutionDetector.detectResolutionStatus(sentence);
                    String civilityStatus = ConflictCivilityDetector.detectCivility(sentence);
                    int intensityLevel = ConflictIntensityDetector.detectIntensity(
                            sentence,
                            conflictTypes,
                            stance,
                            resolutionStatus
                    );
                    String intensityLabel = ConflictIntensityDetector.intensityLabel(intensityLevel);

                    double score = ConflictHeuristics.scoreSentence(
                            sentence,
                            role,
                            conflictTypes,
                            stance,
                            target,
                            resolutionStatus,
                            civilityStatus,
                            intensityLevel
                    );

                    if (score >= MINIMUM_SCORE) {

                        ConflictCandidate candidate = new ConflictCandidate();

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

                        candidate.conflictTypes = conflictTypes;
                        candidate.primaryConflictType = primaryConflictType;
                        candidate.conflictStance = stance;
                        candidate.conflictTarget = target;
                        candidate.resolutionStatus = resolutionStatus;
                        candidate.civilityStatus = civilityStatus;
                        candidate.intensityLevel = intensityLevel;
                        candidate.intensityLabel = intensityLabel;
                        candidate.score = score;
                        candidate.finalDecision = finalDecision;
                        candidate.decisionImpact = ConflictOutcomeImpactAnalyzer.analyzeImpact(
                                stance,
                                resolutionStatus,
                                finalDecision
                        );
                        candidate.unresolvedObjection = ConflictResolutionDetector.isUnresolvedObjection(resolutionStatus);

                        ConflictDatabaseWriter.saveCandidate(connection, candidate);
                        savedCount++;

                        System.out.println("Saved conflict candidate: " + sentence);
                    }
                }
            }

            System.out.println("Conflict extraction completed. Saved candidates: " + savedCount);

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
