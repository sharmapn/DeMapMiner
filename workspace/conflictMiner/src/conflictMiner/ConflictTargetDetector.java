package conflictMiner;

/*
 * ConflictTargetDetector
 *
 * Detects what the conflict appears to be about.
 */
public class ConflictTargetDetector {

    public static String detectTarget(String sentence) {

        if (sentence == null) {
            return "unknown";
        }

        String s = sentence.toLowerCase();

        if (containsAny(s, new String[] {
                "proposal", "pep", "bip", "this change", "this idea", "this design",
                "this feature", "this approach", "the proposal"
        })) {
            return "proposal";
        }

        if (containsAny(s, new String[] {
                "implementation", "implement", "code", "patch", "architecture",
                "algorithm", "api", "syntax", "test", "release", "migration"
        })) {
            return "implementation";
        }

        if (containsAny(s, new String[] {
                "process", "procedure", "discussion", "mailing list", "rough consensus",
                "consensus", "vote", "decision process", "status", "deadline"
        })) {
            return "process";
        }

        if (containsAny(s, new String[] {
                "bdfl", "steering council", "delegate", "bip editor", "maintainer",
                "core developer", "authority", "governance", "who decides", "legitimacy"
        })) {
            return "governance";
        }

        if (containsAny(s, new String[] {
                "security", "vulnerability", "attack", "attack surface", "privacy",
                "consensus-breaking", "consensus rule", "risk"
        })) {
            return "security";
        }

        if (containsAny(s, new String[] {
                "compatibility", "backward", "backwards", "breaking", "migration",
                "interoperability", "standard", "existing code"
        })) {
            return "compatibility";
        }

        if (containsAny(s, new String[] {
                "user", "users", "downstream", "library", "libraries", "wallet",
                "exchange", "miners", "ecosystem", "adoption", "vendor", "company"
        })) {
            return "ecosystem_users";
        }

        if (containsAny(s, new String[] {
                "you", "your argument", "bad faith", "nonsense", "ridiculous",
                "personal", "insult", "hostile", "troll"
        })) {
            return "actor_relationship";
        }

        if (containsAny(s, new String[] {
                "resources", "time", "cost", "maintenance", "burden",
                "review effort", "priority", "roadmap", "release blocker"
        })) {
            return "resources";
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
