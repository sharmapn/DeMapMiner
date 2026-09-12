package influenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * InfluenceCandidateRepository
 *
 * Reads influence_candidates back into InfluenceCandidate objects so the
 * aggregator, exporter, sampler and report generator share one loader.
 * Tolerates databases where the newer optional columns are absent.
 */
public class InfluenceCandidateRepository {

    public static class StoredCandidate extends InfluenceCandidate {
        public int id;
        public String extractionScheme;
    }

    public static List<StoredCandidate> loadAll(Connection connection, String proposalIdentifier) throws Exception {
        return load(connection, proposalIdentifier, null, null);
    }

    public static List<StoredCandidate> load(Connection connection, String proposalIdentifier, Integer proposalNumber, String extraWhere) throws Exception {

        List<StoredCandidate> rows = new ArrayList<StoredCandidate>();
        if (!InfluenceSchemaManager.tableExists(connection, InfluenceSchemaManager.CANDIDATES)) {
            return rows;
        }
        Set<String> cols = InfluenceSchemaManager.columns(connection, InfluenceSchemaManager.CANDIDATES);

        StringBuilder sql = new StringBuilder("SELECT * FROM " + InfluenceSchemaManager.CANDIDATES + " WHERE 1=1");
        if (proposalIdentifier != null) {
            sql.append(" AND LOWER(proposal_identifier) = '").append(proposalIdentifier.toLowerCase().replace("'", "''")).append("'");
        }
        if (proposalNumber != null) {
            sql.append(" AND proposal_number = ").append(proposalNumber.intValue());
        }
        if (extraWhere != null && extraWhere.trim().length() > 0) {
            sql.append(" AND (").append(extraWhere).append(")");
        }
        sql.append(" ORDER BY proposal_number, message_date, id");

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql.toString());
        while (rs.next()) {
            StoredCandidate c = new StoredCandidate();
            c.id = rs.getInt("id");
            c.proposalIdentifier = rs.getString("proposal_identifier");
            c.proposalNumber = rs.getInt("proposal_number");
            c.messageId = rs.getString("message_id");
            c.authorName = rs.getString("author_name");
            c.authorEmail = rs.getString("author_email");
            c.authorRole = rs.getString("author_role");
            c.messageDate = rs.getString("message_date");
            c.sentence = rs.getString("sentence");
            c.influenceTypes = rs.getString("influence_types");
            c.primaryInfluenceType = rs.getString("primary_influence_type");
            c.influenceScope = rs.getString("influence_scope");
            c.influenceDirection = rs.getString("influence_direction");
            c.influenceTarget = rs.getString("influence_target");
            c.score = rs.getDouble("influence_score");
            c.finalDecision = rs.getString("final_decision");
            c.alignsWithOutcome = rs.getBoolean("aligns_with_outcome");
            c.extractionScheme = rs.getString("extraction_scheme");
            c.evidenceCues = cols.contains("evidence_cues") ? rs.getString("evidence_cues") : "";
            c.decisionDate = cols.contains("decision_date") ? rs.getString("decision_date") : null;
            if (cols.contains("days_before_decision")) {
                int d = rs.getInt("days_before_decision");
                c.daysBeforeDecision = rs.wasNull() ? InfluenceTemporalAnalyzer.UNKNOWN_DAYS : d;
            }
            c.decisionPhase = cols.contains("decision_phase") ? nz(rs.getString("decision_phase"), "unknown") : "unknown";
            c.governanceEra = cols.contains("governance_era") ? nz(rs.getString("governance_era"), InfluenceConfig.ERA_UNKNOWN)
                    : InfluenceTemporalAnalyzer.governanceEra(c.messageDate);
            if (c.finalDecision == null || c.finalDecision.trim().length() == 0) c.finalDecision = "unknown";
            if (c.influenceDirection == null) c.influenceDirection = "neutral";
            if (c.influenceScope == null) c.influenceScope = "unknown";
            if (c.authorRole == null || c.authorRole.trim().length() == 0) c.authorRole = "unknown";
            rows.add(c);
        }
        rs.close();
        st.close();
        return rows;
    }

    private static String nz(String v, String d) {
        return v == null || v.trim().length() == 0 ? d : v;
    }

    /*
     * Stable key for an actor. The dataset's clusterBySenderFullName (which
     * InfluenceAuthorResolver restores for the shifted rows) is the identity
     * DeMaP Miner uses to merge one person's several addresses, so the
     * normalised name is the primary key; the e-mail is the fallback when no
     * name is known. Keying on e-mail alone would merge everyone who posts
     * through a shared list address and split people with several addresses.
     */
    public static String actorKey(InfluenceCandidate c) {
        String name = InfluenceRoleMapper.normaliseName(c.authorName);
        if (name.length() > 0 && !name.equals("unknown")) {
            return name;
        }
        if (c.authorEmail != null && c.authorEmail.trim().length() > 0) {
            return c.authorEmail.trim().toLowerCase();
        }
        return "unknown";
    }
}
