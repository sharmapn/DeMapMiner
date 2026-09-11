package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * InfluenceMessageSource
 *
 * Reads proposal-linked messages from the DeMaP Miner message table without
 * assuming exact column names. The prototype queried columns
 * (proposalNumber, fromName, fromEmail, body, date) that do not exist in the
 * real schema, so the columns are discovered with SHOW COLUMNS and mapped:
 *
 *   proposal number : PEP | pepnum2021 | pepnum2020 | proposalNumber | proposal_number
 *   message id      : messageID | id
 *   date            : dateTimeStamp | date2 | date | message_date
 *   body            : analyseWords (headers/quotes already stripped by DeMaP Miner)
 *                     falling back to email (raw) when analyseWords is empty
 *   author name     : clusterBySenderFullName | senderFullName | senderName | Author
 *   author email    : senderemail | from | senderEmail
 *   role            : authorsrole2020 | authorsrole
 *   subject         : subject
 *   proposal type   : peptype2020 | pepType
 *
 * DATABASE ASSUMPTION (peps_new, verified 2026-09-12): table allmessages has
 * 1,589,676 rows of which 109,083 carry a PEP number; 523 distinct PEPs.
 */
public class InfluenceMessageSource {

    public static final String MESSAGE_TABLE = "allmessages";

    public static class Message {
        public String messageId;
        public String messageDate;
        public String subject;
        public String body;
        public String authorName;
        public String authorEmail;
        public String authorRole;
        public String proposalType;
        /* raw senderFullName; "From xxx" marks rows whose name/role columns are shifted (see InfluenceAuthorResolver) */
        public String senderFullName;
        /* mailing-list folder (allmessages.lastdir), used to skip commit/tracker lists */
        public String mailingList;
    }

    public static class Columns {
        public String proposal, messageId, date, body, bodyFallback, name, email, role, subject, proposalType, senderFullName, mailingList;

        static Columns discover(Connection connection, String table) throws Exception {
            Map<String, String> actual = new HashMap<String, String>();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SHOW COLUMNS FROM " + table);
            while (rs.next()) {
                String f = rs.getString("Field");
                actual.put(f.toLowerCase(), f);
            }
            rs.close();
            st.close();

            Columns c = new Columns();
            c.proposal = first(actual, "pep", "pepnum2021", "pepnum2020", "proposalnumber", "proposal_number", "bip");
            c.messageId = first(actual, "messageid", "message_id", "id");
            c.date = first(actual, "datetimestamp", "date2", "date", "message_date");
            c.body = first(actual, "analysewords", "body", "message", "text", "email");
            c.bodyFallback = first(actual, "email", "line", "body");
            c.name = first(actual, "clusterbysenderfullname", "senderfullname", "sendername", "author", "fromname", "author_name");
            c.email = first(actual, "senderemail", "fromemail", "from", "author_email");
            c.role = first(actual, "authorsrole2020", "authorsrole", "author_role", "role");
            c.subject = first(actual, "subject", "processedsubject", "title");
            c.proposalType = first(actual, "peptype2020", "peptype", "proposal_type", "type");
            c.senderFullName = first(actual, "senderfullname");
            c.mailingList = first(actual, "lastdir", "mailing_list", "listname", "folder");

            if (c.proposal == null) throw new Exception("No proposal-number column found in " + table);
            if (c.messageId == null) throw new Exception("No message-id column found in " + table);
            if (c.body == null) throw new Exception("No body column found in " + table);
            return c;
        }

        private static String first(Map<String, String> actual, String... names) {
            for (int i = 0; i < names.length; i++) {
                String f = actual.get(names[i].toLowerCase());
                if (f != null) return f;
            }
            return null;
        }
    }

    private static Columns cachedColumns;
    private static Connection cachedFor;

    public static Columns columns(Connection connection) throws Exception {
        if (cachedColumns == null || cachedFor != connection) {
            cachedColumns = Columns.discover(connection, MESSAGE_TABLE);
            cachedFor = connection;
            System.out.println("Message table columns: proposal=" + cachedColumns.proposal + ", id=" + cachedColumns.messageId
                    + ", date=" + cachedColumns.date + ", body=" + cachedColumns.body + " (fallback " + cachedColumns.bodyFallback
                    + "), name=" + cachedColumns.name + ", email=" + cachedColumns.email + ", role=" + cachedColumns.role);
        }
        return cachedColumns;
    }

    /*
     * Loads every message of one proposal into memory (ordered by date) and
     * closes the ResultSet before returning, so callers can run further SQL
     * while iterating.
     */
    public static List<Message> loadMessages(Connection connection, int proposalNumber) throws Exception {

        Columns c = columns(connection);
        List<Message> messages = new ArrayList<Message>();
        java.util.Set<String> seenIds = new java.util.HashSet<String>();

        String order = c.date != null ? "`" + c.date + "`, `" + c.messageId + "`" : "`" + c.messageId + "`";
        String sql = "SELECT * FROM " + MESSAGE_TABLE + " WHERE `" + c.proposal + "` = ? ORDER BY " + order;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, proposalNumber);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Message m = new Message();
            m.messageId = get(rs, c.messageId);
            m.messageDate = get(rs, c.date);
            m.subject = get(rs, c.subject);
            m.body = get(rs, c.body);
            if ((m.body == null || m.body.trim().length() == 0) && c.bodyFallback != null && !c.bodyFallback.equals(c.body)) {
                m.body = get(rs, c.bodyFallback);
            }
            m.authorName = get(rs, c.name);
            m.authorEmail = get(rs, c.email);
            m.authorRole = get(rs, c.role);
            m.proposalType = get(rs, c.proposalType);
            m.senderFullName = get(rs, c.senderFullName);
            m.mailingList = get(rs, c.mailingList);
            // allmessages contains duplicate messageIDs (same message imported twice); keep the first
            if (m.messageId != null && !seenIds.add(m.messageId)) {
                continue;
            }
            messages.add(m);
        }
        rs.close();
        ps.close();
        return messages;
    }

    /*
     * Distinct proposal numbers that have at least one message.
     */
    public static List<Integer> listProposalNumbers(Connection connection, Integer from, Integer to) throws Exception {
        Columns c = columns(connection);
        List<Integer> numbers = new ArrayList<Integer>();
        String sql = "SELECT DISTINCT `" + c.proposal + "` AS p FROM " + MESSAGE_TABLE
                + " WHERE `" + c.proposal + "` IS NOT NULL AND `" + c.proposal + "` > 0";
        if (from != null) sql += " AND `" + c.proposal + "` >= " + from.intValue();
        if (to != null) sql += " AND `" + c.proposal + "` <= " + to.intValue();
        sql += " ORDER BY p";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            numbers.add(Integer.valueOf(rs.getInt("p")));
        }
        rs.close();
        st.close();
        return numbers;
    }

    private static String get(ResultSet rs, String column) {
        if (column == null) return null;
        try {
            return rs.getString(column);
        } catch (Exception e) {
            return null;
        }
    }
}
