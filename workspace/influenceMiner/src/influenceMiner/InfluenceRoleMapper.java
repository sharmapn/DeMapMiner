package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * InfluenceRoleMapper
 *
 * Layered role resolution for a message author. Highest priority first:
 *
 *   1. Manual CSV mapping (data/role_map.csv, time-sensitive)   -> InfluenceRoleMapLoader
 *   2. Role already recorded on the message row itself
 *        allmessages.authorsrole2020 / authorsrole, values seen in peps_new:
 *        bdfl, bdfl_delegate, coredeveloper, pepeditors, proposalAuthor, otherCommunityMember
 *   3. Proposal-specific metadata from pepdetails:
 *        authorEmail / authorCorrected  -> "proposal author"
 *        bdfl_delegateCorrected         -> "BDFL delegate"
 *   4. Project-wide tables (loaded once):
 *        coredevelopers(coredeveloper, dateadded)  -> "core developer" (date-aware)
 *        pepeditors(pepeditor, dateadded)          -> "PEP editor"    (date-aware)
 *        authorandrole(author, authorsrole)        -> normalised value
 *   5. Well-known identities (Guido van Rossum -> BDFL) and keyword hints in
 *      the display name (used by the Bitcoin data: "miner", "wallet", "exchange")
 *   6. "unknown"
 *
 * Output vocabulary (Python first, Bitcoin roles kept for later portability):
 *   BDFL, steering council, BDFL delegate, proposal author, PEP editor,
 *   core developer, release manager, maintainer, BIP editor, BIP author,
 *   developer, miner, wallet provider, exchange, community member, unknown
 *
 * Steps 3 and 4 only run when the message row carries no role, because the
 * dataset's own labelling (step 2) was produced with more context than we
 * can reconstruct here. A proposal author is still promoted over
 * "community member" from step 3, since that is a per-proposal fact.
 */
public class InfluenceRoleMapper {

    public static final String ROLE_BDFL = "BDFL";
    public static final String ROLE_STEERING = "steering council";
    public static final String ROLE_DELEGATE = "BDFL delegate";
    public static final String ROLE_AUTHOR = "proposal author";
    public static final String ROLE_EDITOR = "PEP editor";
    public static final String ROLE_CORE = "core developer";
    public static final String ROLE_RELEASE = "release manager";
    public static final String ROLE_MAINTAINER = "maintainer";
    public static final String ROLE_DEVELOPER = "developer";
    public static final String ROLE_COMMUNITY = "community member";
    public static final String ROLE_UNKNOWN = "unknown";

    private static Connection loadedFor;
    private static Map<String, String> coreDevelopers;   // lower-case name -> dateadded (may be null)
    private static Map<String, String> pepEditors;       // lower-case name -> dateadded
    private static Map<String, String> authorRoles;      // lower-case name -> normalised role
    private static Map<Integer, ProposalPeople> proposalCache = new HashMap<Integer, ProposalPeople>();

    private static class ProposalPeople {
        Set<String> authorEmails = new HashSet<String>();
        Set<String> authorNames = new HashSet<String>();
        Set<String> delegateNames = new HashSet<String>();
    }

    /*
     * Backwards-compatible entry point used by the first prototype.
     */
    public static String mapRole(String authorEmail, String authorName) {
        return mapRole(null, null, 0, authorEmail, authorName, null, null);
    }

