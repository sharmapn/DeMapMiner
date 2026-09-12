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
 * Rule-based, multi-label detector for the influence mechanisms: the thirteen
 * of the original taxonomy plus seven "controversial" mechanisms (unilateral
 * decision, corporate interest, gatekeeping, exit threat, incivility,
 * backchannel and procedural control) added in September 2026.
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
            "security", "standards", "ecosystem", "economic", "organizational", "coalition", "user_demand",
            /* controversial mechanisms, added Sept 2026 */
            "unilateral", "corporate_interest", "gatekeeping", "exit_threat", "incivility", "backchannel",
            "procedural_control"
    };

    /* The seven mechanisms the OSS-governance literature treats as contested. */
    public static final String[] CONTROVERSIAL_TYPES = {
            "unilateral", "corporate_interest", "gatekeeping", "exit_threat", "incivility", "backchannel",
            "procedural_control"
    };

    public static boolean isControversial(String type) {
        for (int i = 0; i < CONTROVERSIAL_TYPES.length; i++) {
            if (CONTROVERSIAL_TYPES[i].equals(type)) return true;
        }
        return false;
    }

    /* True when the comma-separated influence_types value carries at least one controversial mechanism. */
    public static boolean hasControversial(String influenceTypes) {
        if (influenceTypes == null) return false;
        String[] parts = influenceTypes.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (isControversial(parts[i].trim())) return true;
        }
        return false;
    }

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

        /* ------------------------------------------------------------------
         * Controversial mechanisms (Sept 2026). These are the forms of influence
         * the OSS-governance literature treats as contested: decisions by fiat,
         * corporate stakes, ownership-based gatekeeping, exit threats, hostile
         * pressure, decisions taken off-list and the use of process rules to
         * close a discussion. The lists are precision-oriented: most cues are
         * first-person or explicit phrases, and the broad ones only count near
         * a disambiguating word (NEAR_REQUIRED below).
         * ------------------------------------------------------------------ */
        CUES.put("unilateral", new String[] {
                "i have decided", "i've decided", "i decided", "my decision", "my final decision", "executive decision",
                "i'm going to accept", "i am going to accept", "i'm going to reject", "i am going to reject",
                "i'm going to pronounce", "i am going to pronounce", "i hereby", "i pronounce", "consider it accepted",
                "consider it rejected", "consider this rejected", "consider this accepted", "end of discussion",
                "end of story", "discussion is over", "the discussion is closed", "case closed", "no further discussion",
                "this is final", "final answer", "i overrule", "overruled", "overruling", "unilateral", "unilaterally",
                "by fiat", "fiat", "dictator", "dictatorial", "dictatorship", "benevolent dictator", "my call",
                "not up for debate", "not open for discussion", "not open to discussion", "not negotiable",
                "non-negotiable", "whether you like it or not", "like it or not", "i will merge", "i'm merging",
                "i am merging", "i'll just merge", "i'll merge it", "i just committed", "i've committed",
                "i've already committed", "already merged", "already committed", "already decided", "it's decided",
                "it is decided", "decision has been made", "the decision has been made", "the decision is made",
                "pull rank", "pulling rank", "pulled rank", "fait accompli", "i don't need consensus",
                "don't need a vote", "we don't vote", "not a democracy", "isn't a democracy", "is not a democracy",
                "i'm the bdfl", "i am the bdfl", "as bdfl", "as the bdfl", "bdfl decision", "bdfl pronouncement",
                "my pronouncement", "i get to decide", "i decide", "i'll decide", "i will decide", "i make the call",
                "i've made up my mind", "made up my mind", "my mind is made up", "i have spoken", "so let it be"
        });
        CUES.put("corporate_interest", new String[] {
                "my employer", "our employer", "at my company", "my company", "our company", "at work we", "at work,",
                "$work", "my day job", "day job", "i work at", "i work for", "we at google", "at google", "at dropbox",
                "at microsoft", "at facebook", "at instagram", "at red hat", "at redhat", "at canonical", "at bloomberg",
                "at jetbrains", "at meta", "at amazon", "at intel", "at ibm", "at oracle", "at mozilla", "at nvidia",
                "at quansight", "at anaconda", "at continuum", "at enthought", "at yelp", "at linkedin", "at twitter",
                "at netflix", "at spotify", "at shopify", "at stripe", "at cisco", "at rackspace", "at zope corporation",
                "in our codebase", "our codebase", "our code base", "our production", "in production at",
                "production code at", "our customers", "our product", "our products", "paid to", "being paid",
                "getting paid", "paid by", "paid for by", "funded by", "funding for", "funding from", "grant",
                "sponsored by", "sponsorship", "commercial interest", "commercial interests", "business interest",
                "business interests", "business needs", "business reasons", "business case", "vendor lock",
                "vendor lock-in", "conflict of interest", "conflicts of interest", "corporate agenda",
                "corporate interests", "corporate interest", "company's interest", "company's needs", "company agenda",
                "internal fork", "internal patch", "internal patches", "our internal", "internally at",
                "we use it internally", "use it internally", "we depend on", "we rely on", "we need this for",
                "we need this at", "customer demand", "customers are asking", "customers want", "customers need",
                "enterprise customers", "enterprise users", "commercial users", "commercial support", "contract",
                "consulting", "my clients", "our clients", "clients pay", "who pays", "follow the money",
                "full-time", "full time", "hired", "hire", "employs", "employed by", "employee", "employees",
                "on the clock", "company time", "work hours", "the company i work", "company i work for",
                "corporations", "corporation", "management wants", "management has", "my manager", "my boss"
        });
        CUES.put("gatekeeping", new String[] {
                "i will not merge", "i won't merge", "i'm not going to merge", "i am not going to merge",
                "won't be merged", "will not be merged", "not going to be merged", "i will not accept", "i won't accept",
                "i'm not going to accept", "i am not going to accept", "not going to happen", "won't happen",
                "will not happen", "not happening", "over my dead body", "i refuse", "i'll refuse", "i will refuse",
                "i will revert", "i'll revert", "i'm reverting", "i am reverting", "revert it", "reverted your",
                "i will block", "i'll block", "i'm blocking", "i am blocking", "i block", "i veto", "i'll veto",
                "i will veto", "my veto", "i object", "i strongly object", "i formally object", "formal objection",
                "not on my watch", "no way", "absolutely not", "hell no", "never going to", "i will never",
                "i'll never accept", "i'll never approve", "as long as i'm", "as long as i am", "while i'm the",
                "while i am the", "my module", "my code", "my package", "i own", "i maintain", "i'm the maintainer",
                "i am the maintainer", "as the maintainer", "as maintainer", "as the author of", "as the original author",
                "as its author", "as its maintainer", "i wrote", "i designed", "i created", "i'm the author",
                "i am the author", "i'm responsible for", "i am responsible for", "you need my", "requires my approval",
                "my approval", "my sign-off", "sign-off from me", "my blessing", "needs my ok", "not without my",
                "dead on arrival", "won't fly", "will not fly", "doesn't fly", "no chance", "not a chance",
                "zero chance", "won't get in", "will not get in", "isn't going in", "is not going in", "not going in",
                "reject it outright", "outright rejection", "rejected outright", "strong -1", "hard -1", "firm -1",
                "-1000", "-100", "a big -1", "big -1", "i'm -1", "i am -1", "my -1", "not in my lifetime",
                "the answer is no", "answer is no", "the answer remains no", "no means no", "asked and answered",
                "i said no", "i've said no", "i already said no"
        });
        CUES.put("exit_threat", new String[] {
                "i'll fork", "i will fork", "fork the project", "fork python", "fork cpython", "fork the language",
                "we'll fork", "we will fork", "threaten to fork", "threat of a fork", "hostile fork", "forking python",
                "i'm leaving", "i am leaving", "i'll leave", "i will leave", "i quit", "i'm quitting", "i am quitting",
                "i resign", "i'm resigning", "i am resigning", "resignation", "step down", "stepping down",
                "stepped down", "i'm stepping down", "i'm done", "i am done", "count me out", "i give up",
                "i'm giving up", "i am giving up", "i'm walking away", "walk away", "walking away", "take my ball",
                "i'll stop contributing", "stop contributing", "stop maintaining", "no longer maintain", "i'll withdraw",
                "i withdraw", "withdrawing my", "i'm withdrawing", "i am withdrawing", "withdraw the pep",
                "withdraw this pep", "withdraw my pep", "abandon the pep", "abandon this pep", "i'm abandoning",
                "i am abandoning", "if this goes in", "if this is accepted", "if this pep is accepted",
                "if this is rejected", "if this gets rejected", "if this gets in", "you'll lose", "you will lose",
                "python will lose", "lose contributors", "lose me", "drive away", "driving away", "driven away",
                "burn out", "burned out", "burnt out", "burnout", "permanent vacation", "vacation from", "tired of",
                "sick of", "fed up", "last straw", "ultimatum", "not worth my time", "waste of my time",
                "waste my time", "wasting my time", "no longer worth", "done with this", "i'm done here",
                "my last message", "last message on this", "unsubscribe", "unsubscribed", "unsubscribing",
                "leaving the list", "leave the list", "leave python-dev", "leave python-ideas", "leaving python-dev",
                "leaving python-ideas", "mute this thread", "muting this thread", "muting the thread", "i'm muting",
                "i am muting", "fight so hard", "don't want to fight", "no longer want to", "i no longer",
                "no energy", "out of energy", "exhausted", "exhausting", "demoralizing", "demoralising",
                "demoralized", "demoralised", "disheartening", "disheartened", "i'm tired", "i am tired",
                "i'm exhausted", "i am exhausted", "take a break from", "taking a break from", "hiatus", "retire",
                "retiring", "retired from", "hand over", "handing over", "hand it over", "someone else can",
                "someone else will have to", "find someone else"
        });
        CUES.put("incivility", new String[] {
                "stupid", "idiotic", "idiot", "idiots", "moron", "moronic", "dumb", "ridiculous", "absurd", "nonsense",
                "nonsensical", "laughable", "insane", "crazy idea", "braindead", "brain-dead", "brain dead", "clueless",
                "incompetent", "ignorant", "ignorance", "pathetic", "garbage", "crap", "crappy", "bullshit", "wtf",
                "shut up", "stfu", "fuck", "fucking", "screw this", "screw it", "troll", "trolling", "trolls", "flame",
                "flamewar", "flame war", "flaming", "flamefest", "ranting", "whining", "whine", "childish", "grow up",
                "get a life", "you clearly don't", "you obviously don't", "you don't understand", "you have no idea",
                "you have no clue", "you people", "you guys never", "waste of time", "waste of everyone's time",
                "wasting everyone's time", "wasting our time", "bikeshed", "bikeshedding", "bike shed", "bike-shed",
                "bike-shedding", "bike shedding", "paint the bikeshed", "ad hominem", "personal attack",
                "personal attacks", "insult", "insulting", "insulted", "insults", "rude", "rudeness", "hostile",
                "hostility", "toxic", "toxicity", "condescending", "condescension", "patronizing", "patronising",
                "arrogant", "arrogance", "dismissive", "bullying", "bully", "bullied", "harass", "harassment",
                "harassing", "abusive", "aggressive", "passive-aggressive", "passive aggressive", "snark", "snarky",
                "sarcasm", "sarcastic", "nasty", "mean-spirited", "disrespectful", "disrespect", "code of conduct",
                "civility", "uncivil", "incivility", "the tone of", "your tone", "tone down", "calm down",
                "take a deep breath", "*sigh*", "facepalm", "eye roll", "rolls eyes", "seriously?", "are you kidding",
                "you must be joking", "give me a break", "oh please", "yeah right", "who cares", "nobody cares",
                "no one cares", "get over it", "deal with it", "cry me a river", "boo hoo", "shouting", "yelling",
                "screaming", "name-calling", "name calling", "belittle", "belittling", "mocking", "ridicule",
                "ridiculing", "contempt", "contemptuous", "attacking me", "attacking you", "attacking him",
                "attacking her", "unprofessional", "out of line", "over the line", "crossed a line", "crosses a line",
                "pissed", "piss off", "pissing", "angry"
        });
        CUES.put("backchannel", new String[] {
                "offline", "off-line", "off list", "off-list", "offlist", "privately", "in private", "private email",
                "private mail", "private message", "private discussion", "private conversation", "private channel",
                "private chat", "behind closed doors", "closed doors", "in person", "face to face", "face-to-face",
                "at the sprint", "at the sprints", "during the sprint", "core sprint", "core dev sprint",
                "core developer sprint", "language summit", "at pycon", "at europython", "at the summit",
                "at the conference", "hallway", "hallway track", "over lunch", "over dinner", "over beer", "over beers",
                "over drinks", "on irc", "on zulip", "on discord", "on slack", "in the chat", "on the phone",
                "phone call", "video call", "zoom call", "on a call", "we discussed this", "we already discussed",
                "as discussed with", "as agreed with", "i talked to", "i've talked to", "i spoke to", "i spoke with",
                "i talked with", "i have talked to", "i discussed this with", "i've discussed this with", "guido and i",
                "guido told me", "guido has told me", "guido agreed", "guido already agreed", "the council and i",
                "was decided at", "decided at the", "agreed at the", "internal discussion", "internal mailing list",
                "python-committers", "committers list", "the committers list", "private list", "secret", "secretly",
                "not public", "non-public", "wasn't public", "was not public", "without public", "no public discussion",
                "without discussion", "without any discussion", "without community", "without consulting",
                "without asking", "no one was consulted", "nobody was consulted", "wasn't consulted", "weren't consulted",
                "already been decided", "has already been decided", "was already decided", "already agreed",
                "pre-decided", "predecided", "done deal", "foregone conclusion", "rubber stamp", "rubber-stamp",
                "rubberstamp", "smoke-filled", "cabal", "inner circle", "old boys", "clique", "insiders", "insider",
                "internally", "in the back room", "back room", "backroom", "back-room", "back channel", "backchannel",
                "back-channel", "out of band", "out-of-band", "lack of transparency", "not transparent", "opaque"
        });
        CUES.put("procedural_control", new String[] {
                "wrong list", "wrong venue", "wrong forum", "wrong place", "not the right place", "not the right venue",
                "not the place for", "not the venue", "not the right list", "not the right forum", "belongs on",
                "take it to", "take this to", "take that to", "move this to", "move it to", "please move",
                "should go to python", "python-ideas is the", "go to python-ideas", "post to python-ideas",
                "post it to python-ideas", "python-ideas, not", "belongs on python-ideas", "python-list is",
                "this list is for", "this list is not", "not for this list", "not appropriate for this list",
                "off-topic for", "off topic for", "out of scope", "outside the scope", "beyond the scope",
                "not in scope", "scope creep", "per pep 1", "pep 1 says", "pep 1 requires", "according to pep 1",
                "the pep process", "pep process", "process requires", "the process requires", "follow the process",
                "not following the process", "didn't follow the process", "proper channels", "proper channel",
                "through the proper", "wrong process", "procedural", "procedure", "procedurally", "on procedural grounds",
                "moderator", "moderators", "moderation", "moderated", "i'm locking", "i am locking", "locking this",
                "lock this thread", "locked this thread", "thread locked", "closing this thread", "close this thread",
                "this thread is closed", "closing the thread", "i'm closing this", "thread is over", "muted this thread",
                "let's end this thread", "end this thread", "kill this thread", "please stop", "stop posting",
                "stop replying", "stop this thread", "drop it", "let it go", "let this die", "let it die",
                "let the thread die", "run its course", "we've been over this", "been over this", "already discussed",
                "already been discussed", "discussed to death", "discussed many times", "discussed before", "rehash",
                "rehashing", "rehashed", "beating a dead horse", "dead horse", "flogging a dead horse",
                "here we go again", "comes up every", "every few months", "every few years", "read the archives",
                "search the archives", "check the archives", "the archives", "needs a sponsor", "need a sponsor",
                "find a sponsor", "no sponsor", "without a sponsor", "core dev sponsor", "needs a pep", "write a pep",
                "needs to be a pep", "requires a pep", "submit a pep", "needs a champion", "needs a reference implementation",
                "no reference implementation", "not a complete pep", "incomplete pep", "too late for", "missed the deadline",
                "after the freeze", "after beta", "not in time for", "not the time", "not the right time", "wrong time",
                "bad timing", "not how we do things", "not how things work", "that's not how", "not the way we",
                "rules are rules", "against the rules", "policy says", "letter of the", "by the book", "pep 13",
                "pep 8001", "pep 8016", "per the pep", "as the pep says", "formal process", "formal vote"
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

        /* controversial mechanisms: broad cues that only count near a disambiguating word */
        String[] ownership = { "won't", "will not", "not ", "reject", "refuse", "decide", "so i", "and i", "therefore",
                "object", "veto", "no ", "never", "final", "approve", "accept", "my say", "responsib", "i say", "i think" };
        NEAR_REQUIRED.put("my module", ownership);
        NEAR_REQUIRED.put("my code", ownership);
        NEAR_REQUIRED.put("my package", ownership);
        NEAR_REQUIRED.put("i own", ownership);
        NEAR_REQUIRED.put("i maintain", ownership);
        NEAR_REQUIRED.put("i wrote", ownership);
        NEAR_REQUIRED.put("i designed", ownership);
        NEAR_REQUIRED.put("i created", ownership);
        NEAR_REQUIRED.put("i'm responsible for", ownership);
        NEAR_REQUIRED.put("i am responsible for", ownership);
        NEAR_REQUIRED.put("as the author of", ownership);
        NEAR_REQUIRED.put("as the original author", ownership);
        String[] refusalObject = { "pep", "proposal", "merge", "accept", "approve", "this", "that", "it ", "change", "feature", "syntax" };
        NEAR_REQUIRED.put("no way", new String[] { "accept", "merge", "pep", "proposal", "i'm", "i am", "going to", "i will", "allow", "approve", "happen", "in hell" });
        NEAR_REQUIRED.put("no chance", refusalObject);
        NEAR_REQUIRED.put("won't happen", refusalObject);
        NEAR_REQUIRED.put("will not happen", refusalObject);
        NEAR_REQUIRED.put("not happening", refusalObject);
        NEAR_REQUIRED.put("never going to", new String[] { "accept", "merge", "approve", "happen", "agree", "allow", "get in", "go in", "be accepted" });
        NEAR_REQUIRED.put("my call", new String[] { "it's", "it is", "this is", "that's", "final", "make", "not your" });
        NEAR_REQUIRED.put("fiat", new String[] { "by", "decision", "decree", "bdfl", "guido", "council" });
        NEAR_REQUIRED.put("already merged", new String[] { "pep", "change", "without", "before", "anyway", "discussion", "so ", "already merged it", "i " });
        NEAR_REQUIRED.put("already committed", NEAR_REQUIRED.get("already merged"));
        NEAR_REQUIRED.put("i decide", new String[] { "what", "whether", "if ", "that", "how", "when", "which", "to " });
        NEAR_REQUIRED.put("like it or not", new String[] { "whether", "this", "that", "is ", "will", "going" });

        String[] employerContext = { "employer", "company", "work", "paid", "corporate", "business", "customer", "product",
                "commercial", "fund", "sponsor", "google", "microsoft", "dropbox", "red hat", "canonical", "enterprise" };
        NEAR_REQUIRED.put("grant", new String[] { "psf", "funding", "money", "receive", "awarded", "fund", "pay", "sponsor" });
        NEAR_REQUIRED.put("contract", new String[] { "work", "paid", "company", "client", "fund", "consult", "hire", "employ" });
        NEAR_REQUIRED.put("consulting", new String[] { "work", "client", "paid", "company", "do ", "my ", "business", "gig" });
        NEAR_REQUIRED.put("full-time", new String[] { "paid", "work", "on this", "employ", "hire", "developer", "job", "salary" });
        NEAR_REQUIRED.put("full time", NEAR_REQUIRED.get("full-time"));
        NEAR_REQUIRED.put("hired", employerContext);
        NEAR_REQUIRED.put("hire", employerContext);
        NEAR_REQUIRED.put("employs", employerContext);
        NEAR_REQUIRED.put("employee", employerContext);
        NEAR_REQUIRED.put("employees", employerContext);
        NEAR_REQUIRED.put("we depend on", new String[] { "work", "company", "production", "product", "customer", "internally", "codebase", "business" });
        NEAR_REQUIRED.put("we rely on", NEAR_REQUIRED.get("we depend on"));
        NEAR_REQUIRED.put("corporation", new String[] { "large", "big", "interest", "agenda", "influence", "control", "dominat", "money", "fund" });
        NEAR_REQUIRED.put("corporations", NEAR_REQUIRED.get("corporation"));
        NEAR_REQUIRED.put("management has", new String[] { "decided", "asked", "told", "want", "require", "approved", "mandated" });

        String[] exitFirstPerson = { "i'll", "i will", "i'm", "i am", "leave", "quit", "fork", "stop", "done", "withdraw",
                "resign", "never", "me ", "my " };
        NEAR_REQUIRED.put("if this goes in", exitFirstPerson);
        NEAR_REQUIRED.put("if this is accepted", exitFirstPerson);
        NEAR_REQUIRED.put("if this pep is accepted", exitFirstPerson);
        NEAR_REQUIRED.put("if this is rejected", exitFirstPerson);
        NEAR_REQUIRED.put("if this gets rejected", exitFirstPerson);
        NEAR_REQUIRED.put("if this gets in", exitFirstPerson);
        NEAR_REQUIRED.put("walk away", new String[] { "i ", "me", "from this", "from python", "from the", "i'll", "i'd" });
        NEAR_REQUIRED.put("hiatus", new String[] { "i ", "me", "my", "from python", "from core", "from the", "taking", "on " });
        NEAR_REQUIRED.put("retire", NEAR_REQUIRED.get("hiatus"));
        NEAR_REQUIRED.put("retiring", NEAR_REQUIRED.get("hiatus"));
        NEAR_REQUIRED.put("retired from", NEAR_REQUIRED.get("hiatus"));
        NEAR_REQUIRED.put("hand over", new String[] { "maintain", "my", "i ", "to someone", "the reins", "responsib", "ownership" });
        NEAR_REQUIRED.put("handing over", NEAR_REQUIRED.get("hand over"));
        NEAR_REQUIRED.put("hand it over", NEAR_REQUIRED.get("hand over"));
        NEAR_REQUIRED.put("someone else can", new String[] { "maintain", "i won't", "i'm not", "i am not", "i don't", "i'm done", "take over", "champion", "sponsor", "write", "do it" });
        NEAR_REQUIRED.put("someone else will have to", NEAR_REQUIRED.get("someone else can"));
        NEAR_REQUIRED.put("find someone else", NEAR_REQUIRED.get("someone else can"));
        NEAR_REQUIRED.put("no longer want to", new String[] { "contribut", "maintain", "participat", "work on", "care", "fight", "support", "spend", "argue", "discuss" });
        NEAR_REQUIRED.put("i no longer", NEAR_REQUIRED.get("no longer want to"));
        NEAR_REQUIRED.put("i'm done", new String[] { "with this", "here", "arguing", "discussing", "with python", "with the", "fighting", "talking", "trying" });
        NEAR_REQUIRED.put("i am done", NEAR_REQUIRED.get("i'm done"));
        String[] burnoutContext = { "i ", "me", "my", "this discussion", "this thread", "this pep", "contributor", "maintainer", "core dev", "people", "everyone" };
        NEAR_REQUIRED.put("exhausted", burnoutContext);
        NEAR_REQUIRED.put("exhausting", burnoutContext);
        NEAR_REQUIRED.put("demoralizing", burnoutContext);
        NEAR_REQUIRED.put("demoralising", burnoutContext);
        NEAR_REQUIRED.put("demoralized", burnoutContext);
        NEAR_REQUIRED.put("demoralised", burnoutContext);
        NEAR_REQUIRED.put("disheartening", burnoutContext);
        NEAR_REQUIRED.put("disheartened", burnoutContext);
        NEAR_REQUIRED.put("tired of", new String[] { "i'm", "i am", "we're", "we are", "getting", "people are", "everyone", "sick and" });
        NEAR_REQUIRED.put("sick of", NEAR_REQUIRED.get("tired of"));
        NEAR_REQUIRED.put("step down", new String[] { "i ", "i'll", "i'm", "guido", "bdfl", "as ", "from", "should", "will" });
        NEAR_REQUIRED.put("stepping down", NEAR_REQUIRED.get("step down"));
        NEAR_REQUIRED.put("stepped down", NEAR_REQUIRED.get("step down"));

        NEAR_REQUIRED.put("angry", new String[] { "you", "me", "people", "make", "getting", "why so", "i'm", "i am", "so " });
        NEAR_REQUIRED.put("insane", new String[] { "this is", "that's", "idea", "proposal", "would be", "is ", "crazy" });
        NEAR_REQUIRED.put("flame", new String[] { "war", "fest", "bait", "me", "you", "don't", "not ", "start", "this is", "thread" });
        NEAR_REQUIRED.put("insult", new String[] { "you", "me", "people", "to ", "an ", "is ", "not ", "intelligence", "personal" });
        NEAR_REQUIRED.put("aggressive", new String[] { "passive", "tone", "you", "being", "too ", "so ", "response", "reply", "post" });
        NEAR_REQUIRED.put("hostile", new String[] { "tone", "you", "being", "so ", "response", "reply", "post", "environment", "fork", "toward" });

        NEAR_REQUIRED.put("offline", new String[] { "discuss", "talk", "take", "conversation", "chat", "decid", "agree", "with", "meet", "this " });
        NEAR_REQUIRED.put("off-line", NEAR_REQUIRED.get("offline"));
        NEAR_REQUIRED.put("internally", new String[] { "discuss", "decid", "agree", "talk", "meeting", "among", "we ", "the council", "the team" });
        NEAR_REQUIRED.put("secret", new String[] { "decid", "discuss", "meeting", "agree", "kept", "cabal", "in secret", "no secret", "not a secret" });
        NEAR_REQUIRED.put("in person", new String[] { "discuss", "talk", "meet", "decid", "agree", "conversation", "sprint", "summit", "pycon" });
        String[] venueContext = { "discuss", "talk", "decid", "agree", "meet", "conversation", "we ", "i ", "chat", "spoke", "told" };
        NEAR_REQUIRED.put("at pycon", venueContext);
        NEAR_REQUIRED.put("at europython", venueContext);
        NEAR_REQUIRED.put("at the conference", venueContext);
        NEAR_REQUIRED.put("at the summit", venueContext);
        NEAR_REQUIRED.put("at the sprint", venueContext);
        NEAR_REQUIRED.put("at the sprints", venueContext);
        NEAR_REQUIRED.put("during the sprint", venueContext);
        NEAR_REQUIRED.put("opaque", new String[] { "process", "decision", "how ", "why ", "council", "governance", "to the community", "to outsiders" });
        NEAR_REQUIRED.put("insider", new String[] { "knowledge", "only", "decid", "core", "club", "few", "handful", "not an" });
        NEAR_REQUIRED.put("insiders", NEAR_REQUIRED.get("insider"));

        NEAR_REQUIRED.put("procedure", new String[] { "pep", "process", "follow", "correct", "proper", "wrong", "standard", "according", "formal" });
        NEAR_REQUIRED.put("belongs on", new String[] { "python-ideas", "python-list", "python-dev", "the list", "another list", "a different", "discourse", "the tracker", "bug tracker" });
        NEAR_REQUIRED.put("the archives", new String[] { "read", "search", "check", "look", "see", "in ", "dig", "consult" });
        NEAR_REQUIRED.put("drop it", new String[] { "please", "just", "let's", "should", "i'd", "suggest", "time to" });
        NEAR_REQUIRED.put("let it go", NEAR_REQUIRED.get("drop it"));
        NEAR_REQUIRED.put("please stop", new String[] { "this", "posting", "replying", "arguing", "repeating", "the ", "with", "now" });
        NEAR_REQUIRED.put("letter of the", new String[] { "pep", "law", "rule", "process", "spec", "policy" });
        NEAR_REQUIRED.put("by the book", new String[] { "process", "pep", "go ", "do ", "did", "done", "play", "everything" });
        NEAR_REQUIRED.put("not the time", new String[] { "now is", "this is", "to ", "for " });
        NEAR_REQUIRED.put("wrong time", NEAR_REQUIRED.get("not the time"));
        NEAR_REQUIRED.put("moderation", new String[] { "list", "thread", "post", "queue", "moderator", "need", "heavy", "team", "policy" });
        NEAR_REQUIRED.put("moderated", new String[] { "list", "thread", "post", "being", "was", "is ", "get", "should" });

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
