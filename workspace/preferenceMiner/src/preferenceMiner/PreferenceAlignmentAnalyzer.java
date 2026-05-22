package preferenceMiner;

public class PreferenceAlignmentAnalyzer {

    public static boolean aligns(String polarity, String finalDecision) {

        if (polarity == null || finalDecision == null) {
            return false;
        }

        String p = polarity.toLowerCase();
        String d = finalDecision.toLowerCase();

        if (p.equals("positive") && d.contains("accepted")) {
            return true;
        }

        if (p.equals("negative") && d.contains("rejected")) {
            return true;
        }

        return false;
    }
}
