package influenceMiner;

import java.util.regex.Pattern;

/*
 * InfluenceDirectionDetector
 *
 * Conservative rule-based detector for the direction of an influence sentence:
 *
 *   supporting  pushes the proposal toward acceptance / adoption
 *   blocking    pushes the proposal toward rejection / withdrawal / closure
 *   revising    pushes toward modification, narrowing, deferral, clarification
 *   neutral     influence mechanism present but no clear direction
 *
 * Rules (HEURISTIC, in this order):
 *   1. explicit negated stance phrases are resolved first
 *        "I don't object"  -> supporting,  "I can't support" -> blocking
 *   2. explicit support and explicit block cues are counted; if BOTH fire in
 *      the same sentence the sentence is ambiguous and returned as neutral
 *   3. blocking > supporting > revising > neutral
 *   4. hedged sentences ("maybe", "not sure", "I wonder") never become
 *      supporting or blocking; they fall through to revising or neutral
 *
 * Votes: "+1", "+0.5", "-1", "-0" are matched only at a token boundary so
 * that "Python -1 day" or "x+1" do not count.
 */
public class InfluenceDirectionDetector {

    private static final Pattern PLUS_VOTE = Pattern.compile("(^|[\\s(\\[\"':,;])\\+\\s?(1|0\\.5|0)(?![\\w.])");
    private static final Pattern MINUS_VOTE = Pattern.compile("(^|[\\s(\\[\"':,;])-\\s?(1|0\\.5|0)(?![\\w.])");
    private static final Pattern PLUS_ZERO = Pattern.compile("(^|[\\s(\\[\"':,;])[+-]\\s?0(?![\\w.])");

    private static final String[] NEGATED_SUPPORT = { // reads as blocking
            "don't support", "do not support", "can't support", "cannot support", "won't support", "not in favour",
            "not in favor", "don't agree", "do not agree", "can't agree", "cannot agree", "don't like", "do not like",
            "not convinced", "unconvinced", "don't think this is a good idea", "not a good idea", "don't buy",
            "not sold on", "fail to see", "don't see the point", "don't see the need", "no need for", "not needed",
            "not worth", "isn't worth", "hate", "dislike", "not happy with", "unhappy with", "not a fan"
    };

    private static final String[] NEGATED_BLOCK = { // reads as supporting
            "don't object", "do not object", "no objection", "no objections", "don't oppose", "do not oppose",
            "not against", "not opposed", "wouldn't object", "would not object", "can live with", "could live with",
            "fine with", "fine by me", "ok with", "okay with", "no problem with", "have no problem"
    };

    private static final String[] SUPPORT = {
            "i support", "i'd support", "i would support", "i fully support", "i strongly support", "i agree",
            "i fully agree", "i strongly agree", "i completely agree", "i'm in favour", "i am in favour", "in favour of",
            "i'm in favor", "i am in favor", "in favor of", "looks good", "looks great", "sounds good", "sounds great",
            "good idea", "great idea", "excellent idea", "nice idea", "i like", "i love", "i really like",
            "should be accepted", "should accept", "let's accept", "move forward", "go ahead", "go for it", "let's do it",
            "i endorse", "endorse", "i approve", "approve", "i'm for", "i am for", "lgtm", "ack", "i vote for",
            "i vote yes", "count me in", "please accept", "ship it", "makes sense to me", "i'm convinced", "i am convinced",
            "you've convinced me", "convinced me", "i buy", "strongly in favour", "strongly in favor", "big fan",
            "this is the right", "the right thing", "definitely want", "i want this", "we need this", "we should do this",
            "should be added", "should go in", "belongs in", "worth doing", "worth having", "yes please", "please do"
    };

    private static final String[] BLOCK = {
            "i oppose", "i'm opposed", "i am opposed", "strongly oppose", "i object", "i strongly object", "i disagree",
            "i strongly disagree", "i'm against", "i am against", "against this", "against the pep", "against the proposal",
            "should be rejected", "should reject", "must be rejected", "reject this", "reject the pep", "reject the proposal",
            "please reject", "cannot accept", "can't accept", "can't be accepted", "cannot be accepted", "should not be accepted",
            "shouldn't be accepted", "should not accept", "shouldn't accept", "should not go in", "shouldn't go in",
            "doesn't belong", "does not belong", "nack", "veto", "too risky", "too dangerous", "too complex",
            "too complicated", "too magical", "too ugly", "bad idea", "terrible idea", "horrible idea", "awful idea",
            "i vote no", "i vote against", "please don't", "please do not", "must not", "over my dead body",
            "no way", "absolutely not", "i'd reject", "i would reject", "kill this", "kill the pep", "drop this", "drop the pep",
            "withdraw the pep", "should be withdrawn", "not worth it", "waste of", "abomination", "unacceptable",
            "i'm not in favour", "i'm not in favor", "i am not in favour", "i am not in favor", "i really don't", "strong no",
            /* gatekeeping phrasings (Sept 2026) */
            "not going to happen", "will not merge", "won't merge", "not going to merge", "dead on arrival", "won't fly",
            "not a chance", "i refuse", "i will revert", "i'll revert", "the answer is no", "-1000", "-100"
    };

