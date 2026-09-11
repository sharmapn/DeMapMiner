package influenceMiner;

/*
 * InfluenceScopeDetector
 *
 * internal  the influence comes from, or invokes, the project's own
 *           governance / development structure
 * external  the influence comes from, or invokes, actors outside that
 *           structure (users, downstream projects, companies, miners ...)
 * mixed     both are present (e.g. a core developer invoking user demand)
 * unknown   neither the author's role nor the sentence gives a signal
 *
 * Two evidence sources are combined:
 *   (a) the resolved author role (see InfluenceRoleMapper vocabulary)
 *   (b) actor words in the sentence itself
 * Role alone never yields "external": an unknown or community-member author
 * is only external when the sentence itself speaks for outside actors,
 * because most mailing-list posters cannot be safely classified from
 * name/email alone.
 */
public class InfluenceScopeDetector {

    private static final String[] INTERNAL_ROLES = {
            "bdfl", "steering council", "delegate", "proposal author", "pep editor", "bip editor", "core developer",
            "release manager", "maintainer", "committer", "bip author", "developer"
    };

    private static final String[] INTERNAL_CUES = {
            "core developer", "core developers", "core dev", "core devs", "committer", "committers", "maintainer",
            "maintainers", "steering council", "the council", "bip editor", "pep editor", "pep delegate", "bdfl-delegate",
            "bdfl delegate", "bdfl", "guido", "release manager", "release team", "the pep author", "pep authors",
            "the author of the pep", "python-dev", "we as developers", "those of us who maintain", "cpython developers",
            "bitcoin core developers", "core maintainers", "the maintainers"
    };

    private static final String[] EXTERNAL_CUES = {
            "users", "end users", "end-users", "the user", "our users", "python users", "downstream", "downstream projects",
            "wallet", "wallets", "exchange", "exchanges", "miners", "mining pools", "companies", "company", "business",
            "businesses", "third party", "third-party", "3rd party", "ecosystem", "libraries", "library authors",
            "package maintainers", "distributions", "distros", "vendors", "customers", "clients", "beginners", "newcomers",
            "students", "teachers", "educators", "researchers", "scientists", "the community", "wider community",
            "industry", "enterprise", "standards body", "standards bodies", "regulators", "node operators", "merchants",
            "the public", "outside", "external", "people who use", "people using", "folks who", "everyone who uses",
            "my employer", "at work", "in production", "our codebase", "our code base", "our project", "in my project"
    };

    public static String detectScope(String sentence, String authorRole) {

        String s = sentence == null ? "" : " " + sentence.toLowerCase() + " ";
        String r = authorRole == null ? "" : authorRole.toLowerCase();

        boolean internal = false;
        boolean external = false;

        for (int i = 0; i < INTERNAL_ROLES.length; i++) {
            if (r.contains(INTERNAL_ROLES[i])) {
                internal = true;
                break;
            }
        }
        if (r.contains("miner") || r.contains("wallet") || r.contains("exchange") || r.equals("user")) {
            external = true;
        }

        if (InfluenceTypeDetector.containsAny(s, INTERNAL_CUES)) internal = true;
        if (InfluenceTypeDetector.containsAny(s, EXTERNAL_CUES)) external = true;

        if (internal && external) return "mixed";
        if (internal) return "internal";
        if (external) return "external";
        return "unknown";
    }
}
