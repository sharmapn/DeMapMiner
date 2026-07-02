package conflictMiner;

/*
 * ConflictHeuristics
 *
 * Computes a transparent first-version conflict score. This is intentionally
 * simple so that it can be inspected, revised, and later replaced by ML/LLM
 * classification.
 */
public class ConflictHeuristics {

    public static double scoreSentence(
            String sentence,
            String authorRole,
            String conflictTypes,
            String stance,
            String target,
            String resolutionStatus,
            String civilityStatus,
            int intensityLevel) {

        double score = 0.0;

        if (sentence == null) {
            return score;
        }

        String s = sentence.toLowerCase();

        if (conflictTypes != null && !conflictTypes.equals("none")) {
            score += 0.8;

            String[] types = conflictTypes.split(",");
            if (types.length > 1) {
                score += 0.3;
            }
            if (types.length > 2) {
                score += 0.3;
            }
        }

        if (stance != null && !stance.equals("neutral")) {
            score += 0.6;
        }

        if (stance != null && stance.equals("oppose")) {
            score += 0.5;
        }

        if (target != null && !target.equals("unknown")) {
            score += 0.3;
        }

        if (resolutionStatus != null) {
            if (resolutionStatus.equals("unresolved_objection")) {
                score += 1.0;
            } else if (resolutionStatus.equals("authority_closure")) {
                score += 0.7;
            } else if (resolutionStatus.equals("compromise_or_revision")) {
                score += 0.5;
            } else if (!resolutionStatus.equals("none")) {
                score += 0.3;
            }
        }

        if (civilityStatus != null) {
            if (civilityStatus.equals("uncivil")) {
                score += 0.7;
            } else if (civilityStatus.equals("tense")) {
                score += 0.4;
            }
        }

        score += intensityLevel * 0.35;

        if (containsAny(s, new String[] {
                "disagree", "object", "oppose", "reject", "nack", "-1",
                "not convinced", "not acceptable", "unacceptable", "wrong",
                "concern", "risk", "problem", "blocker", "no consensus"
        })) {
            score += 0.7;
        }

        if (containsAny(s, new String[] {
                "because", "therefore", "reason", "rationale", "evidence",
                "example", "tradeoff", "trade-off", "use case", "as a result"
        })) {
            score += 0.3;
        }

        if (containsAny(s, new String[] {
                "pep", "bip", "proposal", "this change", "this design",
                "this feature", "decision", "accepted", "rejected", "deferred"
        })) {
            score += 0.3;
        }

        if (authorRole != null) {
            String r = authorRole.toLowerCase();

            if (r.contains("bdfl")) {
                score += 0.8;
            } else if (r.contains("steering council")) {
                score += 0.8;
            } else if (r.contains("delegate")) {
                score += 0.7;
            } else if (r.contains("bip editor")) {
                score += 0.7;
            } else if (r.contains("core")) {
                score += 0.6;
            } else if (r.contains("maintainer")) {
                score += 0.6;
            } else if (r.contains("developer")) {
                score += 0.4;
            }
        }

        if (containsAny(s, new String[] {
                "maybe", "perhaps", "i wonder", "not sure", "might"
        })) {
            score -= 0.2;
        }

        return score;
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
