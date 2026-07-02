package sentimentHealthMiner;

/*
 * HealthSignalDetector
 *
 * Detects community-health signals from sentiment categories and lexical cues.
 */
public class HealthSignalDetector {

    public static String detectSignals(String sentence, String categories, String polarity) {

        if (sentence == null) {
            return "none";
        }

        String s = sentence.toLowerCase();
        String signals = "";

        if (hasCategory(categories, "positive_agreement") || hasCategory(categories, "appreciation_gratitude")) {
            signals = append(signals, "support_recognition");
        }

        if (hasCategory(categories, "constructive_concern")) {
            signals = append(signals, "constructive_disagreement");
        }

        if (hasCategory(categories, "strong_objection") || containsAny(s, new String[] {
                "unresolved", "not addressed", "still object", "still concerned", "no consensus"
        })) {
            signals = append(signals, "unresolved_concern");
        }

        if (hasCategory(categories, "frustration")) {
            signals = append(signals, "discussion_tension");
        }

        if (hasCategory(categories, "exhaustion_burnout") || containsAny(s, new String[] {
                "too much work", "no time", "maintainer burden", "burden on maintainers",
                "cannot continue", "can't continue", "step down", "stepping down"
        })) {
            signals = append(signals, "fatigue_burden");
        }

        if (hasCategory(categories, "toxicity_disrespect")) {
            signals = append(signals, "toxicity_risk");
        }

        if (hasCategory(categories, "authority_challenge")) {
            signals = append(signals, "authority_pressure");
        }

        if (hasCategory(categories, "exclusion_isolation")) {
            signals = append(signals, "exclusion_or_isolation");
        }

        if (hasCategory(categories, "reconciliation_repair") || containsAny(s, new String[] {
                "clarify", "clarification", "compromise", "middle ground", "fair point",
                "i see your point", "resolved", "addressed"
        })) {
            signals = append(signals, "repair_recovery");
        }

        if (containsAny(s, new String[] {
                "consensus", "rough consensus", "agreement", "broad support", "community support"
        })) {
            signals = append(signals, "consensus_signal");
        }

        if (containsAny(s, new String[] {
                "ignored", "dismissed", "nobody responded", "no one responded", "not heard"
        })) {
            signals = append(signals, "participation_warning");
        }

        if (containsAny(s, new String[] {
                "after acceptance", "after rejection", "move on", "closed", "final decision",
                "decision has been made", "accepted", "rejected", "withdrawn", "deferred"
        })) {
            signals = append(signals, "post_decision_signal");
        }

        if (signals.length() == 0 && polarity != null && polarity.equals("positive")) {
            signals = append(signals, "positive_climate");
        }

        if (signals.length() == 0 && polarity != null && polarity.equals("negative")) {
            signals = append(signals, "negative_climate");
        }

        if (signals.length() == 0) {
            return "none";
        }

        return signals;
    }

    public static String primarySignal(String signals) {
        if (signals == null || signals.trim().length() == 0) {
            return "none";
        }
        String[] parts = signals.split(",");
        if (parts.length == 0) {
            return "none";
        }
        return parts[0].trim();
    }

    private static boolean hasCategory(String categories, String category) {
        return categories != null && categories.contains(category);
    }

    private static boolean containsAny(String s, String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (s.contains(words[i])) {
                return true;
            }
        }
        return false;
    }

    static String append(String existing, String value) {
        if (existing == null || existing.length() == 0) {
            return value;
        }
        if (existing.contains(value)) {
            return existing;
        }
        return existing + "," + value;
    }
}
