package conflictMiner;

/*
 * ConflictTypeDetector
 *
 * Lightweight multi-label detector for OSS decision conflict types.
 * The categories follow the Conflict Miner paper design:
 * technical, process, governance_authority, preference, rationale,
 * value_ideology, priority_resource, relational, and ecosystem.
 */
public class ConflictTypeDetector {

    public static String detectTypes(String sentence) {

        if (sentence == null) {
            return "none";
        }

        String s = sentence.toLowerCase();
        String tokenized = " " + s.replaceAll("[^a-z0-9+\\-]", " ") + " ";
        String types = "";

        if (containsAny(s, new String[] {
                "api", "syntax", "implementation", "implement", "architecture",
                "performance", "security", "bug", "correctness", "maintainability",
                "backward compatibility", "backwards compatibility", "breaking change",
                "consensus rule", "soft fork", "hard fork", "deployment",
                "test", "migration", "release", "vulnerability", "attack surface"
        })) {
            types = append(types, "technical");
        }

        if (containsAny(s, new String[] {
                "process", "procedure", "pep process", "bip process",
                "not enough discussion", "needs more discussion", "mailing list",
                "rough consensus", "consensus", "decision process", "status",
                "accepted", "rejected", "deferred", "withdrawn", "premature",
                "not ready", "deadline", "vote", "call for decision"
        })) {
            types = append(types, "process");
        }

        if (containsAny(s, new String[] {
                "bdfl", "steering council", "delegate", "pep delegate",
                "bip editor", "maintainer", "core developer", "who decides",
                "authority", "governance", "legitimate", "legitimacy",
                "unilateral", "centralized", "centralised", "capture", "permission"
        })) {
            types = append(types, "governance_authority");
        }

        if (containsAny(s, new String[] {
                "i prefer", "we prefer", "would prefer", "rather", "i want",
                "we want", "i would like", "we would like", "+1", "-1",
                "strong -1", "strong nack", "better", "worse"
        }) || tokenized.contains(" ack ") || tokenized.contains(" nack ")) {
            types = append(types, "preference");
        }

        if (containsAny(s, new String[] {
                "because", "therefore", "reason", "rationale", "evidence",
                "for example", "example", "tradeoff", "trade-off", "use case",
                "this means", "as a result", "due to", "since"
        })) {
            types = append(types, "rationale");
        }

        if (containsAny(s, new String[] {
                "philosophy", "principle", "principles", "pythonic",
                "decentralization", "decentralisation", "minimal", "minimalism",
                "simple is better", "explicit is better", "stability", "innovation",
                "project direction", "project identity", "values", "ideology"
        })) {
            types = append(types, "value_ideology");
        }

        if (containsAny(s, new String[] {
                "priority", "roadmap", "maintenance burden", "burden",
                "not worth", "worth it", "resources", "time", "cost",
                "review effort", "maintainer time", "release blocker", "too much work",
                "complexity", "scope creep"
        })) {
            types = append(types, "priority_resource");
        }

        if (containsAny(s, new String[] {
                "nonsense", "ridiculous", "absurd", "bad faith", "troll",
                "insult", "offensive", "you are wrong", "your argument",
                "frustrating", "frustrated", "angry", "hostile", "personal attack"
        })) {
            types = append(types, "relational");
        }

        if (containsAny(s, new String[] {
                "ecosystem", "downstream", "users", "user base", "library",
                "libraries", "framework", "wallet", "wallets", "exchange", "exchanges",
                "miners", "mining", "vendor", "vendors", "company", "companies",
                "organization", "organisation", "adoption", "compatibility with"
        })) {
            types = append(types, "ecosystem");
        }

        if (types.length() == 0) {
            return "none";
        }

        return types;
    }

    public static String primaryType(String conflictTypes) {

        if (conflictTypes == null || conflictTypes.trim().length() == 0) {
            return "none";
        }

        String[] parts = conflictTypes.split(",");
        if (parts.length == 0) {
            return "none";
        }

        return parts[0].trim();
    }

    private static boolean containsAny(String s, String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (s.contains(words[i])) {
                return true;
            }
        }
        return false;
    }

    private static String append(String existing, String value) {
        if (existing == null || existing.length() == 0) {
            return value;
        }
        if (existing.contains(value)) {
            return existing;
        }
        return existing + "," + value;
    }
}
