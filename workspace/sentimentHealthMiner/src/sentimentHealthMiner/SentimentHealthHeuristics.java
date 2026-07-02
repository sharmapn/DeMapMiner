package sentimentHealthMiner;

/*
 * SentimentHealthHeuristics
 *
 * Computes transparent first-version support and risk scores. This is intended
 * to be inspectable and replaceable by ML/LLM classification later.
 */
public class SentimentHealthHeuristics {

    public static double scoreSupport(
            String sentence,
            String authorRole,
            String categories,
            String polarity,
            String target,
            String healthSignals,
            String healthDimensions,
            int intensityLevel) {

        double score = 0.0;

        if (sentence == null) {
            return score;
        }

        if (containsAny(categories, new String[] {
                "positive_agreement", "appreciation_gratitude", "reconciliation_repair", "confidence_decisiveness"
        })) {
            score += 1.0;
        }

        if ("positive".equals(polarity)) {
            score += 0.8;
        } else if ("mixed".equals(polarity)) {
            score += 0.3;
        }

        if (containsAny(healthSignals, new String[] {
                "support_recognition", "repair_recovery", "consensus_signal", "positive_climate"
        })) {
            score += 1.0;
        }

        if (containsAny(healthDimensions, new String[] {
                "post_decision_recovery", "participation_structure"
        })) {
            score += 0.4;
        }

        if (target != null && !target.equals("unknown")) {
            score += 0.2;
        }

        score += Math.min(1.0, intensityLevel * 0.15);
        score += roleWeight(authorRole);

        return score;
    }

    public static double scoreRisk(
            String sentence,
            String authorRole,
            String categories,
            String polarity,
            String target,
            String healthSignals,
            String healthDimensions,
            int intensityLevel) {

        double score = 0.0;

        if (sentence == null) {
            return score;
        }

        String s = sentence.toLowerCase();

        if (containsAny(categories, new String[] {
                "constructive_concern", "strong_objection", "frustration", "authority_challenge",
                "exhaustion_burnout", "toxicity_disrespect", "exclusion_isolation"
        })) {
            score += 1.0;
        }

        if ("negative".equals(polarity)) {
            score += 0.8;
        } else if ("mixed".equals(polarity)) {
            score += 0.5;
        }

        if (containsAny(healthSignals, new String[] {
                "unresolved_concern", "discussion_tension", "fatigue_burden",
                "toxicity_risk", "authority_pressure", "exclusion_or_isolation",
                "participation_warning", "negative_climate"
        })) {
            score += 1.1;
        }

        if (containsAny(healthDimensions, new String[] {
                "governance_stress", "sustainability_risk", "conflict_quality"
        })) {
            score += 0.7;
        }

        if ("governance".equals(target) || "actor_relationship".equals(target)) {
            score += 0.4;
        }

        if (containsAny(s, new String[] {
                "ignored", "not addressed", "no consensus", "unacceptable", "personal attack",
                "burnout", "burned out", "step down", "stepping down", "i am done", "i'm done"
        })) {
            score += 1.0;
        }

        score += intensityLevel * 0.35;
        score += roleWeight(authorRole);

        if (containsAny(categories, new String[] { "reconciliation_repair", "appreciation_gratitude" })) {
            score -= 0.4;
        }

        return score;
    }

    public static String overallHealthLabel(double supportScore, double riskScore) {

        if (riskScore >= 4.0) {
            return "high_risk";
        }
        if (riskScore >= 3.0) {
            return "moderate_risk";
        }
        if (supportScore >= 3.0 && riskScore < 2.0) {
            return "healthy_supportive_signal";
        }
        if (supportScore >= 2.0 && riskScore < 2.5) {
            return "constructive_or_repair_signal";
        }
        if (riskScore > supportScore) {
            return "watch_signal";
        }
        if (supportScore > riskScore) {
            return "support_signal";
        }

        return "neutral_or_low_signal";
    }

    private static double roleWeight(String authorRole) {

        if (authorRole == null) {
            return 0.0;
        }

        String r = authorRole.toLowerCase();

        if (r.contains("bdfl")) {
            return 0.8;
        }
        if (r.contains("steering council")) {
            return 0.8;
        }
        if (r.contains("delegate")) {
            return 0.7;
        }
        if (r.contains("bip editor")) {
            return 0.7;
        }
        if (r.contains("core")) {
            return 0.6;
        }
        if (r.contains("maintainer")) {
            return 0.6;
        }
        if (r.contains("developer")) {
            return 0.4;
        }

        return 0.0;
    }

    private static boolean containsAny(String value, String[] words) {
        if (value == null) {
            return false;
        }
        for (int i = 0; i < words.length; i++) {
            if (value.contains(words[i])) {
                return true;
            }
        }
        return false;
    }
}
