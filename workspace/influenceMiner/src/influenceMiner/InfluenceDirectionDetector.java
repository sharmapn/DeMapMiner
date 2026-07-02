package influenceMiner;

/*
 * Detects the likely direction of an influence sentence.
 *
 * supporting: pushes the proposal toward acceptance/adoption
 * blocking: pushes the proposal toward rejection/withdrawal
 * revising: pushes the proposal toward modification, compromise, delay, or clarification
 * neutral: influence type may be present, but direction is not obvious
 */
public class InfluenceDirectionDetector {

    public static String detectDirection(String sentence) {

        if (sentence == null) {
            return "neutral";
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("+1") ||
            s.contains("i support") ||
            s.contains("i agree") ||
            s.contains("i am in favour") ||
            s.contains("i am in favor") ||
            s.contains("looks good") ||
            s.contains("good idea") ||
            s.contains("should be accepted") ||
            s.contains("move forward") ||
            s.contains("proceed") ||
            s.contains("endorse") ||
            s.contains("approve")
        ) {
            return "supporting";
        }

        if (
            s.contains("-1") ||
            s.contains("i oppose") ||
            s.contains("i object") ||
            s.contains("i disagree") ||
            s.contains("i am against") ||
            s.contains("should be rejected") ||
            s.contains("reject this") ||
            s.contains("cannot accept") ||
            s.contains("should not") ||
            s.contains("shouldn't") ||
            s.contains("nack") ||
            s.contains("veto") ||
            s.contains("too risky") ||
            s.contains("breaks existing")
        ) {
            return "blocking";
        }

        if (
            s.contains("revise") ||
            s.contains("revision") ||
            s.contains("modify") ||
            s.contains("change the proposal") ||
            s.contains("alternative") ||
            s.contains("compromise") ||
            s.contains("defer") ||
            s.contains("postpone") ||
            s.contains("clarify") ||
            s.contains("reword") ||
            s.contains("update the pep") ||
            s.contains("update the bip") ||
            s.contains("narrow the scope")
        ) {
            return "revising";
        }

        return "neutral";
    }
}
