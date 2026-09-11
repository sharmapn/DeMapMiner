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
