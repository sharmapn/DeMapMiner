package influenceMiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * InfluenceOutcomeResolver
 *
 * Finds the real outcome of a proposal instead of the old hard-coded
 * "accepted". Resolution order:
 *
 *   1. accrejpeps                         - the 248 PEPs whose final state was
 *                                           ACCEPTED or REJECTED before the 2018
 *                                           governance change (PEP, state, date2).
 *   2. pepstates_danieldata_datetimestamp - full state history mined from the
 *                                           PEP git log (PEP, state, dateTimeStamp).
 *                                           The table name comes from the
 *                                           proposalStateTableName property in
 *                                           DeMaP Miner; this default matches it.
 *   3. data/proposal_outcomes.csv         - manual fallback:
 *                                           proposal_identifier,proposal_number,title,status,decision_date,source
 *   4. unknown
 *
 * Creation date is read from pepdetails (falling back to proposaldetails).
 *
 * DATABASE ASSUMPTIONS (verified against peps_new on 2026-09-12):
 *   accrejpeps(PEP int, state longtext, date2 date)
 *   pepstates_danieldata_datetimestamp(PEP int, state longtext, dateTimeStamp timestamp, ...)
 *   pepdetails(pep int, created date, ...)
 * If a table is missing the resolver just moves to the next source.
 *
 * Status values are normalised to:
 *   accepted, rejected, withdrawn, deferred, final, active, superseded,
 *   draft, provisional, unknown
 */
public class InfluenceOutcomeResolver {

    public static final String[] NORMALISED_STATUSES = {
            "accepted", "rejected", "withdrawn", "deferred", "final", "active",
            "superseded", "draft", "provisional", "unknown"
    };

    public static final String ACCREJ_TABLE = "accrejpeps";
    public static final String DEFAULT_STATE_TABLE = "pepstates_danieldata_datetimestamp";
    /* The DeMaP Miner prop key proposalStateTableName (or -Dinfluence.stateTable) selects the
       state-history table, e.g. pepstates_github for the 2026 corpus. */
    public static String stateTable() {
        String v = System.getProperty("influence.stateTable");
        if (v == null || v.trim().isEmpty()) {
            try { v = connections.PropertiesFile.readFromPropertiesFile("proposalStateTableName", true); } catch (Exception e) { v = null; }
        }
        return v == null || v.trim().isEmpty() ? DEFAULT_STATE_TABLE : v.trim();
    }
    public static final String DETAILS_TABLE = "pepdetails";
    public static final String DETAILS_TABLE_FALLBACK = "proposaldetails";
    public static final String CSV_FILE = "proposal_outcomes.csv";

    private static final Map<String, InfluenceOutcome> cache = new HashMap<String, InfluenceOutcome>();
    private static Map<String, InfluenceOutcome> csvOutcomes;

    public static InfluenceOutcome resolveOutcome(Connection connection, String proposalIdentifier, int proposalNumber) {

        String key = (proposalIdentifier == null ? "" : proposalIdentifier.toLowerCase()) + ":" + proposalNumber;
        InfluenceOutcome cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        InfluenceOutcome outcome = new InfluenceOutcome();

        if (connection != null) {
            try {
                resolveFromAccRej(connection, proposalNumber, outcome);
            } catch (Exception e) {
                // table may not exist in this database; fall through
            }
            try {
                resolveFromStates(connection, proposalNumber, outcome);
            } catch (Exception e) {
                // ignore and fall through
            }
            try {
                resolveCreatedDate(connection, proposalNumber, outcome);
            } catch (Exception e) {
                // ignore
            }
        }

        if (!outcome.isKnown()) {
            InfluenceOutcome fromCsv = csvOutcome(proposalIdentifier, proposalNumber);
            if (fromCsv != null) {
                outcome.finalDecision = fromCsv.finalDecision;
                outcome.decisionDate = fromCsv.decisionDate;
                outcome.source = "csv";
                if (outcome.firstDecision == null) {
                    outcome.firstDecision = fromCsv.finalDecision;
                    outcome.firstDecisionDate = fromCsv.decisionDate;
                }
            }
        }

        cache.put(key, outcome);
        return outcome;
    }

