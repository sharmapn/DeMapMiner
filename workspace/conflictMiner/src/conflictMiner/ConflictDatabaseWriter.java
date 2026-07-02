package conflictMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * ConflictDatabaseWriter
 *
 * Saves extracted conflict candidates into the conflict_candidates table.
 */
public class ConflictDatabaseWriter {

    public static void saveCandidate(Connection connection, ConflictCandidate candidate) throws SQLException {

        if (connection == null || candidate == null) {
            return;
        }

        String sql = "INSERT INTO conflict_candidates "
                + "(proposal_identifier, proposal_number, message_id, thread_id, parent_message_id, "
                + "author_name, author_email, author_role, message_date, sentence, conflict_types, "
                + "primary_conflict_type, conflict_stance, conflict_target, resolution_status, "
                + "civility_status, intensity_level, intensity_label, conflict_score, final_decision, "
                + "decision_impact, unresolved_objection, extraction_scheme) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        ps.setString(11, candidate.conflictTypes);
        ps.setString(12, candidate.primaryConflictType);
        ps.setString(13, candidate.conflictStance);
        ps.setString(14, candidate.conflictTarget);
        ps.setString(15, candidate.resolutionStatus);
        ps.setString(16, candidate.civilityStatus);
        ps.setInt(17, candidate.intensityLevel);
        ps.setString(18, candidate.intensityLabel);
        ps.setDouble(19, candidate.score);
        ps.setString(20, candidate.finalDecision);
        ps.setString(21, candidate.decisionImpact);
        ps.setBoolean(22, candidate.unresolvedObjection);
        ps.setString(23, "heuristic-v1");

        ps.executeUpdate();
        ps.close();
    }
}
