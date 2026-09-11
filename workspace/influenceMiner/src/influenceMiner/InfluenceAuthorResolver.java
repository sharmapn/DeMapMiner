package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/*
 * InfluenceAuthorResolver
 *
 * Works around a data-quality problem found in peps_new.allmessages on
 * 2026-09-12: for the rows imported by the newer loader (recognisable by
 * senderFullName starting with "From ", mostly 2017-2018 messages, i.e.
 * PEP 572's whole discussion) the columns clusterBySenderFullName,
 * senderName and authorsrole2020 are shifted by one row - they describe
 * the sender of the PREVIOUS message. senderemail is correct in all rows.
 * Example (PEP 572):
 *   8182634 chris.jerdonek@gmail.com  cluster="Chris Angelico"
 *   8182635 rosuav@gmail.com          cluster="Chris Jerdonek"   <- Chris Angelico
 *   8182636 ncoghlan@gmail.com        cluster="Chris Angelico"   <- Nick Coghlan
 * For guido@python.org: 3828/3836 old-style rows carry the right name,
 * only 76/465 "From "-style rows do.
 *
 * Strategy (no changes to the dataset):
 *   - identity key is always the e-mail address
 *   - for unreliable rows, the display name is the majority
 *     clusterBySenderFullName of the RELIABLE rows with the same e-mail,
 *     falling back to the e-mail local part ("From rosuav" -> "rosuav")
 *   - for unreliable rows, the dataset role is the most recent
 *     non-proposal-specific authorsrole2020 of the reliable rows with the
 *     same e-mail (bdfl / coredeveloper / pepeditors / otherCommunityMember);
 *     proposal-specific roles (proposal author, BDFL delegate) are then
 *     re-derived by InfluenceRoleMapper from pepdetails
 *   - duplicate messageIDs (109,083 PEP-linked rows but 91,562 distinct
 *     ids) are collapsed by InfluenceMessageSource
 *
 * Lookups are cached per e-mail; both queries use the senderemail index.
 */
public class InfluenceAuthorResolver {

    public static class Identity {
        public String email;
        public String name;
        public String datasetRole;   // normalised by InfluenceRoleMapper later; may be null
        public boolean rowUnreliable;
        public boolean nameRecovered;
    }

    private static final Map<String, String> nameCache = new HashMap<String, String>();
    private static final Map<String, String> roleCache = new HashMap<String, String>();

    public static boolean isUnreliableRow(String senderFullName) {
        return senderFullName != null && senderFullName.trim().toLowerCase().startsWith("from ");
    }

    public static Identity resolve(Connection connection, InfluenceMessageSource.Message m) {

        Identity id = new Identity();
        id.email = m.authorEmail == null ? "" : m.authorEmail.trim().toLowerCase();
        id.rowUnreliable = isUnreliableRow(m.senderFullName);

        if (!id.rowUnreliable) {
            id.name = m.authorName;
            id.datasetRole = m.authorRole;
            if (id.name == null || id.name.trim().length() == 0) {
                id.name = localPart(m.senderFullName, id.email);
            }
            return id;
        }

        String recovered = id.email.length() > 0 ? canonicalName(connection, id.email) : null;
        if (recovered != null) {
            id.name = recovered;
            id.nameRecovered = true;
        } else {
            id.name = localPart(m.senderFullName, id.email);
        }
        id.datasetRole = id.email.length() > 0 ? reliableRole(connection, id.email) : null;
        return id;
    }

    public static String canonicalName(Connection connection, String email) {
        if (nameCache.containsKey(email)) {
            return nameCache.get(email);
        }
        String name = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT clusterBySenderFullName, COUNT(*) c FROM " + InfluenceMessageSource.MESSAGE_TABLE
                            + " WHERE senderemail = ? AND senderFullName NOT LIKE 'From %' AND clusterBySenderFullName IS NOT NULL"
                            + " AND clusterBySenderFullName <> '' GROUP BY clusterBySenderFullName ORDER BY c DESC LIMIT 1");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            // column missing in another schema: leave null
        }
        nameCache.put(email, name);
        return name;
    }

    public static String reliableRole(Connection connection, String email) {
        if (roleCache.containsKey(email)) {
            return roleCache.get(email);
        }
        String role = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT authorsrole2020 FROM " + InfluenceMessageSource.MESSAGE_TABLE
                            + " WHERE senderemail = ? AND senderFullName NOT LIKE 'From %' AND authorsrole2020 IS NOT NULL"
                            + " AND authorsrole2020 NOT IN ('proposalAuthor','bdfl_delegate')"
                            + " ORDER BY dateTimeStamp DESC LIMIT 1");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                role = rs.getString(1);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            // leave null
        }
        roleCache.put(email, role);
        return role;
    }

    /*
     * "From p f moore" -> "p f moore"; if that is empty use the e-mail local part.
     */
    public static String localPart(String senderFullName, String email) {
        String n = senderFullName == null ? "" : senderFullName.trim();
        if (n.toLowerCase().startsWith("from ")) {
            n = n.substring(5).trim();
        }
        if (n.length() == 0 && email != null && email.indexOf('@') > 0) {
            n = email.substring(0, email.indexOf('@'));
        }
        return n;
    }

    public static void reset() {
        nameCache.clear();
        roleCache.clear();
    }
}
