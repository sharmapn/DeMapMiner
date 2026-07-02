package conflictMiner;

/*
 * ConflictCivilityDetector
 *
 * Separates decision conflict from civility. A sentence can be high-conflict
 * but civil, or low-conflict but uncivil.
 */
public class ConflictCivilityDetector {

    public static String detectCivility(String sentence) {

        if (sentence == null) {
            return "unknown";
        }

        String s = sentence.toLowerCase();

        if (containsAny(s, new String[] {
                "nonsense", "ridiculous", "absurd", "stupid", "idiotic",
                "bad faith", "troll", "liar", "dishonest", "shut up",
                "personal attack", "insult", "hostile", "offensive"
        })) {
            return "uncivil";
        }

        if (containsAny(s, new String[] {
                "frustrating", "frustrated", "angry", "annoyed", "upset",
                "this is not acceptable", "unacceptable", "i object", "strong -1",
                "strong nack", "you are wrong"
        })) {
            return "tense";
        }

        if (containsAny(s, new String[] {
                "thanks", "thank you", "i appreciate", "good point",
                "fair point", "i understand", "i see your point", "respectfully",
                "with respect", "i may be missing something", "please clarify"
        })) {
            return "civil";
        }

        return "unknown";
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
