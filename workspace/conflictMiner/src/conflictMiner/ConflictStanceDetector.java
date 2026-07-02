package conflictMiner;

/*
 * ConflictStanceDetector
 *
 * Detects the kind of conflict act expressed by a sentence.
 */
public class ConflictStanceDetector {

    public static String detectStance(String sentence) {

        if (sentence == null) {
            return "neutral";
        }

        String s = sentence.toLowerCase();
        String tokenized = " " + s.replaceAll("[^a-z0-9+\\-]", " ") + " ";

        if (containsAny(s, new String[] {
                "i disagree", "we disagree", "do not agree", "don't agree",
                "i object", "we object", "object to", "oppose", "opposed",
                "strong -1", "-1", "reject", "should be rejected",
                "should not be accepted", "unacceptable", "not acceptable"
        }) || tokenized.contains(" nack ")) {
            return "oppose";
        }

        if (containsAny(s, new String[] {
                "i am concerned", "i'm concerned", "we are concerned",
                "concern", "worry", "worried", "risk", "problematic",
                "not convinced", "not sure", "this seems risky", "this may break"
        })) {
            return "concern";
        }

        if (containsAny(s, new String[] {
                "however", "but", "on the other hand", "that said",
                "this does not address", "doesn't address", "counterexample",
                "on the contrary", "i don't think that follows", "i do not think that follows"
        })) {
            return "rebuttal";
        }

        if (containsAny(s, new String[] {
                "compromise", "middle ground", "alternative", "instead",
                "could revise", "should revise", "if we changed", "if this is changed",
                "one way to resolve", "to address this", "maybe we can"
        })) {
            return "compromise";
        }

        if (containsAny(s, new String[] {
                "process", "procedure", "not enough discussion", "needs more discussion",
                "rough consensus", "consensus", "call for decision", "premature",
                "not ready", "decision process", "vote", "defer"
        })) {
            return "procedural";
        }

        if (containsAny(s, new String[] {
                "i propose instead", "we propose instead", "another option",
                "alternative proposal", "option b", "different approach",
                "would be better to", "rather than"
        })) {
            return "alternative";
        }

        if (containsAny(s, new String[] {
                "i agree", "we agree", "+1", "support", "supported",
                "sounds good", "makes sense", "i accept", "we accept"
        }) || tokenized.contains(" ack ")) {
            return "support";
        }

        return "neutral";
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
