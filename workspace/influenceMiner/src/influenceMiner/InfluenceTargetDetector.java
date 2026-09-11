package influenceMiner;

/*
 * InfluenceTargetDetector
 *
 * Coarse detection of WHAT the influence is aimed at. Order matters: the
 * more specific targets are tested first, and "proposal" is the catch-all
 * when the sentence names the PEP/BIP but nothing more specific.
 *
 *   governance, implementation, security, compatibility, ecosystem, users,
 *   proposal, unknown
 *
 * HEURISTIC: word-boundary cue matching only.
 */
public class InfluenceTargetDetector {

    private static final String[] GOVERNANCE = {
            "governance", "steering council", "bdfl", "delegate", "decision process", "decision-making process",
            "authority", "who decides", "process for", "pep process", "bip process", "voting", "the vote", "election",
            "pronouncement", "final say", "how decisions"
    };
    private static final String[] IMPLEMENTATION = {
            "implement", "implementation", "implemented", "implementing", "release", "testing", "test suite", "deploy",
            "deployment", "migration", "maintenance", "patch", "patches", "the code", "cpython", "reference implementation",
            "backport", "compiler", "parser", "bytecode", "interpreter", "performance", "benchmark", "optimization",
            "optimisation", "activation", "rollout"
    };
    private static final String[] SECURITY = {
            "security", "attack", "attacker", "vulnerability", "vulnerable", "privacy", "consensus failure", "exploit",
            "double spend", "double-spend", "malicious", "unsafe", "safety", "denial of service", "ddos", "threat"
    };
    private static final String[] COMPATIBILITY = {
            "compatibility", "compatible", "incompatible", "breaking change", "breaks existing", "break existing",
            "interoperability", "backward", "backwards", "legacy", "python 2", "python2", "py2", "py3", "2to3",
            "deprecat", "old nodes", "old clients", "soft fork", "hard fork", "softfork", "hardfork", "regression"
    };
    private static final String[] ECOSYSTEM = {
            "ecosystem", "downstream", "wallet", "wallets", "exchange", "exchanges", "miner", "miners", "library",
            "libraries", "third party", "third-party", "3rd party", "pypi", "stdlib", "standard library", "packages",
            "distros", "distributions", "vendors", "tools", "ides", "frameworks", "numpy", "django", "twisted", "pypy",
            "jython", "ironpython", "other implementations", "the network", "full nodes", "lightning"
    };
    private static final String[] USERS = {
            "users", "end users", "end-users", "beginners", "newcomers", "newbies", "students", "user demand",
            "pain point", "people who", "readers", "the average", "ordinary", "programmers", "developers who use",
            "learners", "teaching"
    };
    private static final String[] PROPOSAL = {
            "pep", "bip", "proposal", "this change", "this idea", "this design", "the design", "this feature",
            "the feature", "this syntax", "the syntax", "this approach", "the approach", "this suggestion",
            "the spec", "the specification", "this pep", "the pep", "this bip", "the bip"
    };

    public static String detectTarget(String sentence) {

        if (sentence == null) {
            return "unknown";
        }
        String s = " " + sentence.toLowerCase() + " ";

        if (InfluenceTypeDetector.containsAny(s, GOVERNANCE)) return "governance";
        if (InfluenceTypeDetector.containsAny(s, SECURITY)) return "security";
        if (InfluenceTypeDetector.containsAny(s, COMPATIBILITY) || s.contains("deprecat")) return "compatibility";
        if (InfluenceTypeDetector.containsAny(s, IMPLEMENTATION)) return "implementation";
        if (InfluenceTypeDetector.containsAny(s, ECOSYSTEM)) return "ecosystem";
        if (InfluenceTypeDetector.containsAny(s, USERS)) return "users";
        if (InfluenceTypeDetector.containsAny(s, PROPOSAL) || s.matches(".*\\b(pep|bip)\\s?\\d+.*")) return "proposal";

        return "unknown";
    }
}
