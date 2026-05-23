package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InfluenceDatabaseWriter {

    public static void saveCandidate(Connection connection, InfluenceCandidate candidate) throws Exception {

        String sql = "INSERT INTO influence_candidates "
                + "(proposal_identifier, proposal_number, message_id, sentence, author_role, "
                + "influence_category, influence_subcategory, influence_direction, influence_score, "
                + "final_decision, aligns_with_decision) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, candidate.proposalIdentifier);
        ps.setInt(2, candidate.proposalNumber);
        ps.setString(3, candidate.messageId);
        ps.setString(4, candidate.sentence);
        ps.setString(5, candidate.authorRole);
        ps.setString(6, candidate.influenceCategory);
        ps.setString(7, candidate.influenceSubcategory);
        ps.setString(8, candidate.influenceDirection);
        ps.setDouble(9, candidate.influenceScore);
        ps.setString(10, candidate.finalDecision);
        ps.setBoolean(11, candidate.alignsWithDecision);

        ps.executeUpdate();
        ps.close();
    }
}
