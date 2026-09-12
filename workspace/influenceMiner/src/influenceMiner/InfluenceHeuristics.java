package influenceMiner;

/*
 * InfluenceHeuristics
 *
 * Computes a lightweight, additive influence score for a candidate sentence.
 * It mirrors the Preference Miner prototype: simple, transparent, and easy
 * to replace later with a trained classifier. Every component is HEURISTIC.
 *
 * Score components (typical range 0 .. ~5):
 *   +0.8   at least one influence type detected (+0.2 per extra type, max +0.4)
 *   +0.6   deontic / decision language (should, must, recommend, object ...)
 *   +0.4   justification language (because, therefore, for example, evidence)
 *   +0.5   explicit reference to the proposal (PEP/BIP, this change ...)
 *   +0.4   a non-neutral direction
 *   +0.2/0.4 internal|external / mixed scope
 *   +0.3   a recognised target
 *   +0.3..1.0 author role weight (BDFL 1.0 ... community member 0.2)
 *   +0.2..0.8 temporal proximity to the decision (only when the decision
 *            date is KNOWN and the message precedes the decision)
 *   -0.2   hedging (maybe, perhaps, not sure ...)
 *   -0.3   very short sentence (< 6 words) unless it is a vote
 *
 * Unknown temporal distance and post-decision messages add nothing, so
 * proposals without a resolved decision date are not artificially boosted.
 */
public class InfluenceHeuristics {

    public static double scoreSentence(
            String sentence,
            String authorRole,
            String influenceTypes,
            String influenceDirection,
            String influenceScope,
            String influenceTarget,
            int daysBeforeDecision) {

        double score = 0.0;

        if (sentence == null) {
            return score;
        }

        String s = " " + sentence.toLowerCase() + " ";

        if (influenceTypes != null && !influenceTypes.equals("none")) {
            score += 0.8;
            int n = influenceTypes.split(",").length;
            if (n > 1) score += 0.2;
            if (n > 2) score += 0.2;
        }

        if (InfluenceTypeDetector.containsAny(s, new String[] {
                "should", "must", "need to", "needs to", "have to", "cannot", "can't", "recommend", "suggest",
                "object", "reject", "accept", "agree", "disagree", "oppose", "support", "insist", "require", "propose",
                "strongly", "definitely", "absolutely", "clearly", "obviously", "important", "essential", "critical"
        })) {
            score += 0.6;
        }

        if (InfluenceTypeDetector.containsAny(s, new String[] {
                "because", "therefore", "as a result", "this means", "evidence", "example", "for example", "for instance",
                "since", "given that", "the reason", "consider that", "in practice", "in my experience", "we found",
                "i've seen", "i have seen", "shows that", "demonstrates", "proves"
        })) {
            score += 0.4;
        }

        if (InfluenceTypeDetector.containsAny(s, new String[] {
                "pep", "bip", "proposal", "this change", "this idea", "this design", "this feature", "this syntax",
                "the spec", "this approach"
        }) || s.matches(".*\\b(pep|bip)\\s?\\d+.*")) {
            score += 0.5;
        }

        if (influenceDirection != null && !influenceDirection.equals("neutral")) {
            score += 0.4;
        }

        if (influenceScope != null) {
            if (influenceScope.equals("mixed")) {
                score += 0.4;
            } else if (influenceScope.equals("internal") || influenceScope.equals("external")) {
                score += 0.2;
            }
        }

        if (influenceTarget != null && !influenceTarget.equals("unknown")) {
            score += 0.3;
        }

        score += roleWeight(authorRole);

        if (InfluenceTemporalAnalyzer.isKnown(daysBeforeDecision) && daysBeforeDecision >= 0) {
            if (daysBeforeDecision <= 7) {
                score += 0.8;
            } else if (daysBeforeDecision <= 30) {
                score += 0.5;
            } else if (daysBeforeDecision <= 90) {
                score += 0.2;
            }
        }

        if (InfluenceDirectionDetector.isHedged(sentence)) {
            score -= 0.2;
        }

        int words = sentence.trim().split("\\s+").length;
        if (words < 6 && !s.matches(".*[+-]\\s?[01].*")) {
            score -= 0.3;
        }

        return Math.round(score * 100.0) / 100.0;
    }

    /*
     * Role weight reflects formal decision power in the project, not evidence
     * that the particular sentence was influential.
     */
    public static double roleWeight(String authorRole) {
        if (authorRole == null) {
            return 0.0;
        }
        String r = authorRole.toLowerCase();

        if (r.contains("bdfl") && !r.contains("delegate")) return 1.0;
        if (r.contains("lead maintainer")) return 1.0;
        if (r.contains("steering council")) return 1.0;
        if (r.contains("delegate")) return 0.9;
        if (r.contains("bip editor")) return 0.9;
        if (r.contains("pep editor")) return 0.7;
        if (r.contains("proposal author") || r.contains("bip author")) return 0.7;
        if (r.contains("release manager")) return 0.8;
        if (r.contains("core")) return 0.8;
        if (r.contains("maintainer")) return 0.8;
        if (r.contains("developer")) return 0.6;
        if (r.contains("miner") || r.contains("wallet") || r.contains("exchange")) return 0.5;
        if (r.contains("community")) return 0.2;
        if (r.contains("user")) return 0.3;
        return 0.0;
    }
}
