package influenceMiner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * InfluenceAuthorResolver
 *
 * Works around a data-quality problem in peps_new.allmessages (verified
 * against the raw message headers on 2026-09-12):
 *
 * For the rows imported by the newer loader (recognisable by senderFullName
 * starting with "From ", mostly 2017-2018 messages, i.e. PEP 572's whole
 * discussion) the columns senderemail and senderFullName are shifted by one
 * row - they belong to the NEXT message. clusterBySenderFullName, senderName
 * and authorsrole2020 are consistent with the raw "From:" header that is
 * stored with the body in the email column. Example (PEP 572):
 *
 *   id       senderemail            cluster         raw header
 *   8182634  chris.jerdonek@...     Chris Angelico  rosuav at gmail.com (Chris Angelico)
 *   8182635  rosuav@gmail.com       Chris Jerdonek  chris.jerdonek at gmail.com (Chris Jerdonek)
 *   8182636  ncoghlan@gmail.com     Chris Angelico  rosuav at gmail.com (Chris Angelico)
 *
 * Over all 10,831 "From"-style PEP-linked rows the raw header agrees with the
 * cluster name 9,532 times (88%) and with senderemail 3,155 times (29%).
 *
 * Strategy (no changes to the dataset):
 *   - for reliable rows use the columns as they are
 *   - for shifted rows keep clusterBySenderFullName and authorsrole2020, and
 *     take the address (and, if the cluster name is empty, the display name)
 *     from the raw "From:" header when it is present in the body column;
 *     otherwise the address is left empty
 *   - actor identity downstream is the cluster name (InfluenceCandidateRepository)
 *   - duplicate messageIDs (109,083 PEP-linked rows but 91,562 distinct ids)
 *     are collapsed by InfluenceMessageSource
 */
public class InfluenceAuthorResolver {

    public static class Identity {
        public String email;
        public String name;
        public String datasetRole;   // normalised by InfluenceRoleMapper later; may be null
        public boolean rowUnreliable;
        public boolean emailRecovered;
    }

    /* "From: rosuav at gmail.com (Chris Angelico)" or "From: Chris Angelico <rosuav@gmail.com>" */
    private static final Pattern HEADER_AT = Pattern.compile("(?im)^From:\\s*([^\\s<(]+)\\s+at\\s+([^\\s<(]+)(?:\\s*\\(([^)]*)\\))?");
    private static final Pattern HEADER_ANGLE = Pattern.compile("(?im)^From:\\s*(?:\"?([^\"<]*?)\"?\\s*)?<([^>\\s]+@[^>\\s]+)>");
    private static final Pattern HEADER_PLAIN = Pattern.compile("(?im)^From:\\s*([^\\s<(]+@[^\\s<(]+)(?:\\s*\\(([^)]*)\\))?");

    public static boolean isUnreliableRow(String senderFullName) {
        return senderFullName != null && senderFullName.trim().toLowerCase().startsWith("from ");
    }

    public static Identity resolve(InfluenceMessageSource.Message m) {

        Identity id = new Identity();
        id.rowUnreliable = isUnreliableRow(m.senderFullName);

        if (!id.rowUnreliable) {
            id.email = m.authorEmail == null ? "" : m.authorEmail.trim().toLowerCase();
            id.name = m.authorName;
            id.datasetRole = m.authorRole;
            if (id.name == null || id.name.trim().length() == 0) {
                id.name = localPart(m.senderFullName, id.email);
            }
            return id;
        }

        // shifted row: cluster name and role are right, the address is not
        id.name = m.authorName;
        id.datasetRole = m.authorRole;
        id.email = "";

        String[] header = parseFromHeader(m.rawHeader);
        if (header != null) {
            id.email = header[0];
            id.emailRecovered = true;
            if ((id.name == null || id.name.trim().length() == 0) && header[1] != null && header[1].trim().length() > 0) {
                id.name = header[1].trim();
            }
        }
        if (id.name == null || id.name.trim().length() == 0) {
            id.name = id.email.length() > 0 ? id.email.substring(0, id.email.indexOf('@')) : "";
        }
        return id;
    }

    /*
     * Returns {email, displayName} parsed from the first "From:" header line
     * in the raw message text, or null when there is none.
     */
    public static String[] parseFromHeader(String rawHeader) {
        if (rawHeader == null || rawHeader.length() == 0) {
            return null;
        }
        Matcher m = HEADER_AT.matcher(rawHeader);
        if (m.find()) {
            return new String[] { (m.group(1) + "@" + m.group(2)).toLowerCase(), m.group(3) };
        }
        m = HEADER_ANGLE.matcher(rawHeader);
        if (m.find()) {
            return new String[] { m.group(2).toLowerCase(), m.group(1) };
        }
        m = HEADER_PLAIN.matcher(rawHeader);
        if (m.find()) {
            return new String[] { m.group(1).toLowerCase(), m.group(2) };
        }
        return null;
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
}
