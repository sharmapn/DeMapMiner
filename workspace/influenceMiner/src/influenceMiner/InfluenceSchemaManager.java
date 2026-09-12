package influenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/*
 * InfluenceSchemaManager
 *
 * Creates the Influence Miner tables, indexes and any columns that were
 * added after the first prototype, so that a batch run works against a
 * database that only has the original influence_candidates table (or none
 * at all). Everything is idempotent: existing tables and data are kept.
 *
 * The same DDL is kept in sql/influence_candidates.sql and
 * sql/influence_summary_tables.sql for people who prefer to run it by hand.
 */
public class InfluenceSchemaManager {

    public static final String CANDIDATES = "influence_candidates";
    public static final String ACTOR_SUMMARY = "influence_actor_summary";
    public static final String PROPOSAL_SUMMARY = "influence_proposal_summary";

    /* one <mechanism>_count column per entry of InfluenceTypeDetector.TYPES, in the same order */
    public static final String[] TYPE_COUNT_COLUMNS = typeCountColumns();

    private static String[] typeCountColumns() {
        String[] cols = new String[InfluenceTypeDetector.TYPES.length];
        for (int i = 0; i < cols.length; i++) cols[i] = InfluenceTypeDetector.TYPES[i] + "_count";
        return cols;
    }

    public static void ensureSchema(Connection connection) throws SQLException {

        if (connection == null) {
            throw new SQLException("No database connection");
        }

        Statement st = connection.createStatement();
        try {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + CANDIDATES + " ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "proposal_identifier VARCHAR(20),"
                    + "proposal_number INT,"
                    + "message_id VARCHAR(100),"
                    + "author_name VARCHAR(255),"
                    + "author_email VARCHAR(255),"
                    + "author_role VARCHAR(100),"
                    + "message_date DATETIME,"
                    + "sentence TEXT,"
                    + "influence_types VARCHAR(255),"
                    + "primary_influence_type VARCHAR(50),"
                    + "influence_scope VARCHAR(50),"
                    + "influence_direction VARCHAR(50),"
                    + "influence_target VARCHAR(100),"
                    + "influence_score DOUBLE,"
                    + "final_decision VARCHAR(50),"
                    + "aligns_with_outcome BOOLEAN,"
                    + "extraction_scheme VARCHAR(30),"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            addColumnIfMissing(connection, st, CANDIDATES, "evidence_cues", "TEXT");
            addColumnIfMissing(connection, st, CANDIDATES, "decision_date", "DATETIME");
            addColumnIfMissing(connection, st, CANDIDATES, "days_before_decision", "INT");
            addColumnIfMissing(connection, st, CANDIDATES, "decision_phase", "VARCHAR(30)");
            addColumnIfMissing(connection, st, CANDIDATES, "governance_era", "VARCHAR(30)");

            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_proposal", "proposal_identifier, proposal_number");
            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_author", "author_email");
            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_type", "primary_influence_type");
            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_direction", "influence_direction");
            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_score", "influence_score");

            StringBuilder typeCols = new StringBuilder();
            for (int i = 0; i < TYPE_COUNT_COLUMNS.length; i++) {
                typeCols.append(TYPE_COUNT_COLUMNS[i]).append(" INT DEFAULT 0,");
            }

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + ACTOR_SUMMARY + " ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "proposal_identifier VARCHAR(20),"
                    + "proposal_number INT,"
                    + "author_name VARCHAR(255),"
                    + "author_email VARCHAR(255),"
                    + "author_role VARCHAR(100),"
                    + "total_influence_sentences INT,"
                    + typeCols
                    + "supporting_count INT DEFAULT 0,"
                    + "blocking_count INT DEFAULT 0,"
                    + "revising_count INT DEFAULT 0,"
                    + "neutral_count INT DEFAULT 0,"
                    + "average_score DOUBLE,"
                    + "max_score DOUBLE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + PROPOSAL_SUMMARY + " ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "proposal_identifier VARCHAR(20),"
                    + "proposal_number INT,"
                    + "final_decision VARCHAR(50),"
                    + "total_influence_sentences INT,"
                    + "total_actors INT,"
                    + typeCols
                    + "supporting_count INT DEFAULT 0,"
                    + "blocking_count INT DEFAULT 0,"
                    + "revising_count INT DEFAULT 0,"
                    + "neutral_count INT DEFAULT 0,"
                    + "internal_count INT DEFAULT 0,"
                    + "external_count INT DEFAULT 0,"
                    + "mixed_count INT DEFAULT 0,"
                    + "unknown_scope_count INT DEFAULT 0,"
                    + "average_score DOUBLE,"
                    + "max_score DOUBLE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            // summary tables created by an earlier version lack the controversial-mechanism columns
            for (int i = 0; i < TYPE_COUNT_COLUMNS.length; i++) {
                addColumnIfMissing(connection, st, ACTOR_SUMMARY, TYPE_COUNT_COLUMNS[i], "INT DEFAULT 0");
                addColumnIfMissing(connection, st, PROPOSAL_SUMMARY, TYPE_COUNT_COLUMNS[i], "INT DEFAULT 0");
            }
            addColumnIfMissing(connection, st, CANDIDATES, "controversial", "BOOLEAN DEFAULT 0");
            addIndexIfMissing(connection, st, CANDIDATES, "idx_influence_controversial", "controversial");

            addIndexIfMissing(connection, st, ACTOR_SUMMARY, "idx_actor_summary_proposal", "proposal_identifier, proposal_number");
            addIndexIfMissing(connection, st, PROPOSAL_SUMMARY, "idx_proposal_summary_proposal", "proposal_identifier, proposal_number");
        } finally {
            st.close();
        }
    }

    public static boolean tableExists(Connection connection, String table) {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SHOW TABLES LIKE '" + table + "'");
            boolean exists = rs.next();
            rs.close();
            st.close();
            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    public static Set<String> columns(Connection connection, String table) throws SQLException {
        Set<String> cols = new HashSet<String>();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SHOW COLUMNS FROM " + table);
        while (rs.next()) {
            cols.add(rs.getString("Field").toLowerCase());
        }
        rs.close();
        st.close();
        return cols;
    }

    private static void addColumnIfMissing(Connection connection, Statement st, String table, String column, String type) throws SQLException {
        if (!columns(connection, table).contains(column.toLowerCase())) {
            st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
            System.out.println("Added column " + table + "." + column);
        }
    }

    private static void addIndexIfMissing(Connection connection, Statement st, String table, String index, String columns) throws SQLException {
        ResultSet rs = st.executeQuery("SHOW INDEX FROM " + table + " WHERE Key_name = '" + index + "'");
        boolean exists = rs.next();
        rs.close();
        if (!exists) {
            st.executeUpdate("CREATE INDEX " + index + " ON " + table + " (" + columns + ")");
            System.out.println("Created index " + index + " on " + table);
        }
    }

    public static void clearCandidates(Connection connection, String proposalIdentifier, Integer proposalNumber) throws SQLException {
        String sql = "DELETE FROM " + CANDIDATES + " WHERE 1=1";
        if (proposalIdentifier != null) sql += " AND LOWER(proposal_identifier) = '" + proposalIdentifier.toLowerCase().replace("'", "''") + "'";
        if (proposalNumber != null) sql += " AND proposal_number = " + proposalNumber.intValue();
        Statement st = connection.createStatement();
        int n = st.executeUpdate(sql);
        st.close();
        System.out.println("Cleared " + n + " previous rows from " + CANDIDATES
                + (proposalNumber != null ? " for proposal " + proposalNumber : ""));
    }
}
