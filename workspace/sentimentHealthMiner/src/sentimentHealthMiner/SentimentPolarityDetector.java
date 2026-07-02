package sentimentHealthMiner;

/*
 * SentimentPolarityDetector
 *
 * Assigns a simple polarity label from the detected categories.
 */
public class SentimentPolarityDetector {

    public static String detectPolarity(String sentence, String categories) {

        boolean positive = false;
        boolean negative = false;

        if (categories != null) {
            positive = categories.contains("positive_agreement")
                    || categories.contains("appreciation_gratitude")
                    || categories.contains("reconciliation_repair")
                    || categories.contains("confidence_decisiveness");

            negative = categories.contains("constructive_concern")
                    || categories.contains("strong_objection")
                    || categories.contains("frustration")
                    || categories.contains("exhaustion_burnout")
                    || categories.contains("toxicity_disrespect")
                    || categories.contains("authority_challenge")
                    || categories.contains("exclusion_isolation");
        }

        if (positive && negative) {
            return "mixed";
        }
        if (positive) {
            return "positive";
        }
        if (negative) {
            return "negative";
        }

        if (sentence == null) {
            return "unknown";
        }

        String s = sentence.toLowerCase();

        if (containsAny(s, new String[] { "good", "great", "excellent", "useful", "helpful", "thanks" })) {
            return "positive";
        }

        if (containsAny(s, new String[] { "bad", "wrong", "broken", "dangerous", "confusing", "unacceptable" })) {
            return "negative";
        }

        return "neutral";
    }

    private static boolean containsAny(String s, String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (s.contains(words[i])) {
                return true;
            }
        }
        return false;
    }
}
