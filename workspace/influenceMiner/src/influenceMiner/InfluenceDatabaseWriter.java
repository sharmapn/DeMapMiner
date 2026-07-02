package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * InfluenceDatabaseWriter
 *
 * Saves extracted influence candidates into the influence_candidates table.
 */
public class InfluenceDatabaseWriter {

    public static void saveCandidate(Connection connection, InfluenceCandidate candidate) throws SQLException {

        if (connection == null || candidate == null) {
            return;
        }

        String sql = "INSERT INTO influence_candidates "
                + "(proposal_identifier, proposal_number, message_id, author_name, author_email, author_role, "
                + "message_date, sentence, influence_types, primary_influence_type, influence_scope, "
                + "influence_direction, influence_target, influence_score, final_decision, "
                + "aligns_with_outcome, extraction_scheme) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, candidate.proposalIdentifier);
        ps.setInt(2, candidate.proposalNumber);
        ps.setString(3, candidate.messageId);
        ps.setString(4, candidate.authorName);
        ps.setString(5, candidate.authorEmail);
        ps.setString(6, candidate.authorRole);
        ps.setString(7, candidate.messageDate);
        ps.setString(8, candidate.sentence);
        ps.setString(9, candidate.influenceTypes);
        ps.setString(10, candidate.primaryInfluenceType);
        ps.setString(11, candidate.influenceScope);
        ps.setString(12, candidate.influenceDirection);
        ps.setString(13, candidate.influenceTarget);
        ps.setDouble(14, candidate.score);
        ps.setString(15, candidate.finalDecision);
        ps.setBoolean(16, candidate.alignsWithOutcome);
        ps.setString(17, "heuristic-v1");

        ps.executeUpdate();
        ps.close();
    }
}
