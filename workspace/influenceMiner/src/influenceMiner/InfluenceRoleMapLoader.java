package influenceMiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * InfluenceRoleMapLoader
 *
 * Loads optional manual role mappings from data/role_map.csv:
 *
 *   project,proposal_identifier,proposal_number,author_email,author_name,role,start_date,end_date,source
 *   python,pep,572,guido@python.org,Guido van Rossum,BDFL,,,manual
 *   python,,,nad@python.org,Ned Deily,release manager,2014-01-01,2024-12-31,manual
 *
 * Matching rules:
 *   - proposal_number empty  -> applies to every proposal of that identifier
 *   - proposal_identifier empty -> applies to every project
 *   - author_email OR author_name may be empty; whichever is present must match
 *     (case-insensitive, whitespace-trimmed)
 *   - start_date/end_date (yyyy-MM-dd) bound the validity of the role; empty = open
 *
 * The file is optional. When it is absent the mapper relies on the database.
 */
public class InfluenceRoleMapLoader {

    public static final String CSV_FILE = "role_map.csv";

    public static class RoleMapping {
        public String project;
        public String proposalIdentifier;
        public Integer proposalNumber;
        public String authorEmail;
        public String authorName;
        public String role;
        public LocalDate startDate;
        public LocalDate endDate;
        public String source;

        boolean matches(String proposalIdentifier, int proposalNumber, String email, String name, LocalDateTime when) {
            if (this.proposalIdentifier != null && this.proposalIdentifier.length() > 0
                    && (proposalIdentifier == null || !this.proposalIdentifier.equalsIgnoreCase(proposalIdentifier.trim()))) {
                return false;
            }
            if (this.proposalNumber != null && this.proposalNumber.intValue() != proposalNumber) {
                return false;
            }
            boolean emailGiven = this.authorEmail != null && this.authorEmail.length() > 0;
            boolean nameGiven = this.authorName != null && this.authorName.length() > 0;
            if (!emailGiven && !nameGiven) {
                return false;
            }
            if (emailGiven && (email == null || !this.authorEmail.equalsIgnoreCase(email.trim()))) {
                return false;
            }
            if (nameGiven && (name == null || !this.authorName.equalsIgnoreCase(InfluenceRoleMapper.normaliseName(name)))) {
                return false;
            }
            if (when != null) {
                LocalDate d = when.toLocalDate();
                if (startDate != null && d.isBefore(startDate)) return false;
                if (endDate != null && d.isAfter(endDate)) return false;
            }
            return true;
        }
    }

    private static List<RoleMapping> mappings;

    public static List<RoleMapping> load() {
        if (mappings != null) {
            return mappings;
        }
        mappings = new ArrayList<RoleMapping>();
        File f = new File(InfluenceConfig.dataDir(), CSV_FILE);
        if (!f.exists()) {
            return mappings;
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
                String[] c = InfluenceCsvExporter.parseCsvLine(line);
                if (c.length < 6) continue;
                RoleMapping m = new RoleMapping();
                m.project = c[0].trim().toLowerCase();
                m.proposalIdentifier = c[1].trim().toLowerCase();
                m.proposalNumber = c[2].trim().length() == 0 ? null : Integer.valueOf(c[2].trim());
                m.authorEmail = c[3].trim().toLowerCase();
                m.authorName = InfluenceRoleMapper.normaliseName(c[4]);
                m.role = c[5].trim();
                m.startDate = c.length > 6 ? parseDate(c[6]) : null;
                m.endDate = c.length > 7 ? parseDate(c[7]) : null;
                m.source = c.length > 8 ? c[8].trim() : "";
                if (m.role.length() > 0) {
                    mappings.add(m);
                }
            }
            System.out.println("Loaded " + mappings.size() + " role mappings from " + f);
        } catch (Exception e) {
            System.err.println("Could not read " + f + ": " + e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
            }
        }
        return mappings;
    }

    public static String lookup(String proposalIdentifier, int proposalNumber, String email, String name, String messageDate) {
        List<RoleMapping> list = load();
        if (list.isEmpty()) {
            return null;
        }
        LocalDateTime when = InfluenceTemporalAnalyzer.parseDateTime(messageDate);
        for (int i = 0; i < list.size(); i++) {
            RoleMapping m = list.get(i);
            if (m.matches(proposalIdentifier, proposalNumber, email, name, when)) {
                return m.role;
            }
        }
        return null;
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.trim().length() == 0) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static void reset() {
        mappings = null;
    }
}