    public static String mapRole(Connection connection, String proposalIdentifier, int proposalNumber,
                                 String authorEmail, String authorName, String messageRole, String messageDate) {

        String email = authorEmail == null ? "" : authorEmail.trim().toLowerCase();
        String name = normaliseName(authorName);

        // 1. manual CSV
        String csv = InfluenceRoleMapLoader.lookup(proposalIdentifier, proposalNumber, email, name, messageDate);
        if (csv != null && csv.length() > 0) {
            return csv;
        }

        // 2. role stored on the message row
        String fromRow = normaliseDatasetRole(messageRole);

        // 3. proposal-specific metadata
        String proposalRole = null;
        if (connection != null && proposalNumber > 0) {
            ProposalPeople people = proposalPeople(connection, proposalNumber);
            if (people != null) {
                if (matchesEmail(people.authorEmails, email) || people.authorNames.contains(name)) {
                    proposalRole = ROLE_AUTHOR;
                }
                if (people.delegateNames.contains(name)) {
                    proposalRole = ROLE_DELEGATE;
                }
            }
        }

        // Proposal-specific roles win over project-wide ones, following the
        // dataset's own convention (authorsrole2020 = proposalAuthor on the
        // author's own PEP); only the BDFL / steering council keep their role.
        if (proposalRole != null) {
            if (fromRow != null && (fromRow.equals(ROLE_BDFL) || fromRow.equals(ROLE_STEERING))) {
                return fromRow;
            }
            if (fromRow != null && fromRow.equals(ROLE_DELEGATE) && proposalRole.equals(ROLE_AUTHOR)) {
                return fromRow;
            }
            return proposalRole;
        }
        if (fromRow != null) {
            return fromRow;
        }

        // 4. project-wide tables
        if (connection != null) {
            loadProjectTables(connection);
            if (name.length() > 0) {
                if (isListedAt(pepEditors, name, messageDate)) return ROLE_EDITOR;
                if (isListedAt(coreDevelopers, name, messageDate)) return ROLE_CORE;
                String r = authorRoles.get(name);
                if (r != null) return r;
            }
        }

        // 5. well-known identities / name hints
        String combined = email + " " + name;
        if (combined.contains("guido") || combined.contains("van rossum") || combined.contains("gvanrossum")) {
            return ROLE_BDFL;
        }
        if (combined.contains("steering")) return ROLE_STEERING;
        if (combined.contains("delegate")) return ROLE_DELEGATE;
        if (combined.contains("maintainer")) return ROLE_MAINTAINER;
        if (combined.contains("wallet")) return "wallet provider";
        if (combined.contains("exchange")) return "exchange";
        if (combined.matches(".*\\bminer\\b.*") || combined.matches(".*\\bmining\\b.*")) return "miner";

        return ROLE_UNKNOWN;
    }

    /*
     * Maps the labels used in allmessages.authorsrole2020 / authorandrole to
     * the Influence Miner vocabulary. Returns null for empty input.
     */
    public static String normaliseDatasetRole(String raw) {
        if (raw == null) return null;
        String r = raw.trim().toLowerCase();
        if (r.length() == 0 || r.equals("null")) return null;
        if (r.equals("bdfl")) return ROLE_BDFL;
        if (r.contains("steering")) return ROLE_STEERING;
        if (r.contains("delegate")) return ROLE_DELEGATE;
        if (r.contains("proposalauthor") || r.equals("author") || r.contains("pep author") || r.contains("bip author")) return ROLE_AUTHOR;
        if (r.contains("pepeditor")) return ROLE_EDITOR;
        if (r.contains("bipeditor") || r.contains("bip editor")) return "BIP editor";
        if (r.contains("coredeveloper") || r.contains("core developer") || r.contains("core dev")) return ROLE_CORE;
        if (r.contains("release")) return ROLE_RELEASE;
        if (r.contains("maintainer")) return ROLE_MAINTAINER;
        if (r.contains("othercommunitymember") || r.contains("community")) return ROLE_COMMUNITY;
        if (r.contains("developer")) return ROLE_DEVELOPER;
        if (r.contains("user")) return "user";
        if (r.equals("unknown")) return null;
        return raw.trim();
    }

    /*
     * Names in the database look like "From marko ristin", "Guido van Rossum",
     * "tim peters\t". Normalise to lower-case, trimmed, no "From " prefix.
     */
    public static String normaliseName(String name) {
        if (name == null) return "";
        String n = name.replace("\t", " ").trim().toLowerCase();
        if (n.startsWith("from ")) {
            n = n.substring(5).trim();
        }
        return n.replaceAll("\\s+", " ");
    }

    private static boolean matchesEmail(Set<String> emails, String email) {
        if (email == null || email.length() == 0) return false;
        if (emails.contains(email)) return true;
        // "rosuav at gmail.com" style obfuscation
        String alt = email.replace(" at ", "@");
        return emails.contains(alt);
    }

