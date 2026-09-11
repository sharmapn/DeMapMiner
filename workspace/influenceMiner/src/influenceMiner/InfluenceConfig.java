package influenceMiner;

import java.io.File;
import java.time.LocalDate;

import connections.PropertiesFile;

/*
 * InfluenceConfig
 *
 * Central place for the few tunable settings Influence Miner needs.
 * Every setting can be supplied three ways, highest priority first:
 *   1. a JVM system property, e.g.  -Dinfluence.outputDir=D:/out
 *   2. a key in the DeMaP Miner .prop file (conf/DEMAPMinerPEPsNew.prop)
 *   3. the built-in default below
 *
 * Keys:
 *   influenceOutputDir            where CSV/report files are written
 *   influenceDataDir              where optional CSV inputs live (role_map.csv, proposal_outcomes.csv)
 *   influenceMinimumScore         candidates below this heuristic score are not stored
 *   influenceGovernanceSplitDate  yyyy-MM-dd; messages before it are "bdfl_era", on/after "post_bdfl_era"
 *
 * The governance split defaults to 2018-07-12, the day Guido van Rossum
 * announced he was stepping down as BDFL after PEP 572. It is a
 * configuration value, not a hard-coded constant inside the analysis code.
 */
public class InfluenceConfig {

    public static final String DEFAULT_OUTPUT_DIR = "C:/DeMapMiner/workspace/influenceMiner/output";
    public static final String DEFAULT_DATA_DIR = "C:/DeMapMiner/workspace/influenceMiner/data";
    public static final double DEFAULT_MINIMUM_SCORE = 1.0;
    public static final String DEFAULT_GOVERNANCE_SPLIT_DATE = "2018-07-12";

    public static final String ERA_BDFL = "bdfl_era";
    public static final String ERA_POST_BDFL = "post_bdfl_era";
    public static final String ERA_UNKNOWN = "unknown";

    public static final String EXTRACTION_SCHEME = "heuristic-v2";

    /*
     * Automated senders and mailing lists that carry no human discussion:
     * commit notifications (which embed whole PEP texts), bug-tracker and
     * patch-tracker robots. They are needed by DeMaP Miner's state mining but
     * must not count as influence. Override with influenceExcludeSenders /
     * influenceExcludeLists (comma-separated, case-insensitive substrings).
     */
    public static final String DEFAULT_EXCLUDE_SENDERS =
            "python-checkins@python.org,report@bugs.python.org,status@bugs.python.org,noreply@sourceforge.net,"
            + "new-bugs-announce@python.org,bugs@python.org,noreply@github.com,notifications@github.com";
    public static final String DEFAULT_EXCLUDE_LISTS = "python-checkins,python-bugs-list,python-patches";

    private static String[] cachedExcludeSenders;
    private static String[] cachedExcludeLists;

    public static String[] excludeSenders() {
        if (cachedExcludeSenders == null) {
            cachedExcludeSenders = splitList(read("influence.excludeSenders", "influenceExcludeSenders", DEFAULT_EXCLUDE_SENDERS));
        }
        return cachedExcludeSenders;
    }

    public static String[] excludeLists() {
        if (cachedExcludeLists == null) {
            cachedExcludeLists = splitList(read("influence.excludeLists", "influenceExcludeLists", DEFAULT_EXCLUDE_LISTS));
        }
        return cachedExcludeLists;
    }

    /*
     * True when the message comes from an automated sender or an excluded list.
     */
    public static boolean isExcludedMessage(String senderEmail, String mailingList) {
        String e = senderEmail == null ? "" : senderEmail.trim().toLowerCase();
        String l = mailingList == null ? "" : mailingList.trim().toLowerCase();
        String[] senders = excludeSenders();
        for (int i = 0; i < senders.length; i++) {
            if (e.length() > 0 && e.contains(senders[i])) return true;
        }
        String[] lists = excludeLists();
        for (int i = 0; i < lists.length; i++) {
            if (l.length() > 0 && l.contains(lists[i])) return true;
        }
        return false;
    }

    private static String[] splitList(String v) {
        if (v == null || v.trim().length() == 0 || v.trim().equalsIgnoreCase("none")) {
            return new String[0];
        }
        String[] parts = v.split(",");
        java.util.List<String> out = new java.util.ArrayList<String>();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim().toLowerCase();
            if (p.length() > 0) out.add(p);
        }
        return out.toArray(new String[out.size()]);
    }

    private static String cachedOutputDir;
    private static String cachedDataDir;
    private static Double cachedMinimumScore;
    private static LocalDate cachedSplitDate;

    public static String outputDir() {
        if (cachedOutputDir == null) {
            cachedOutputDir = read("influence.outputDir", "influenceOutputDir", DEFAULT_OUTPUT_DIR);
        }
        return cachedOutputDir;
    }

    public static String dataDir() {
        if (cachedDataDir == null) {
            cachedDataDir = read("influence.dataDir", "influenceDataDir", DEFAULT_DATA_DIR);
        }
        return cachedDataDir;
    }

    public static double minimumScore() {
        if (cachedMinimumScore == null) {
            String v = read("influence.minimumScore", "influenceMinimumScore", null);
            double d = DEFAULT_MINIMUM_SCORE;
            if (v != null) {
                try {
                    d = Double.parseDouble(v.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            cachedMinimumScore = Double.valueOf(d);
        }
        return cachedMinimumScore.doubleValue();
    }

    public static LocalDate governanceSplitDate() {
        if (cachedSplitDate == null) {
            String v = read("influence.governanceSplitDate", "influenceGovernanceSplitDate", DEFAULT_GOVERNANCE_SPLIT_DATE);
            LocalDate d;
            try {
                d = LocalDate.parse(v.trim());
            } catch (Exception e) {
                d = LocalDate.parse(DEFAULT_GOVERNANCE_SPLIT_DATE);
            }
            cachedSplitDate = d;
        }
        return cachedSplitDate;
    }

    /*
     * Classifies a message date into a governance era using the split date.
     */
    public static String governanceEra(LocalDate messageDate) {
        if (messageDate == null) {
            return ERA_UNKNOWN;
        }
        return messageDate.isBefore(governanceSplitDate()) ? ERA_BDFL : ERA_POST_BDFL;
    }

    public static File ensureOutputDir() {
        File dir = new File(outputDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File outputFile(String name) {
        return new File(ensureOutputDir(), name);
    }

    private static String read(String systemKey, String propKey, String defaultValue) {
        String v = System.getProperty(systemKey);
        if (v != null && v.trim().length() > 0) {
            return v.trim();
        }
        try {
            v = PropertiesFile.readFromPropertiesFile(propKey, true);
        } catch (Exception e) {
            v = null;
        }
        if (v != null && v.trim().length() > 0) {
            return v.trim();
        }
        return defaultValue;
    }
}
