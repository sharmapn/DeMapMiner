package preferenceMiner;

public class PreferencePolarity {

    public static String detectPolarity(String sentence) {

        if (sentence == null) {
            return "neutral";
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("+1") ||
            s.contains("i support") ||
            s.contains("i like") ||
            s.contains("i agree") ||
            s.contains("i am in favour") ||
            s.contains("i am in favor") ||
            s.contains("this is a good idea") ||
            s.contains("i prefer") ||
            s.contains("looks good to me")
        ) {
            return "positive";
        }

        if (
            s.contains("-1") ||
            s.contains("i oppose") ||
            s.contains("i dislike") ||
            s.contains("i disagree") ||
            s.contains("i am against") ||
            s.contains("i object") ||
            s.contains("should be rejected") ||
            s.contains("bad idea") ||
            s.contains("not a good idea")
        ) {
            return "negative";
        }

        if (
            s.contains("i am not sure") ||
            s.contains("i'm not sure") ||
            s.contains("i have concerns") ||
            s.contains("i worry") ||
            s.contains("maybe") ||
            s.contains("on the other hand")
        ) {
            return "mixed";
        }

        return "neutral";
    }
}
