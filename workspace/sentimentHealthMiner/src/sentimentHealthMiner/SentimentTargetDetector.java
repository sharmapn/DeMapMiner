package sentimentHealthMiner;

/*
 * SentimentTargetDetector
 *
 * Detects the likely target of the emotional signal.
 */
public class SentimentTargetDetector {

    public static String detectTarget(String sentence) {

        if (sentence == null) {
            return "unknown";
        }

        String s = sentence.toLowerCase();

        if (containsAny(s, new String[] {
                "pep", "bip", "proposal", "this proposal", "the proposal",
                "this idea", "this feature", "this change", "assignment expression"
        })) {
            return "proposal";
        }

        if (containsAny(s, new String[] {
                "implementation", "code", "patch", "api", "syntax", "architecture",
                "performance", "test", "release", "migration", "deployment"
        })) {
            return "implementation";
        }

        if (containsAny(s, new String[] {
                "process", "procedure", "discussion", "mailing list", "consensus",
                "vote", "decision process", "not enough discussion", "too early", "premature"
        })) {
            return "process";
        }

        if (containsAny(s, new String[] {
                "bdfl", "steering council", "delegate", "pep delegate", "bip editor",
                "maintainer", "core developer", "authority", "governance", "who decides"
        })) {
            return "governance";
        }

        if (containsAny(s, new String[] {
                "you ", "your ", "he ", "she ", "they ", "person", "people",
                "author", "speaker", "sender", "contributor"
        })) {
            return "actor_relationship";
        }

        if (containsAny(s, new String[] {
                "community", "project", "team", "group", "core team", "developers",
                "contributors", "maintainers"
        })) {
            return "community";
        }

        if (containsAny(s, new String[] {
                "users", "user base", "ecosystem", "downstream", "library", "libraries",
                "wallet", "wallets", "miners", "exchanges", "vendors", "companies"
        })) {
            return "ecosystem_users";
        }

        if (containsAny(s, new String[] {
                "i am", "i'm", "i feel", "i cannot", "i can't", "my concern",
                "my concerns", "my point", "my objection"
        })) {
            return "self";
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
