package influenceMiner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * InfluenceTypeDetector
 *
 * Rule-based, multi-label detector for the thirteen influence mechanisms.
 * It stays transparent on purpose: every label can be traced back to the cue
 * phrases that fired (see detectEvidenceCues), which is what the annotation
 * study needs. A supervised / LLM classifier can replace detectTypes() later
 * without changing callers.
 *
 * Matching rules:
 *   - all cues are matched on word boundaries (so "api" does not fire inside
 *     "rapid", "fee" not inside "coffee", "spec" not inside "special")
 *   - British and American spellings are both listed
 *   - a few very broad words are only accepted NEAR a disambiguating word:
 *       "break"  needs compatib/existing/code/api/users/change/backward nearby
 *       "safe"   is dropped; "unsafe"/"safety" kept
 *       "risk"   needs security/attack/regression/break/compat/data nearby
 *       "team"   needs release/core/dev/steering/security nearby
 *       "node"   needs old/full/network/bitcoin/upgrade nearby
 *   - Python- and Bitcoin-specific vocabulary is included in the same lists
 *
 * HEURISTIC: cue lists were written from the taxonomy in the Influence Miner
 * paper and from reading PEP 572 / PEP 308 threads; they have not been
 * validated against a gold standard yet.
 */
public class InfluenceTypeDetector {

    public static final String[] TYPES = {
            "strategic", "operational", "functional", "tactical", "authority", "compatibility",
            "security", "standards", "ecosystem", "economic", "organizational", "coalition", "user_demand"
    };

    private static final Map<String, String[]> CUES = new LinkedHashMap<String, String[]>();
    private static final Map<String, Pattern[]> COMPILED = new LinkedHashMap<String, Pattern[]>();

    /* cue -> words that must appear within NEAR_WINDOW characters for the cue to count */
    private static final Map<String, String[]> NEAR_REQUIRED = new LinkedHashMap<String, String[]>();
    private static final int NEAR_WINDOW = 60;