    private static boolean isListedAt(Map<String, String> table, String name, String messageDate) {
        if (table == null || !table.containsKey(name)) return false;
        String added = table.get(name);
        if (added == null || messageDate == null) return true;
        java.time.LocalDateTime a = InfluenceTemporalAnalyzer.parseDateTime(added);
        java.time.LocalDateTime m = InfluenceTemporalAnalyzer.parseDateTime(messageDate);
        if (a == null || m == null) return true;
        return !m.isBefore(a);
    }

    private static ProposalPeople proposalPeople(Connection connection, int proposalNumber) {
        Integer key = Integer.valueOf(proposalNumber);
        if (proposalCache.containsKey(key)) {
            return proposalCache.get(key);
        }
        ProposalPeople people = new ProposalPeople();
        String[] tables = { InfluenceOutcomeResolver.DETAILS_TABLE, InfluenceOutcomeResolver.DETAILS_TABLE_FALLBACK };
        for (int t = 0; t < tables.length; t++) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT author, authorCorrected, authorEmail, bdfl_delegate, bdfl_delegateCorrected FROM " + tables[t] + " WHERE pep = ?");
                ps.setInt(1, proposalNumber);
                ResultSet rs = ps.executeQuery();
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    addSplit(people.authorEmails, rs.getString("authorEmail"), true);
                    addSplit(people.authorNames, rs.getString("authorCorrected"), false);
                    addSplit(people.authorNames, rs.getString("author"), false);
                    addSplit(people.delegateNames, rs.getString("bdfl_delegateCorrected"), false);
                    addSplit(people.delegateNames, rs.getString("bdfl_delegate"), false);
                }
                rs.close();
                ps.close();
                if (any) break;
            } catch (Exception e) {
                // table missing or column missing: try the next one
            }
        }
        proposalCache.put(key, people);
        return people;
    }

    private static void addSplit(Set<String> target, String value, boolean isEmail) {
        if (value == null) return;
        String[] parts = value.split("[,;]");
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.length() == 0) continue;
            if (isEmail) {
                // strip "Name <email>" forms
                int lt = p.indexOf('<');
                int gt = p.indexOf('>');
                if (lt >= 0 && gt > lt) p = p.substring(lt + 1, gt);
                target.add(p.trim().toLowerCase().replace(" at ", "@"));
            } else {
                int lt = p.indexOf('<');
                if (lt > 0) p = p.substring(0, lt);
                target.add(normaliseName(p));
            }
        }
    }

    private static synchronized void loadProjectTables(Connection connection) {
        if (loadedFor == connection && coreDevelopers != null) {
            return;
        }
        loadedFor = connection;
        coreDevelopers = new HashMap<String, String>();
        pepEditors = new HashMap<String, String>();
        authorRoles = new HashMap<String, String>();

        loadNameDateTable(connection, "SELECT coredeveloper, dateadded FROM coredevelopers", coreDevelopers);
        loadNameDateTable(connection, "SELECT pepeditor, dateadded FROM pepeditors", pepEditors);

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT author, authorsrole FROM authorandrole");
            while (rs.next()) {
                String n = normaliseName(rs.getString(1));
                String r = normaliseDatasetRole(rs.getString(2));
                if (n.length() > 0 && r != null && !authorRoles.containsKey(n)) {
                    authorRoles.put(n, r);
                }
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            // optional table
        }
        System.out.println("Role tables loaded: " + coreDevelopers.size() + " core developers, "
                + pepEditors.size() + " PEP editors, " + authorRoles.size() + " author/role rows.");
    }

    private static void loadNameDateTable(Connection connection, String sql, Map<String, String> target) {
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String n = normaliseName(rs.getString(1));
                String d = null;
                try {
                    d = rs.getString(2);
                } catch (Exception ignored) {
                }
                if (n.length() > 0 && !target.containsKey(n)) {
                    target.put(n, d);
                }
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            // optional table
        }
    }

    public static void reset() {
        loadedFor = null;
        coreDevelopers = null;
        pepEditors = null;
        authorRoles = null;
        proposalCache.clear();
        InfluenceRoleMapLoader.reset();
    }
}
