package sentimentHealthMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * SentimentHealthDatabaseWriter
 *
 * Saves extracted sentiment/health candidates into the
 * sentiment_health_candidates table.
 */
public class SentimentHealthDatabaseWriter {

    public static void saveCandidate(Connection connection, SentimentHealthCandidate candidate) throws SQLException {

        if (connection == null || candidate == null) {
            return;
        }

        String sql = "INSERT INTO sentiment_health_candidates "
                + "(proposal_identifier, proposal_number, message_id, thread_id, parent_message_id, "
                + "author_name, author_email, author_role, message_date, sentence, sentiment_categories, "
                + "primary_sentiment_category, sentiment_polarity, sentiment_target, health_signals, "
                + "primary_health_signal, health_dimensions, primary_health_dimension, intensity_level, "
                + "intensity_label, health_support_score, health_risk_score, overall_health_label, "
                + "final_decision, decision_relationship, extraction_scheme) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, candidate.proposalIdentifier);
        ps.setInt(2, candidate.proposalNumber);
        ps.setString(3, candidate.messageId);
        ps.setString(4, candidate.threadId);
        ps.setString(5, candidate.parentMessageId);
        ps.setString(6, candidate.authorName);
        ps.setString(7, candidate.authorEmail);
        ps.setString(8, candidate.authorRole);
        ps.setString(9, candidate.messageDate);
        ps.setString(10, candidate.sentence);
        ps.setString(11, candidate.sentimentCategories);
        ps.setString(12, candidate.primarySentimentCategory);
        ps.setString(13, candidate.sentimentPolarity);
        ps.setString(14, candidate.sentimentTarget);
        ps.setString(15, candidate.healthSignals);
        ps.setString(16, candidate.primaryHealthSignal);
        ps.setString(17, candidate.healthDimensions);
        ps.setString(18, candidate.primaryHealthDimension);
        ps.setInt(19, candidate.intensityLevel);
        ps.setString(20, candidate.intensityLabel);
        ps.setDouble(21, candidate.healthSupportScore);
        ps.setDouble(22, candidate.healthRiskScore);
        ps.setString(23, candidate.overallHealthLabel);
        ps.setString(24, candidate.finalDecision);
        ps.setString(25, candidate.decisionRelationship);
        ps.setString(26, "heuristic-v1");

        ps.executeUpdate();
        ps.close();
    }
}