    static {
        CUES.put("strategic", new String[] {
                "long term", "long-term", "longterm", "future direction", "project direction", "direction of the language",
                "direction of python", "philosophy", "principle", "principles", "values", "identity", "governance",
                "vision", "simplicity", "readability", "maintainability", "decentralization", "decentralisation",
                "decentralized", "decentralised", "zen of python", "the zen", "pythonic", "unpythonic",
                "language design", "design philosophy", "big picture", "in the long run", "for the future",
                "consistency of the language", "one obvious way", "explicit is better", "censorship resistance",
                "protocol conservatism", "core values"
        });
        CUES.put("operational", new String[] {
                "implement", "implementation", "implemented", "implementing", "maintain", "maintenance", "maintaining",
                "release", "releases", "migration", "migrate", "migrating", "deployment", "deploy", "deployed",
                "testing", "test suite", "tests", "documentation", "docs", "rollout", "backport", "backports",
                "schedule", "timeline", "feature freeze", "beta", "alpha", "reference implementation", "patch",
                "patches", "cpython", "bugfix release", "maintenance burden", "maintenance cost", "tooling",
                "build", "packaging", "activation", "signalling", "signaling"
        });
        CUES.put("functional", new String[] {
                "solves", "solve the problem", "does not solve", "doesn't solve", "use case", "use cases", "usecase",
                "edge case", "edge cases", "corner case", "functionality", "feature", "api", "behaviour", "behavior",
                "semantics", "semantic", "alternative design", "better approach", "problem statement", "the problem",
                "what problem", "actually needed", "real problem", "does it solve", "the same thing", "simpler way",
                "syntax", "readable", "unreadable", "expressiveness", "special case", "cleaner", "clearer"
        });
        CUES.put("tactical", new String[] {
                "to summarize", "to summarise", "summary", "summarizing", "summarising", "let us decide", "let's decide",
                "can we agree", "compromise", "reframe", "narrow", "clarify", "clarification", "evidence", "defer",
                "postpone", "next step", "next steps", "call for", "rough consensus", "let's move on", "move on",
                "step back", "the real question", "the question is", "to be clear", "concrete proposal", "concrete example",
                "show me", "data", "benchmark", "benchmarks", "poll", "vote", "straw poll", "let's focus", "focus on",
                "off topic", "off-topic", "separate thread", "separate pep", "separate proposal", "split this"
        });
        CUES.put("authority", new String[] {
                "bdfl", "steering council", "delegate", "pep delegate", "bdfl-delegate", "core developer", "core developers",
                "core dev", "core devs", "committer", "committers", "maintainer", "maintainers", "bip editor", "editor",
                "release manager", "decision maker", "pronounce", "pronouncement", "pronounced", "veto", "expert",
                "experts", "guido", "gvr", "final say", "final decision", "the decision is", "i have decided",
                "i'm accepting", "i am accepting", "i'm rejecting", "i am rejecting", "accepted the pep", "rejected the pep",
                "ruling", "authority", "in charge", "the council"
        });
        CUES.put("compatibility", new String[] {
                "backward compatibility", "backwards compatibility", "backward compatible", "backwards compatible",
                "backward-compatible", "backwards-compatible", "compatible", "incompatible", "incompatibility",
                "breaking change", "breaks existing", "break existing", "existing code", "existing users", "existing programs",
                "legacy", "interoperability", "migration path", "old nodes", "old clients", "old versions", "older versions",
                "python 2", "python2", "py2", "py3", "python 3", "2to3", "deprecate", "deprecated", "deprecation",
                "regression", "regressions", "break", "breaks", "broke", "broken", "soft fork", "softfork", "hard fork",
                "hardfork", "consensus change", "non-upgraded", "upgrade path", "transition"
        });
        CUES.put("security", new String[] {
                "security", "secure", "insecure", "attack", "attacks", "attacker", "vulnerability", "vulnerabilities",
                "vulnerable", "risk", "risky", "unsafe", "safety", "attack surface", "privacy", "robust", "robustness",
                "denial of service", "ddos", "exploit", "exploitable", "threat", "consensus failure", "malicious",
                "double spend", "double-spend", "51%", "reorg", "chain split", "network split", "fraud", "spam",
                "sandbox", "injection", "footgun", "foot-gun", "dangerous", "data loss", "corruption", "crash", "crashes"
        });
        CUES.put("standards", new String[] {
                "standard", "standards", "standardize", "standardise", "standardized", "standardised", "specification",
                "spec", "rfc", "rfcs", "convention", "conventions", "conventional", "interoperable", "interoperate",
                "compliance", "compliant", "protocol standard", "iso", "ieee", "posix", "unicode standard", "w3c",
                "de facto standard", "de-facto standard", "best practice", "best practices", "pep 8", "pep8",
                "style guide", "consistent with"
        });
        CUES.put("ecosystem", new String[] {
                "ecosystem", "downstream", "library", "libraries", "wallet", "wallets", "exchange", "exchanges", "miner",
                "miners", "mining pool", "node", "nodes", "third party", "third-party", "3rd party", "package", "packages",
                "client", "clients", "pypi", "stdlib", "standard library", "numpy", "django", "twisted", "cython", "pypy",
                "jython", "ironpython", "distributions", "linux distros", "distros", "vendors", "tools", "ide", "ides",
                "editors", "linters", "type checkers", "frameworks", "framework", "lightning", "spv", "full node",
                "full nodes", "the network", "other implementations", "alternative implementations"
        });
        CUES.put("economic", new String[] {
                "cost", "costs", "costly", "expensive", "cheap", "funding", "funded", "incentive", "incentives",
                "fee", "fees", "fee market", "market", "business", "businesses", "commercial", "commercially",
                "resources", "resource cost", "economic", "economics", "economy", "money", "paid", "pay for",
                "budget", "block reward", "mempool", "revenue", "profit", "financial", "man-hours", "person-hours",
                "developer time", "time and effort", "effort required"
        });
        CUES.put("organizational", new String[] {
                "company", "companies", "foundation", "psf", "organization", "organisation", "organizations",
                "organisations", "working group", "team", "release team", "committee", "institution", "sponsor",
                "sponsors", "sponsored", "employer", "my employer", "at work", "google", "microsoft", "dropbox",
                "red hat", "canonical", "enterprise", "corporate", "blockstream", "chaincode", "bitcoin core",
                "core team"
        });
        CUES.put("coalition", new String[] {
                "several people", "many people", "several others", "many others", "we agree", "we all", "we all agree",
                "consensus seems", "consensus is", "strong support", "broad support", "widespread support",
                "multiple developers", "many developers", "several developers", "others have said", "as others",
                "as others have", "there is agreement", "community agrees", "general agreement", "everyone agrees",
                "most people", "most of us", "nobody wants", "no one wants", "the majority", "overwhelming",
                "unanimous", "unanimously", "+1 from me too", "me too", "i agree with", "agree with everyone",
                "as X said", "as mentioned by", "seconded", "second that", "ditto", "same here", "likewise"
        });
        CUES.put("user_demand", new String[] {
                "users want", "users need", "user demand", "people ask", "people want", "people keep asking",
                "users keep asking", "keep asking", "keeps asking", "asking for this", "asked for this", "users ask",
                "frequently requested", "frequently asked", "common request", "commonly requested", "pain point",
                "pain points", "confusing for users", "confuses users", "beginners", "beginner", "newcomers", "newbies",
                "newbie", "students", "teaching", "learners", "real users", "real-world", "real world", "in practice",
                "adoption", "adopted", "widely used", "popular", "demand", "asked for", "requested", "wish list",
                "wishlist", "faq", "stack overflow", "stackoverflow", "tutorial", "end users", "end-users", "the users"
        });

        NEAR_REQUIRED.put("break", new String[] { "compatib", "existing", "code", "api", "user", "change", "backward", "program", "script", "software", "thing" });
        NEAR_REQUIRED.put("breaks", NEAR_REQUIRED.get("break"));
        NEAR_REQUIRED.put("broke", NEAR_REQUIRED.get("break"));
        NEAR_REQUIRED.put("broken", NEAR_REQUIRED.get("break"));
        NEAR_REQUIRED.put("risk", new String[] { "security", "attack", "regression", "break", "compat", "data", "safety", "danger", "vulnerab", "crash", "corrupt" });
        NEAR_REQUIRED.put("risky", NEAR_REQUIRED.get("risk"));
        NEAR_REQUIRED.put("team", new String[] { "release", "core", "dev", "steering", "security", "infrastructure", "packaging" });
        NEAR_REQUIRED.put("node", new String[] { "old", "full", "network", "bitcoin", "upgrade", "run", "operator" });
        NEAR_REQUIRED.put("nodes", NEAR_REQUIRED.get("node"));
        NEAR_REQUIRED.put("market", new String[] { "fee", "share", "economic", "incentive", "price", "business" });
        NEAR_REQUIRED.put("data", new String[] { "show", "evidence", "measure", "benchmark", "number", "statistic", "collect" });
        NEAR_REQUIRED.put("build", new String[] { "system", "process", "farm", "bot", "break", "fail", "windows", "linux" });
        NEAR_REQUIRED.put("tools", new String[] { "third", "external", "ide", "editor", "lint", "analysis", "existing", "many" });
        NEAR_REQUIRED.put("editor", new String[] { "pep", "bip", "the editor" });
        NEAR_REQUIRED.put("editors", new String[] { "pep", "bip" });
        NEAR_REQUIRED.put("client", new String[] { "old", "other", "third", "bitcoin", "library", "server" });
        NEAR_REQUIRED.put("clients", NEAR_REQUIRED.get("client"));
        NEAR_REQUIRED.put("popular", new String[] { "request", "demand", "feature", "library", "package", "idiom", "widely" });
        NEAR_REQUIRED.put("demand", new String[] { "user", "people", "strong", "real", "little", "no ", "much" });
        NEAR_REQUIRED.put("patch", new String[] { "submit", "attach", "review", "apply", "write", "wrote", "reference", "implement" });
        NEAR_REQUIRED.put("patches", NEAR_REQUIRED.get("patch"));
        NEAR_REQUIRED.put("tests", new String[] { "test suite", "unit", "regression", "pass", "fail", "write", "run", "coverage" });
        NEAR_REQUIRED.put("vote", new String[] { "let", "call", "take", "should", "we" });
        NEAR_REQUIRED.put("transition", new String[] { "period", "path", "plan", "smooth", "python", "version" });
        NEAR_REQUIRED.put("crash", new String[] { "interpreter", "python", "segfault", "will", "can", "cause" });
        NEAR_REQUIRED.put("crashes", NEAR_REQUIRED.get("crash"));
        NEAR_REQUIRED.put("spam", new String[] { "attack", "network", "transaction", "list" });

        for (Map.Entry<String, String[]> e : CUES.entrySet()) {
            String[] cues = e.getValue();
            Pattern[] ps = new Pattern[cues.length];
            for (int i = 0; i < cues.length; i++) {
                ps[i] = compileCue(cues[i]);
            }
            COMPILED.put(e.getKey(), ps);
        }
    }