    /*
     * accrejpeps gives the final accepted/rejected decision. When a PEP has
     * several rows (PEP 308: rejected 2003, accepted 2005) the latest row is
     * the final decision and the earliest is the first decision.
     */
    private static void resolveFromAccRej(Connection connection, int proposalNumber, InfluenceOutcome outcome) throws Exception {

        String sql = "SELECT state, date2 FROM " + ACCREJ_TABLE + " WHERE PEP = ? ORDER BY date2 ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, proposalNumber);
        ResultSet rs = ps.executeQuery();

        String first = null, firstDate = null, last = null, lastDate = null;
        while (rs.next()) {
            String state = normalise(rs.getString("state"));
            String date = rs.getString("date2");
            if (first == null) {
                first = state;
                firstDate = date;
            }
            last = state;
            lastDate = date;
        }
        rs.close();
        ps.close();

        if (last != null && !last.equals("unknown")) {
            outcome.finalDecision = last;
            outcome.decisionDate = lastDate;
            outcome.firstDecision = first;
            outcome.firstDecisionDate = firstDate;
            outcome.source = ACCREJ_TABLE;
        }
    }

    /*
     * The state-history table covers many more PEPs (final, active, deferred,
     * withdrawn, superseded, draft ...). It is used when accrejpeps has no
     * row, and it always supplies the first accepted/rejected date when known.
     */
    private static void resolveFromStates(Connection connection, int proposalNumber, InfluenceOutcome outcome) throws Exception {

        String sql = "SELECT state, dateTimeStamp FROM " + stateTable() + " WHERE PEP = ? ORDER BY dateTimeStamp ASC, id ASC";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, proposalNumber);
        ResultSet rs = ps.executeQuery();

        List<String[]> history = new ArrayList<String[]>();
        while (rs.next()) {
            String state = normalise(rs.getString("state"));
            String date = null;
            try {
                Timestamp ts = rs.getTimestamp("dateTimeStamp");
                if (ts != null) {
                    date = ts.toString();
                    if (date.endsWith(".0")) {
                        date = date.substring(0, date.length() - 2);
                    }
                }
            } catch (Exception e) {
                date = rs.getString("dateTimeStamp");
            }
            if (!state.equals("unknown")) {
                history.add(new String[] { state, date });
            }
        }
        rs.close();
        ps.close();

        if (history.isEmpty()) {
            return;
        }

        String[] firstDecision = null;
        String[] lastDecision = null;
        String[] lastTerminal = null;
        for (int i = 0; i < history.size(); i++) {
            String[] h = history.get(i);
            if (h[0].equals("accepted") || h[0].equals("rejected")) {
                if (firstDecision == null) firstDecision = h;
                lastDecision = h;
            }
            if (isTerminal(h[0])) {
                lastTerminal = h;
            }
        }

        if (outcome.firstDecision == null && firstDecision != null) {
            outcome.firstDecision = firstDecision[0];
            outcome.firstDecisionDate = firstDecision[1];
        }

        if (outcome.isKnown()) {
            // accrejpeps already gave the decision; prefer its (date-only) value but
            // upgrade to the timestamped version from the state history when it agrees.
            if (lastDecision != null && lastDecision[0].equals(outcome.finalDecision) && lastDecision[1] != null) {
                outcome.decisionDate = lastDecision[1];
            }
            return;
        }

        String[] last = history.get(history.size() - 1);
        outcome.finalDecision = last[0];
        outcome.source = stateTable();

