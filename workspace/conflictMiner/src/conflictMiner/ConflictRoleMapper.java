package conflictMiner;

/*
 * ConflictRoleMapper
 *
 * Lightweight role mapper. This should later be replaced or enriched with
 * project-specific actor metadata from Python PEPs, Bitcoin BIPs, maintainers,
 * delegates, BIP editors, steering-council members, and core developers.
 */
public class ConflictRoleMapper {

    public static String mapRole(String authorEmail, String authorName) {

        String email = safeLower(authorEmail);
        String name = safeLower(authorName);
        String combined = email + " " + name;

        if (containsAny(combined, new String[] {
                "guido", "bdfl"
        })) {
            return "BDFL";
        }

        if (containsAny(combined, new String[] {
                "steering", "council"
        })) {
            return "steering council";
        }

        if (containsAny(combined, new String[] {
                "delegate", "pep delegate"
        })) {
            return "delegate";
        }

        if (containsAny(combined, new String[] {
                "bip editor", "bip-editor", "bips"
        })) {
            return "BIP editor";
        }

        if (containsAny(combined, new String[] {
                "core", "python.org", "python", "cpython"
        })) {
            return "core developer";
        }

        if (containsAny(combined, new String[] {
                "maintainer", "release manager", "release-manager"
        })) {
            return "maintainer";
        }

        if (containsAny(combined, new String[] {
                "bitcoin", "bitcoincore", "bitcoin-core"
        })) {
            return "bitcoin developer";
        }

        if (containsAny(combined, new String[] {
                "miner", "mining", "pool"
        })) {
            return "miner";
        }

        if (containsAny(combined, new String[] {
                "wallet", "exchange"
        })) {
            return "ecosystem actor";
        }

        if (containsAny(combined, new String[] {
                "company", "foundation", "org", "organisation", "organization"
        })) {
            return "organization";
        }

        if (email.length() > 0 || name.length() > 0) {
            return "participant";
        }

        return "unknown";
    }

    private static String safeLower(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase();
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