    private static Pattern compileCue(String cue) {
        String q = Pattern.quote(cue.toLowerCase());
        String left = Character.isLetterOrDigit(cue.charAt(0)) ? "(?<![a-z0-9])" : "";
        String right = Character.isLetterOrDigit(cue.charAt(cue.length() - 1)) ? "(?![a-z0-9])" : "";
        return Pattern.compile(left + q + right);
    }

    /*
     * Returns a comma-separated list of influence types, or "none".
     */
    public static String detectTypes(String sentence) {
        Map<String, List<String>> evidence = detectWithEvidence(sentence);
        if (evidence.isEmpty()) {
            return "none";
        }
        return join(new ArrayList<String>(evidence.keySet()));
    }

    /*
     * "compatibility: backward compatibility|breaking change; security: attack surface"
     */
    public static String detectEvidenceCues(String sentence) {
        Map<String, List<String>> evidence = detectWithEvidence(sentence);
        if (evidence.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> e : evidence.entrySet()) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(e.getKey()).append(": ");
            List<String> cues = e.getValue();
            for (int i = 0; i < cues.size(); i++) {
                if (i > 0) sb.append('|');
                sb.append(cues.get(i));
            }
        }
        return sb.toString();
    }

    public static Map<String, List<String>> detectWithEvidence(String sentence) {

        Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        if (sentence == null) {
            return result;
        }
        String s = sentence.toLowerCase();

        for (Map.Entry<String, Pattern[]> e : COMPILED.entrySet()) {
            String type = e.getKey();
            String[] cues = CUES.get(type);
            Pattern[] ps = e.getValue();
            List<String> hits = new ArrayList<String>();
            for (int i = 0; i < ps.length; i++) {
                Matcher m = ps[i].matcher(s);
                while (m.find()) {
                    String cue = cues[i];
                    if (NEAR_REQUIRED.containsKey(cue) && !containsNear(s, m.start(), m.end(), NEAR_REQUIRED.get(cue))) {
                        continue;
                    }
                    if (!hits.contains(cue)) {
                        hits.add(cue);
                    }
                    break;
                }
            }
            if (!hits.isEmpty()) {
                result.put(type, hits);
            }
        }

        return result;
    }

    /*
     * Primary type = the type whose cue appears first in the sentence would be
     * ideal, but the ordered map already lists types in taxonomy order, which
     * is stable and easy to explain. We keep the first entry.
     */
    public static String primaryType(String influenceTypes) {

        if (influenceTypes == null || influenceTypes.trim().length() == 0 || influenceTypes.equals("none")) {
            return "none";
        }
        String[] parts = influenceTypes.split(",");
        return parts.length == 0 ? "none" : parts[0].trim();
    }

    public static int countMatchedCues(String sentence) {
        int n = 0;
        for (List<String> hits : detectWithEvidence(sentence).values()) {
            n += hits.size();
        }
        return n;
    }

    public static boolean containsAny(String text, String[] cues) {
        if (text == null) return false;
        String s = text.toLowerCase();
        for (int i = 0; i < cues.length; i++) {
            if (containsPhrase(s, cues[i])) return true;
        }
        return false;
    }

    public static boolean containsPhrase(String text, String phrase) {
        if (text == null || phrase == null) return false;
        return compileCue(phrase).matcher(text.toLowerCase()).find();
    }

    /*
     * True when any of the given fragments occurs within NEAR_WINDOW chars
     * either side of the [start,end) span.
     */
    public static boolean containsNear(String text, int start, int end, String[] fragments) {
        int from = Math.max(0, start - NEAR_WINDOW);
        int to = Math.min(text.length(), end + NEAR_WINDOW);
        String window = text.substring(from, start) + " " + text.substring(end, to);
        for (int i = 0; i < fragments.length; i++) {
            if (window.contains(fragments[i])) return true;
        }
        return false;
    }

    public static boolean containsNear(String text, String cue, String[] fragments) {
        Matcher m = compileCue(cue).matcher(text.toLowerCase());
        while (m.find()) {
            if (containsNear(text.toLowerCase(), m.start(), m.end(), fragments)) return true;
        }
        return false;
    }

    private static String join(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(values.get(i));
        }
        return sb.toString();
    }
}
