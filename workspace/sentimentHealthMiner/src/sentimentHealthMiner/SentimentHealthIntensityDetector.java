package sentimentHealthMiner;

/*
 * SentimentHealthIntensityDetector
 *
 * Estimates how strong a sentiment/health signal is on a 0-5 scale.
 */
public class SentimentHealthIntensityDetector {

    public static int detectIntensity(
            String sentence,
            String categories,
            String polarity,
            String healthSignals,
            String healthDimensions) {

        if (sentence == null) {
            return 0;
        }

        String s = sentence.toLowerCase();
        int intensity = 0;

        if (categories != null && !categories.equals("none")) {
            intensity = Math.max(intensity, 1);
        }

        if (polarity != null && (polarity.equals("positive") || polarity.equals("negative") || polarity.equals("mixed"))) {
            intensity = Math.max(intensity, 2);
        }

        if (containsAny(categories, new String[] {
                "strong_objection", "frustration", "authority_challenge", "confidence_decisiveness"
        })) {
            intensity = Math.max(intensity, 3);
        }

        if (containsAny(categories, new String[] {
                "exhaustion_burnout", "toxicity_disrespect", "exclusion_isolation"
        })) {
            intensity = Math.max(intensity, 4);
        }

        if (containsAny(healthSignals, new String[] {
                "fatigue_burden", "toxicity_risk", "authority_pressure", "unresolved_concern"
        })) {
            intensity = Math.max(intensity, 4);
        }

        if (containsAny(s, new String[] {
                "i cannot continue", "i can't continue", "i am done", "i'm done",
                "step down", "stepping down", "burned out", "burnt out",
                "personal attack", "hostile", "unacceptable behaviour", "unacceptable behavior"
        })) {
            intensity = Math.max(intensity, 5);
        }

        if (containsAny(s, new String[] {
                "strongly", "very", "extremely", "completely", "absolutely",
                "never", "always", "must", "cannot", "can't", "blocker"
        })) {
            intensity = Math.min(5, intensity + 1);
        }

        if (containsAny(s, new String[] {
                "maybe", "perhaps", "i wonder", "i may be wrong", "i might be wrong"
        }) && intensity > 0) {
            intensity = Math.max(1, intensity - 1);
        }

        return intensity;
    }

    public static String intensityLabel(int intensityLevel) {
        if (intensityLevel <= 0) {
            return "none";
        }
        if (intensityLevel == 1) {
            return "weak_signal";
        }
        if (intensityLevel == 2) {
            return "clear_affect";
        }
        if (intensityLevel == 3) {
            return "strong_affect_or_tension";
        }
        if (intensityLevel == 4) {
            return "high_health_risk";
        }
        return "severe_governance_or_sustainability_risk";
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
