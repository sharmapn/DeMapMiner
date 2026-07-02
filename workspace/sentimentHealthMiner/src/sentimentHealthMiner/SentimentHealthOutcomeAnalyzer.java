package sentimentHealthMiner;

/*
 * SentimentHealthOutcomeAnalyzer
 *
 * Provides a first-pass interpretation of how a sentiment/health signal relates
 * to the final proposal outcome.
 */
public class SentimentHealthOutcomeAnalyzer {

    public static String analyzeRelationship(
            String polarity,
            String overallHealthLabel,
            String finalDecision,
            String healthDimensions) {

        if (finalDecision == null || finalDecision.trim().length() == 0) {
            finalDecision = "unknown";
        }

        String decision = finalDecision.toLowerCase();

        if (decision.contains("accepted") || decision.contains("approved") || decision.contains("final")) {
            if ("positive".equals(polarity)) {
                return "positive_signal_aligned_with_acceptance";
            }
            if ("negative".equals(polarity) && containsAny(healthDimensions, new String[] { "governance_stress", "sustainability_risk" })) {
                return "risk_signal_despite_acceptance";
            }
            if ("negative".equals(polarity)) {
                return "opposition_or_concern_before_acceptance";
            }
            return "neutral_or_mixed_signal_before_acceptance";
        }

        if (decision.contains("rejected") || decision.contains("declined")) {
            if ("negative".equals(polarity)) {
                return "negative_signal_aligned_with_rejection";
            }
            if ("positive".equals(polarity)) {
                return "support_signal_despite_rejection";
            }
            return "neutral_or_mixed_signal_before_rejection";
        }

        if (decision.contains("deferred") || decision.contains("postponed")) {
            if (overallHealthLabel != null && overallHealthLabel.contains("risk")) {
                return "health_risk_signal_before_deferral";
            }
            return "signal_before_deferral";
        }

        if (decision.contains("withdrawn")) {
            if (overallHealthLabel != null && overallHealthLabel.contains("risk")) {
                return "health_risk_signal_before_withdrawal";
            }
            return "signal_before_withdrawal";
        }

        if (overallHealthLabel != null && overallHealthLabel.contains("risk")) {
            return "health_risk_with_unknown_outcome";
        }

        return "unknown_outcome_relationship";
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
