package sentimentHealthMiner;

/*
 * SentimentCategoryDetector
 *
 * Lightweight multi-label detector for sentiment and affective categories
 * in OSS decision-making discussions.
 */
public class SentimentCategoryDetector {

    public static String detectCategories(String sentence) {

        if (sentence == null) {
            return "none";
        }

        String s = sentence.toLowerCase();
        String tokenized = " " + s.replaceAll("[^a-z0-9+\\-]", " ") + " ";
        String categories = "";

        if (containsAny(s, new String[] {
                "i agree", "we agree", "agree with", "sounds good", "makes sense",
                "i support", "we support", "support this", "i am in favor", "i am in favour",
                "+1", "looks good", "good idea"
        }) || tokenized.contains(" ack ")) {
            categories = append(categories, "positive_agreement");
        }

        if (containsAny(s, new String[] {
                "thanks", "thank you", "appreciate", "grateful", "nice work",
                "good work", "well done", "thanks for", "thank you for", "appreciated"
        })) {
            categories = append(categories, "appreciation_gratitude");
        }

        if (containsAny(s, new String[] {
                "concern", "worry", "worried", "risk", "problem", "issue",
                "i am not sure", "not sure", "might be confusing", "could confuse",
                "this may", "this might", "dangerous", "fragile", "too complex"
        })) {
            categories = append(categories, "constructive_concern");
        }

        if (containsAny(s, new String[] {
                "i object", "we object", "strongly object", "oppose", "i oppose",
                "we oppose", "reject this", "should be rejected", "unacceptable",
                "not acceptable", "strong -1", "-1", "nack", "strong nack"
        })) {
            categories = append(categories, "strong_objection");
        }

        if (containsAny(s, new String[] {
                "frustrated", "frustrating", "frustration", "annoying", "annoyed",
                "again and again", "we keep", "tired of", "fed up", "this is going nowhere",
                "how many times", "already discussed", "repeatedly"
        })) {
            categories = append(categories, "frustration");
        }

        if (containsAny(s, new String[] {
                "exhausted", "exhausting", "burned out", "burnt out", "burnout",
                "i cannot keep", "i can't keep", "too much", "i am done", "i'm done",
                "no longer have energy", "do not have energy", "step down", "stepping down"
        })) {
            categories = append(categories, "exhaustion_burnout");
        }

        if (containsAny(s, new String[] {
                "nonsense", "ridiculous", "absurd", "stupid", "idiotic",
                "bad faith", "troll", "insult", "offensive", "you clearly",
                "you obviously", "personal attack", "hostile", "shut up"
        })) {
            categories = append(categories, "toxicity_disrespect");
        }

        if (containsAny(s, new String[] {
                "who decides", "who gets to decide", "authority", "governance",
                "legitimate", "legitimacy", "bdfl", "steering council", "delegate",
                "pep delegate", "bip editor", "maintainer decision", "unilateral",
                "centralized", "centralised", "decision process"
        })) {
            categories = append(categories, "authority_challenge");
        }

        if (containsAny(s, new String[] {
                "sorry", "apologize", "apologise", "i may have misunderstood",
                "i might have misunderstood", "let me clarify", "to clarify", "let's step back",
                "lets step back", "compromise", "middle ground", "de-escalate", "deescalate",
                "i see your point", "fair point"
        })) {
            categories = append(categories, "reconciliation_repair");
        }

        if (containsAny(s, new String[] {
                "maybe", "perhaps", "possibly", "i wonder", "not sure", "unsure",
                "uncertain", "i might be wrong", "i may be wrong", "i may be missing",
                "i might be missing"
        })) {
            categories = append(categories, "uncertainty_doubt");
        }

        if (containsAny(s, new String[] {
                "i am confident", "we are confident", "clearly", "definitely",
                "certainly", "this is the right", "right direction", "i have decided",
                "we have decided", "final decision", "i accept", "accepted"
        })) {
            categories = append(categories, "confidence_decisiveness");
        }

        if (containsAny(s, new String[] {
                "ignored", "dismissed", "not heard", "not listened", "nobody responded",
                "no one responded", "my concern has not", "my concerns have not",
                "not addressed", "left out", "excluded"
        })) {
            categories = append(categories, "exclusion_isolation");
        }

        if (categories.length() == 0) {
            return "none";
        }

        return categories;
    }

    public static String primaryCategory(String categories) {

        if (categories == null || categories.trim().length() == 0) {
            return "none";
        }

        String[] parts = categories.split(",");
        if (parts.length == 0) {
            return "none";
        }

        return parts[0].trim();
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
