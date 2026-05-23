package sentimentMiner;

public class SentimentClassifier {

    public static String classify(String sentence) {

        if (sentence == null) {
            return "neutral";
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("great") ||
            s.contains("excellent") ||
            s.contains("good idea") ||
            s.contains("well done") ||
            s.contains("i like") ||
            s.contains("i support") ||
            s.contains("happy")
        ) {
            return "positive";
        }

        if (
            s.contains("bad decision") ||
            s.contains("terrible") ||
            s.contains("awful") ||
            s.contains("frustrated") ||
            s.contains("angry") ||
            s.contains("toxic") ||
            s.contains("burnout") ||
            s.contains("i don't like") ||
            s.contains("i disagree")
        ) {
            return "negative";
        }

        return "neutral";
    }

    public static boolean detectStressSignal(String sentence) {

        if (sentence == null) {
            return false;
        }

        String s = sentence.toLowerCase();

        return (
            s.contains("burnout") ||
            s.contains("exhausted") ||
            s.contains("frustrated") ||
            s.contains("too much") ||
            s.contains("cannot continue") ||
            s.contains("stepping down") ||
            s.contains("fatigue")
        );
    }

    public static boolean detectToxicitySignal(String sentence) {

        if (sentence == null) {
            return false;
        }

        String s = sentence.toLowerCase();

        return (
            s.contains("ridiculous") ||
            s.contains("stupid") ||
            s.contains("nonsense") ||
            s.contains("toxic") ||
            s.contains("waste of time") ||
            s.contains("absurd")
        );
    }
}
