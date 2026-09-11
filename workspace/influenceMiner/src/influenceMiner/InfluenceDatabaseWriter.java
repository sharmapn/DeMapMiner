package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/*
 * InfluenceDatabaseWriter
 *
 * Saves extracted influence candidates into influence_candidates.
 * saveCandidates() batches inserts, which matters when a proposal produces
 * thousands of rows. Dates are normalised to "yyyy-MM-dd HH:mm:ss" so the
 * DATETIME columns accept them; unknown temporal values become NULL.
 */
public class InfluenceDatabaseWriter {

    private static final String INSERT_SQL = "INSERT INTO " + InfluenceSchemaManager.CANDIDATES
            + " (proposal_identifier, proposal_number, message_id, author_name, author_email, author_role, "
            + "message_date, sentence, influence_types, primary_influence_type, influence_scope, "
            + "influence_direction, influence_target, influence_score, final_decision, "
            + "aligns_with_outcome, extraction_scheme, evidence_cues, decision_date, days_before_decision, "
            + "decision_phase, governance_era) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static void saveCandidate(Connection connection, InfluenceCandidate candidate) throws SQLException {

        if (connection == null || candidate == null) {
            return;
        }
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
        try {
            bind(ps, candidate);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    public static int saveCandidates(Connection connection, List<InfluenceCandidate> candidates) throws SQLException {

        if (connection == null || candidates == null || candidates.isEmpty()) {
            return 0;
        }
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
        int saved = 0;
        try {
            int inBatch = 0;
            for (int i = 0; i < candidates.size(); i++) {
                bind(ps, candidates.get(i));
                ps.addBatch();
                inBatch++;
                if (inBatch >= 500) {
                    saved += count(ps.executeBatch());
                    inBatch = 0;
                }
            }
            if (inBatch > 0) {
                saved += count(ps.executeBatch());
            }
        } finally {
            ps.close();
        }
        return saved;
    }

    private static int count(int[] results) {
        int n = 0;
        for (int i = 0; i < results.length; i++) {
            if (results[i] >= 0 || results[i] == PreparedStatement.SUCCESS_NO_INFO) n++;
        }
        return n;
    }

    private static void bind(PreparedStatement ps, InfluenceCandidate c) throws SQLException {
        ps.setString(1, c.proposalIdentifier);
        ps.setInt(2, c.proposalNumber);
        ps.setString(3, c.messageId);
        ps.setString(4, trim(c.authorName, 255));
        ps.setString(5, trim(c.authorEmail, 255));
        ps.setString(6, trim(c.authorRole, 100));
        ps.setString(7, InfluenceTemporalAnalyzer.toSqlDateTime(c.messageDate));
        ps.setString(8, c.sentence);
        ps.setString(9, trim(c.influenceTypes, 255));
        ps.setString(10, trim(c.primaryInfluenceType, 50));
        ps.setString(11, trim(c.influenceScope, 50));
        ps.setString(12, trim(c.influenceDirection, 50));
        ps.setString(13, trim(c.influenceTarget, 100));
        ps.setDouble(14, c.score);
        ps.setString(15, trim(c.finalDecision, 50));
        ps.setBoolean(16, c.alignsWithOutcome);
        ps.setString(17, InfluenceConfig.EXTRACTION_SCHEME);
        ps.setString(18, c.evidenceCues);
        ps.setString(19, InfluenceTemporalAnalyzer.toSqlDateTime(c.decisionDate));
        if (InfluenceTemporalAnalyzer.isKnown(c.daysBeforeDecision)) {
            ps.setInt(20, c.daysBeforeDecision);
        } else {
            ps.setNull(20, Types.INTEGER);
        }
        ps.setString(21, trim(c.decisionPhase, 30));
        ps.setString(22, trim(c.governanceEra, 30));
    }

    private static String trim(String v, int max) {
        if (v == null) return null;
        return v.length() > max ? v.substring(0, max) : v;
    }
}
