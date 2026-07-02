package influenceMiner;

/*
 * Compares influence direction with final proposal outcome.
 *
 * This should not be interpreted as causal proof. It simply indicates that
 * the detected direction is consistent with the observed final decision.
 */
public class InfluenceOutcomeAlignmentAnalyzer {

    public static boolean aligns(String influenceDirection, String finalDecision) {

        if (influenceDirection == null || finalDecision == null) {
            return false;
        }

        String d = influenceDirection.toLowerCase();
        String f = finalDecision.toLowerCase();

        if (d.equals("supporting") && (
                f.contains("accepted") ||
                f.contains("approved") ||
                f.contains("final") ||
                f.contains("active") ||
                f.contains("deployed"))) {
            return true;
        }

        if (d.equals("blocking") && (
                f.contains("rejected") ||
                f.contains("withdrawn") ||
                f.contains("closed") ||
                f.contains("deferred"))) {
            return true;
        }

        if (d.equals("revising") && (
                f.contains("deferred") ||
                f.contains("superseded") ||
                f.contains("revised") ||
                f.contains("draft") ||
                f.contains("provisional"))) {
            return true;
        }

        return false;
    }
}
