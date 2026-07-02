package sentimentHealthMiner;

/*
 * HealthDimensionDetector
 *
 * Maps low-level health signals into broader community-health dimensions.
 */
public class HealthDimensionDetector {

    public static String detectDimensions(String signals, String categories, String target) {

        String dimensions = "";

        if (containsAny(signals, new String[] {
                "support_recognition", "positive_climate", "negative_climate", "discussion_tension"
        })) {
            dimensions = append(dimensions, "emotional_climate");
        }

        if (containsAny(signals, new String[] {
                "constructive_disagreement", "unresolved_concern", "toxicity_risk", "repair_recovery"
        })) {
            dimensions = append(dimensions, "conflict_quality");
        }

        if (containsAny(signals, new String[] {
                "exclusion_or_isolation", "participation_warning", "support_recognition"
        })) {
            dimensions = append(dimensions, "participation_structure");
        }

        if (containsAny(signals, new String[] {
                "authority_pressure", "consensus_signal"
        }) || "governance".equals(target)) {
            dimensions = append(dimensions, "governance_stress");
        }

        if (containsAny(signals, new String[] {
                "fatigue_burden", "toxicity_risk", "unresolved_concern"
        })) {
            dimensions = append(dimensions, "sustainability_risk");
        }

        if (containsAny(signals, new String[] {
                "repair_recovery", "post_decision_signal"
        })) {
            dimensions = append(dimensions, "post_decision_recovery");
        }

        if (dimensions.length() == 0 && categories != null && !categories.equals("none")) {
            dimensions = append(dimensions, "emotional_climate");
        }

        if (dimensions.length() == 0) {
            return "none";
        }

        return dimensions;
    }

    public static String primaryDimension(String dimensions) {
        if (dimensions == null || dimensions.trim().length() == 0) {
            return "none";
        }
        String[] parts = dimensions.split(",");
        if (parts.length == 0) {
            return "none";
        }
        return parts[0].trim();
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

    private static String append(String existing, String value) {
        if (existing == null || existing.length() == 0) {
            return value;
        }
        if (existing.contains(value)) {
            return existing;
        }
        return existing + "," + value;
    }
}
