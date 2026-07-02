package influenceMiner;

/*
 * InfluenceHeuristics
 *
 * Computes a lightweight influence score for candidate sentences.
 * This mirrors the first Preference Miner prototype: simple, transparent,
 * and easy to replace later with ML or LLM-based classification.
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

        String s = sentence.toLowerCase();

        if (influenceTypes != null && !influenceTypes.equals("none")) {
            score += 0.8;

            String[] types = influenceTypes.split(",");
            if (types.length > 1) {
                score += 0.2;
            }
            if (types.length > 2) {
                score += 0.2;
            }
        }

        if (
            s.contains("should") ||
            s.contains("must") ||
            s.contains("need to") ||
            s.contains("cannot") ||
            s.contains("recommend") ||
            s.contains("suggest") ||
            s.contains("object") ||
            s.contains("reject") ||
            s.contains("accept") ||
            s.contains("agree") ||
            s.contains("disagree")
        ) {
            score += 0.6;
        }

        if (
            s.contains("because") ||
            s.contains("therefore") ||
            s.contains("as a result") ||
            s.contains("this means") ||
            s.contains("evidence") ||
            s.contains("example") ||
            s.contains("for example")
        ) {
            score += 0.4;
        }

        if (
            s.contains("pep") ||
            s.contains("bip") ||
            s.contains("proposal") ||
            s.contains("this change") ||
            s.contains("this idea") ||
            s.contains("this design")
        ) {
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

        if (authorRole != null) {
            String r = authorRole.toLowerCase();

            if (r.contains("bdfl")) {
                score += 1.0;
            } else if (r.contains("steering council")) {
                score += 1.0;
            } else if (r.contains("delegate")) {
                score += 0.9;
            } else if (r.contains("bip editor")) {
                score += 0.9;
            } else if (r.contains("core")) {
                score += 0.8;
            } else if (r.contains("maintainer")) {
                score += 0.8;
            } else if (r.contains("developer")) {
                score += 0.6;
            } else if (r.contains("miner") || r.contains("wallet") || r.contains("exchange")) {
                score += 0.5;
            } else if (r.contains("user")) {
                score += 0.3;
            }
        }

        if (daysBeforeDecision >= 0 && daysBeforeDecision <= 7) {
            score += 0.8;
        } else if (daysBeforeDecision > 7 && daysBeforeDecision <= 30) {
            score += 0.5;
        } else if (daysBeforeDecision > 30 && daysBeforeDecision <= 90) {
            score += 0.2;
        }

        if (
            s.contains("not sure") ||
            s.contains("maybe") ||
            s.contains("perhaps") ||
            s.contains("might") ||
            s.contains("i wonder")
        ) {
            score -= 0.2;
        }

        return score;
    }
}
