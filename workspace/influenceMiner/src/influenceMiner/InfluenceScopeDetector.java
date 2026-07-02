package influenceMiner;

/*
 * Distinguishes internal, external, mixed, or unknown influence.
 *
 * This is only a first heuristic version. A later version should combine
 * sender-role metadata, email-domain metadata, organization mapping,
 * and project-specific role tables.
 */
public class InfluenceScopeDetector {

    public static String detectScope(String sentence, String authorRole) {

        String s = sentence == null ? "" : sentence.toLowerCase();
        String r = authorRole == null ? "" : authorRole.toLowerCase();

        boolean internal = false;
        boolean external = false;

        if (
            r.contains("bdfl") ||
            r.contains("delegate") ||
            r.contains("core") ||
            r.contains("maintainer") ||
            r.contains("editor") ||
            r.contains("release") ||
            r.contains("developer")
        ) {
            internal = true;
        }

        if (
            s.contains("core developer") ||
            s.contains("maintainer") ||
            s.contains("steering council") ||
            s.contains("bip editor") ||
            s.contains("pep delegate") ||
            s.contains("release manager")
        ) {
            internal = true;
        }

        if (
            s.contains("users") ||
            s.contains("downstream") ||
            s.contains("wallet") ||
            s.contains("wallets") ||
            s.contains("exchange") ||
            s.contains("exchanges") ||
            s.contains("miners") ||
            s.contains("companies") ||
            s.contains("business") ||
            s.contains("third party") ||
            s.contains("ecosystem") ||
            s.contains("libraries")
        ) {
            external = true;
        }

        if (internal && external) {
            return "mixed";
        }

        if (internal) {
            return "internal";
        }

        if (external) {
            return "external";
        }

        return "unknown";
    }
}