    private static final String[] REVISE = {
            "revise", "revised", "revision", "modify", "modified", "modification", "change the proposal", "change the pep",
            "change the bip", "alternative", "alternatively", "compromise", "defer", "deferred", "postpone", "postponed",
            "clarify", "clarified", "clarification", "reword", "rewrite", "rephrase", "update the pep", "update the bip",
            "updated pep", "narrow the scope", "narrower", "reduce the scope", "limit this to", "restrict this to",
            "needs more work", "needs work", "more work", "more evidence", "more data", "more thought", "more discussion",
            "should be updated", "should be changed", "should be clarified", "should be amended", "should be reworked",
            "i suggest changing", "i'd suggest", "i would suggest", "suggest", "could be improved", "can be improved",
            "improve", "tweak", "adjust", "instead of", "rather than", "what about", "how about", "why not", "consider",
            "reconsider", "amend", "amendment", "split", "separate pep", "separate proposal", "not yet", "premature",
            "wait until", "hold off", "table this", "put on hold", "if you change", "if it were", "would be better if",
            "i'd prefer", "i would prefer", "prefer", "conditional", "provided that", "as long as", "unless"
    };

    private static final String[] HEDGES = {
            "not sure", "maybe", "perhaps", "i wonder", "i'm not certain", "i am not certain", "possibly",
            "i guess", "i suppose", "it might", "could be", "don't know", "no strong opinion", "no opinion", "meh"
    };

    private static final String[] QUESTION_WORDS = { "?", "why ", "how ", "what ", "does anyone", "is there" };

    public static String detectDirection(String sentence) {

        if (sentence == null) {
            return "neutral";
        }
        String s = " " + sentence.toLowerCase().trim() + " ";

        boolean hedged = InfluenceTypeDetector.containsAny(s, HEDGES);
        boolean plusVote = PLUS_VOTE.matcher(s).find() && !PLUS_ZERO.matcher(s).find();
        boolean minusVote = MINUS_VOTE.matcher(s).find() && !PLUS_ZERO.matcher(s).find();

        boolean support = plusVote;
        boolean block = minusVote;

        // 1. negated stance phrases (checked before the plain phrases they contain)
        if (InfluenceTypeDetector.containsAny(s, NEGATED_BLOCK)) support = true;
        if (InfluenceTypeDetector.containsAny(s, NEGATED_SUPPORT)) block = true;

        // 2. plain explicit cues, skipping ones already covered by a negation
        String stripped = s;
        for (int i = 0; i < NEGATED_BLOCK.length; i++) stripped = stripped.replace(NEGATED_BLOCK[i], " ");
        for (int i = 0; i < NEGATED_SUPPORT.length; i++) stripped = stripped.replace(NEGATED_SUPPORT[i], " ");
        if (InfluenceTypeDetector.containsAny(stripped, SUPPORT)) support = true;
        if (InfluenceTypeDetector.containsAny(stripped, BLOCK)) block = true;

        // Questions rarely carry a committed direction ("Should this be rejected?").
        boolean question = s.trim().endsWith("?");

        if (support && block) {
            return "neutral"; // contradictory cues: ambiguous, leave for human review
        }
        if (block && !hedged && !question) {
            return "blocking";
        }
        if (support && !hedged && !question) {
            return "supporting";
        }
        if (InfluenceTypeDetector.containsAny(s, REVISE)) {
            return "revising";
        }
        if ((block || support) && (hedged || question)) {
            // weak stance: treat as a request to revisit rather than a firm push
            return "revising";
        }
        return "neutral";
    }

    public static boolean isHedged(String sentence) {
        return sentence != null && InfluenceTypeDetector.containsAny(" " + sentence.toLowerCase() + " ", HEDGES);
    }
}
