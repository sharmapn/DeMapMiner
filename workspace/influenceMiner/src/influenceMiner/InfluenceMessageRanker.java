package influenceMiner;

import java.sql.Connection;
import java.sql.Statement;

/*
 * InfluenceMessageRanker (Sept 2026)
 *
 * Message-based ranking scheme (MBS), the counterpart of the sentence-based
 * scheme (SBS) that influence_candidates implements. Rationale Miner found that
 * ranking whole messages recovers more of the ground truth in the top ranks than
 * ranking sentences, because a message carrying several signals is read once,
 * in context. Influence Miner therefore aggregates the candidates of each
 * message into influence_message_summary:
 *
 *   candidate_count  number of candidate sentences in the message
 *   score_sum        sum of their scores       (default message score: a message
 *                                              that argues on several fronts ranks
 *                                              higher than a one-line remark)
 *   score_max        highest single score      (alternative: the strongest sentence)
 *   mechanisms       union of the mechanisms of its candidates
 *   controversial    1 if any candidate carries a controversial mechanism
 *   top_sentence     the highest-scoring candidate sentence
 *
 * The table is rebuilt from influence_candidates by one grouped query, so it is
 * always consistent with the sentence table and costs nothing at extraction time.
 * Both viewers offer "rank by message" on it.
 */
public class InfluenceMessageRanker {

    public static final String TABLE = "influence_message_summary";

    public static void ensureTable(Connection connection) throws Exception {
        Statement st = connection.createStatement();
        try {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "proposal_identifier VARCHAR(20),"
                    + "proposal_number INT,"
                    + "message_id VARCHAR(50),"
                    + "message_date DATETIME,"
                    + "author_name VARCHAR(255),"
                    + "author_role VARCHAR(100),"
                    + "candidate_count INT,"
                    + "score_sum DOUBLE,"
                    + "score_max DOUBLE,"
                    + "score_avg DOUBLE,"
                    + "mechanisms TEXT,"
                    + "controversial BOOLEAN DEFAULT 0,"
                    + "supporting_count INT DEFAULT 0,"
                    + "blocking_count INT DEFAULT 0,"
                    + "revising_count INT DEFAULT 0,"
                    + "top_sentence TEXT,"
                    + "final_decision VARCHAR(50),"
                    + "governance_era VARCHAR(30),"
                    + "decision_phase VARCHAR(30),"
                    + "INDEX idx_msg_summary_proposal (proposal_identifier, proposal_number),"
                    + "INDEX idx_msg_summary_message (message_id)"
                    + ")");
        } finally {
            st.close();
        }
    }

    /*
     * Rebuild the whole table for one proposal identifier (pep / bip).
     */
    public static int rebuild(Connection connection, String proposalIdentifier) throws Exception {
        ensureTable(connection);
        Statement st = connection.createStatement();
        try {
            st.executeUpdate("DELETE FROM " + TABLE + " WHERE proposal_identifier = '" + proposalIdentifier + "'");
            // the top sentence is the candidate with the highest score in the message
            // (ties broken by the lowest id, i.e. the earliest stored sentence)
            int n = st.executeUpdate("INSERT INTO " + TABLE + " (proposal_identifier, proposal_number, message_id, message_date, author_name, author_role, "
                    + "candidate_count, score_sum, score_max, score_avg, mechanisms, controversial, supporting_count, blocking_count, revising_count, "
                    + "top_sentence, final_decision, governance_era, decision_phase) "
                    + "SELECT c.proposal_identifier, c.proposal_number, c.message_id, MIN(c.message_date), MIN(c.author_name), MIN(c.author_role), "
                    + "COUNT(*), ROUND(SUM(c.influence_score), 2), MAX(c.influence_score), ROUND(AVG(c.influence_score), 2), "
                    + "GROUP_CONCAT(DISTINCT c.influence_types ORDER BY c.influence_types SEPARATOR ','), MAX(c.controversial), "
                    + "SUM(c.influence_direction = 'supporting'), SUM(c.influence_direction = 'blocking'), SUM(c.influence_direction = 'revising'), "
                    + "(SELECT t.sentence FROM " + InfluenceSchemaManager.CANDIDATES + " t WHERE t.proposal_identifier = c.proposal_identifier "
                    + "   AND t.proposal_number = c.proposal_number AND t.message_id = c.message_id ORDER BY t.influence_score DESC, t.id LIMIT 1), "
                    + "MIN(c.final_decision), MIN(c.governance_era), MIN(c.decision_phase) "
                    + "FROM " + InfluenceSchemaManager.CANDIDATES + " c WHERE c.proposal_identifier = '" + proposalIdentifier + "' "
                    + "GROUP BY c.proposal_identifier, c.proposal_number, c.message_id");
            // the union of comma-separated lists repeats mechanisms ("a,b,a,c"); collapse each row
            java.sql.PreparedStatement up = connection.prepareStatement("UPDATE " + TABLE + " SET mechanisms = ? WHERE id = ?");
            java.sql.ResultSet rs = st.executeQuery("SELECT id, mechanisms FROM " + TABLE + " WHERE proposal_identifier = '" + proposalIdentifier + "'");
            int k = 0;
            while (rs.next()) {
                up.setString(1, distinctMechanisms(rs.getString("mechanisms")));
                up.setInt(2, rs.getInt("id"));
                up.addBatch();
                if (++k % 1000 == 0) up.executeBatch();
            }
            up.executeBatch();
            rs.close();
            up.close();
            System.out.println(TABLE + ": " + n + " rows written for " + proposalIdentifier + ".");
            return n;
        } finally {
            st.close();
        }
    }

    /*
     * Collapse "a,b,a,c" into "a,b,c" (used by the viewers when displaying the union).
     */
    public static String distinctMechanisms(String concatenated) {
        if (concatenated == null) return "";
        java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<String>();
        for (String t : concatenated.split(",")) {
            if (t.trim().length() > 0) set.add(t.trim());
        }
        return String.join(",", set);
    }
}
