package influenceMiner;

import java.util.ArrayList;
import java.util.List;

/*
 * InfluenceTypeDetector
 *
 * Lightweight rule-based detector for the first Influence Miner integration.
 * Later versions can replace this with a supervised multi-label classifier.
 */
public class InfluenceTypeDetector {

    public static String detectTypes(String sentence) {

        if (sentence == null) {
            return "none";
        }

        String s = sentence.toLowerCase();
        List<String> types = new ArrayList<String>();

        addIf(types, "strategic", containsAny(s, new String[] {
                "long term", "long-term", "future direction", "project direction",
                "philosophy", "governance", "vision", "principle", "principles",
                "values", "identity", "decentral", "readability", "simplicity"
        }));

        addIf(types, "operational", containsAny(s, new String[] {
                "implement", "implementation", "maintenance", "maintain", "release",
                "migration", "migrate", "deploy", "deployment", "testing", "test suite",
                "documentation", "backport", "rollout", "schedule"
        }));

        addIf(types, "functional", containsAny(s, new String[] {
                "solves", "does not solve", "doesn't solve", "use case", "edge case",
                "functionality", "feature", "api", "behaviour", "behavior", "semantics",
                "alternative design", "better approach", "problem statement"
        }));

        addIf(types, "tactical", containsAny(s, new String[] {
                "to summarize", "to summarise", "summary", "let us decide", "let's decide",
                "can we agree", "compromise", "reframe", "narrow", "clarify", "evidence",
                "defer", "postpone", "next step", "call for", "rough consensus"
        }));

        addIf(types, "authority", containsAny(s, new String[] {
                "bdfl", "steering council", "delegate", "pep delegate", "core developer",
                "maintainer", "bip editor", "editor", "release manager", "decision maker",
                "i pronounce", "pronouncement", "veto", "expert"
        }));

        addIf(types, "compatibility", containsAny(s, new String[] {
                "backward compatibility", "backwards compatibility", "compatible", "incompatible",
                "break", "breaking change", "breaks existing", "interoperability", "migration path",
                "old nodes", "existing code", "existing users", "legacy"
        }));

        addIf(types, "security", containsAny(s, new String[] {
                "security", "attack", "vulnerability", "risk", "unsafe", "safe", "safety",
                "consensus failure", "attack surface", "privacy", "robust", "robustness",
                "denial of service", "dos", "exploit", "threat"
        }));

        addIf(types, "standards", containsAny(s, new String[] {
                "standard", "standards", "specification", "spec", "rfc", "convention",
                "interoperable", "interoperate", "compliance", "protocol standard"
        }));

        addIf(types, "ecosystem", containsAny(s, new String[] {
                "ecosystem", "downstream", "library", "libraries", "wallet", "wallets",
                "exchange", "exchanges", "miner", "miners", "node", "nodes", "users",
                "third party", "package", "packages", "client", "clients"
        }));

        addIf(types, "economic", containsAny(s, new String[] {
                "cost", "costs", "funding", "incentive", "incentives", "fee", "fees",
                "market", "business", "commercial", "resources", "resource cost", "economic"
        }));

        addIf(types, "organizational", containsAny(s, new String[] {
                "company", "companies", "foundation", "organization", "organisation",
                "working group", "team", "release team", "committee", "institution", "sponsor"
        }));

        addIf(types, "coalition", containsAny(s, new String[] {
                "several people", "many people", "we agree", "we all", "consensus seems",
                "strong support", "broad support", "multiple developers", "others have said",
                "as others", "there is agreement", "community agrees"
        }));

        addIf(types, "user_demand", containsAny(s, new String[] {
                "users want", "users need", "user demand", "people ask", "frequently requested",
                "common request", "pain point", "confusing for users", "beginners", "newcomers",
                "real users", "adoption"
        }));

        if (types.size() == 0) {
            return "none";
        }

        return join(types);
    }

    public static String primaryType(String influenceTypes) {

        if (influenceTypes == null || influenceTypes.trim().length() == 0) {
            return "none";
        }

        String[] parts = influenceTypes.split(",");
        if (parts.length == 0) {
            return "none";
        }

        return parts[0].trim();
    }

    private static boolean containsAny(String text, String[] cues) {

        for (int i = 0; i < cues.length; i++) {
            if (text.contains(cues[i])) {
                return true;
            }
        }

        return false;
    }

    private static void addIf(List<String> types, String type, boolean condition) {

        if (condition && !types.contains(type)) {
            types.add(type);
        }
    }

    private static String join(List<String> values) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(values.get(i));
        }

        return sb.toString();
    }
}
