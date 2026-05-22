package preferenceMiner;

public class PreferenceHeuristics {

    public static double scoreSentence(String sentence, String authorRole, int daysBeforeDecision) {

        double score = 0.0;

        if (sentence == null) {
            return score;
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("i support") ||
            s.contains("i oppose") ||
            s.contains("i like") ||
            s.contains("i dislike") ||
            s.contains("i agree") ||
            s.contains("i disagree") ||
            s.contains("+1") ||
            s.contains("-1")
        ) {
            score += 0.9;
        }

        if (
            s.contains("pep") ||
            s.contains("bip") ||
            s.contains("proposal") ||
            s.contains("this change") ||
            s.contains("this idea")
        ) {
            score += 0.6;
        }

        if (authorRole != null) {
            String r = authorRole.toLowerCase();

            if (r.contains("bdfl")) {
                score += 1.0;
            } else if (r.contains("delegate")) {
                score += 0.9;
            } else if (r.contains("core")) {
                score += 0.8;
            } else if (r.contains("developer")) {
                score += 0.6;
            } else if (r.contains("user")) {
                score += 0.3;
            }
        }

        if (daysBeforeDecision >= 0 && daysBeforeDecision <= 7) {
            score += 0.9;
        } else if (daysBeforeDecision > 7 && daysBeforeDecision <= 30) {
            score += 0.6;
        } else if (daysBeforeDecision > 30 && daysBeforeDecision <= 90) {
            score += 0.3;
        }

        if (
            s.contains("not sure") ||
            s.contains("maybe") ||
            s.contains("perhaps") ||
            s.contains("might")
        ) {
            score -= 0.3;
        }

        return score;
    }
}