        if (lastDecision != null) {
            outcome.decisionDate = lastDecision[1];
        } else if (lastTerminal != null) {
            outcome.decisionDate = lastTerminal[1];
        } else {
            outcome.decisionDate = null; // draft/active: no decision moment yet
        }
    }

    private static void resolveCreatedDate(Connection connection, int proposalNumber, InfluenceOutcome outcome) throws Exception {
        String[] tables = { DETAILS_TABLE, DETAILS_TABLE_FALLBACK };
        for (int t = 0; t < tables.length; t++) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT created FROM " + tables[t] + " WHERE pep = ? AND created IS NOT NULL ORDER BY created ASC LIMIT 1");
                ps.setInt(1, proposalNumber);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    outcome.createdDate = rs.getString("created");
                }
                rs.close();
                ps.close();
                if (outcome.createdDate != null) {
                    return;
                }
            } catch (Exception e) {
                // try next table
            }
        }
    }

    /*
     * Maps the raw status strings found in the database / PEP headers to the
     * normalised vocabulary used across Influence Miner.
     */
    public static String normalise(String rawState) {

        if (rawState == null) {
            return "unknown";
        }
        String s = rawState.trim().toLowerCase();
        if (s.startsWith("status")) {
            int idx = s.indexOf(':');
            if (idx >= 0) s = s.substring(idx + 1).trim();
        }

        if (s.startsWith("accept") || s.startsWith("approv")) return "accepted";
        if (s.startsWith("reject")) return "rejected";
        if (s.startsWith("withdraw")) return "withdrawn";
        if (s.startsWith("defer") || s.startsWith("postpone")) return "deferred";
        if (s.startsWith("final") || s.startsWith("finish") || s.startsWith("complete")) return "final";
        if (s.startsWith("active")) return "active";
        if (s.startsWith("supersed") || s.startsWith("replace")) return "superseded";
        if (s.startsWith("draft") || s.startsWith("incomplete")) return "draft";
        if (s.startsWith("provisional")) return "provisional";
        return "unknown";
    }

    public static boolean isTerminal(String normalisedStatus) {
        return normalisedStatus.equals("accepted") || normalisedStatus.equals("rejected")
                || normalisedStatus.equals("withdrawn") || normalisedStatus.equals("deferred")
                || normalisedStatus.equals("final") || normalisedStatus.equals("superseded");
    }

    /*
     * Coarse grouping used by RQ4: decided-positive vs decided-negative vs undecided.
     */
    public static String outcomeGroup(String normalisedStatus) {
        if (normalisedStatus == null) return "unknown";
        if (normalisedStatus.equals("accepted") || normalisedStatus.equals("final") || normalisedStatus.equals("active")) {
            return "positive";
        }
        if (normalisedStatus.equals("rejected") || normalisedStatus.equals("withdrawn")) {
            return "negative";
        }
        if (normalisedStatus.equals("deferred") || normalisedStatus.equals("superseded")
                || normalisedStatus.equals("draft") || normalisedStatus.equals("provisional")) {
            return "undecided";
        }
        return "unknown";
    }

    private static InfluenceOutcome csvOutcome(String proposalIdentifier, int proposalNumber) {
        if (csvOutcomes == null) {
            csvOutcomes = loadCsv();
        }
        String id = proposalIdentifier == null ? "" : proposalIdentifier.trim().toLowerCase();
        InfluenceOutcome o = csvOutcomes.get(id + ":" + proposalNumber);
        if (o == null) {
            o = csvOutcomes.get(":" + proposalNumber);
        }
        return o;
    }

    private static Map<String, InfluenceOutcome> loadCsv() {
        Map<String, InfluenceOutcome> map = new HashMap<String, InfluenceOutcome>();
        File f = new File(InfluenceConfig.dataDir(), CSV_FILE);
        if (!f.exists()) {
            return map;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.trim().length() == 0 || line.startsWith("#")) continue;
                String[] cols = InfluenceCsvExporter.parseCsvLine(line);
                if (cols.length < 4) continue;
                String id = cols[0].trim().toLowerCase();
                int num;
                try {
                    num = Integer.parseInt(cols[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                InfluenceOutcome o = new InfluenceOutcome(normalise(cols[3]), cols.length > 4 && cols[4].trim().length() > 0 ? cols[4].trim() : null);
                o.source = "csv";
                map.put(id + ":" + num, o);
            }
        } catch (Exception e) {
            System.err.println("Could not read " + f + ": " + e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
            }
        }
        return map;
    }

    public static void clearCache() {
        cache.clear();
        csvOutcomes = null;
    }
}
