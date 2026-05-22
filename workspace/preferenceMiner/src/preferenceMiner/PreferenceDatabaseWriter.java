package preferenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * PreferenceDatabaseWriter
 *
 * Saves extracted preference candidates into the preference_candidates table.
 * This class is deliberately small so that the extraction logic remains separate
 * from database-writing logic.
 */
public class PreferenceDatabaseWriter {

    public static void saveCandidate(Connection connection, PreferenceCandidate candidate) throws SQLException {

        if (connection == null || candidate == null) {
            return;
        }

        String sql = "INSERT INTO preference_candidates "
                + "(proposal_identifier, proposal_number, message_id, author_name, author_email, author_role, "
                + "message_date, sentence, preference_polarity, preference_score, final_decision, "
                + "aligns_with_decision, extraction_scheme) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, candidate.proposalIdentifier);
        ps.setInt(2, candidate.proposalNumber);
        ps.setString(3, candidate.messageId);
        ps.setString(4, candidate.authorName);
        ps.setString(5, candidate.authorEmail);
        ps.setString(6, candidate.authorRole);
        ps.setString(7, candidate.messageDate);
        ps.setString(8, candidate.sentence);
        ps.setString(9, candidate.polarity);
        ps.setDouble(10, candidate.score);
        ps.setString(11, candidate.finalDecision);
        ps.setBoolean(12, candidate.alignsWithDecision);
        ps.setString(13, "heuristic-v1");

        ps.executeUpdate();
        ps.close();
    }
}
