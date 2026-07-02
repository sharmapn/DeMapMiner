package conflictMiner;

/*
 * ConflictIntensityDetector
 *
 * Estimates conflict intensity on a 0-5 scale.
 * 0 = no conflict, 5 = decision-blocking conflict.
 */
public class ConflictIntensityDetector {

    public static int detectIntensity(String sentence, String conflictTypes, String stance, String resolutionStatus) {

        if (sentence == null) {
            return 0;
        }

        String s = sentence.toLowerCase();
        int level = 0;

        if (conflictTypes != null && !conflictTypes.equals("none")) {
            level = 1;
        }

        if (stance != null) {
            if (stance.equals("concern") || stance.equals("procedural")) {
                level = Math.max(level, 1);
            }
            if (stance.equals("oppose") || stance.equals("rebuttal") || stance.equals("alternative")) {
                level = Math.max(level, 2);
            }
            if (stance.equals("compromise")) {
                level = Math.max(level, 2);
            }
        }

        if (containsAny(s, new String[] {
                "strong -1", "strong nack", "nack", "i object", "we object",
                "serious concern", "major concern", "fundamental problem",
                "not acceptable", "unacceptable", "wrong approach", "cannot accept",
                "should be rejected", "should reject"
        })) {
            level = Math.max(level, 3);
        }

        if (containsAny(s, new String[] {
                "still not addressed", "not addressed", "unresolved objection",
                "my objection remains", "this remains a blocker", "blocking issue",
                "escalate", "bad faith", "hostile", "ridiculous", "nonsense",
                "consensus failure", "no consensus"
        })) {
            level = Math.max(level, 4);
        }

        if (containsAny(s, new String[] {
                "block this", "blocking this", "decision blocker", "must not be accepted",
                "cannot go forward", "cannot proceed", "should not proceed",
                "will fork", "hard fork risk", "security-critical blocker",
                "consensus-breaking", "breaks consensus"
        })) {
            level = Math.max(level, 5);
        }

        if (resolutionStatus != null && resolutionStatus.equals("unresolved_objection")) {
            level = Math.max(level, 4);
        }

        if (s.contains("!") && level > 0 && level < 5) {
            level = level + 1;
        }

        if (level > 5) {
            level = 5;
        }

        return level;
    }

    public static String intensityLabel(int level) {
        if (level <= 0) {
            return "no_conflict";
        }
        if (level == 1) {
            return "mild_concern";
        }
        if (level == 2) {
            return "explicit_disagreement";
        }
        if (level == 3) {
            return "strong_or_sustained_dispute_signal";
        }
        if (level == 4) {
            return "escalated_or_unresolved_conflict";
        }
        return "decision_blocking_conflict";
    }

    private static boolean containsAny(String s, String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (s.contains(words[i])) {
                return true;
            }
        }
        return false;
    }
}
