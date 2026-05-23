package sentimentMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SentimentDatabaseWriter {

    public static void saveCandidate(Connection connection, SentimentCandidate candidate) throws Exception {

        String sql = "INSERT INTO sentiment_candidates "
                + "(proposal_identifier, proposal_number, message_id, sentence, author_role, "
                + "sentiment_label, sentiment_score, emotion_category, stress_signal, toxicity_signal, "
                + "governance_era, final_decision) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, candidate.proposalIdentifier);
        ps.setInt(2, candidate.proposalNumber);
        ps.setString(3, candidate.messageId);
        ps.setString(4, candidate.sentence);
        ps.setString(5, candidate.authorRole);
        ps.setString(6, candidate.sentimentLabel);
        ps.setDouble(7, candidate.sentimentScore);
        ps.setString(8, candidate.emotionCategory);
        ps.setBoolean(9, candidate.stressSignal);
        ps.setBoolean(10, candidate.toxicitySignal);
        ps.setString(11, candidate.governanceEra);
        ps.setString(12, candidate.finalDecision);

        ps.executeUpdate();
        ps.close();
    }
}
