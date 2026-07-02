package conflictMiner;

/*
 * ConflictResolutionDetector
 *
 * Detects local textual signs that a conflict is unresolved, clarified,
 * revised, deferred, or closed by authority. Full resolution analysis should
 * later be done at thread/episode level.
 */
public class ConflictResolutionDetector {

    public static String detectResolutionStatus(String sentence) {

        if (sentence == null) {
            return "none";
        }

        String s = sentence.toLowerCase();

        if (containsAny(s, new String[] {
                "still not addressed", "not addressed", "unresolved objection",
                "my objection remains", "this remains unresolved", "open objection",
                "still a blocker", "blocking issue"
        })) {
            return "unresolved_objection";
        }

        if (containsAny(s, new String[] {
                "compromise", "middle ground", "revised", "revision", "updated proposal",
                "changed the proposal", "address this by", "to address this",
                "one way to resolve", "if we changed", "if this is changed"
        })) {
            return "compromise_or_revision";
        }

        if (containsAny(s, new String[] {
                "clarify", "clarification", "to be clear", "i mean", "what i meant",
                "this was misunderstood", "misunderstanding", "explained"
        })) {
            return "clarified";
        }

        if (containsAny(s, new String[] {
                "accepted", "i accept", "we accept", "that addresses my concern",
                "concern addressed", "i am satisfied", "i'm satisfied", "resolved for me"
        })) {
            return "accepted_by_actor";
        }

        if (containsAny(s, new String[] {
                "reject", "rejected", "should reject", "should be rejected",
                "not acceptable", "unacceptable", "cannot accept"
        })) {
            return "rejected_by_actor";
        }

        if (containsAny(s, new String[] {
                "defer", "deferred", "postpone", "later", "not now",
                "future version", "future pep", "future bip", "revisit"
        })) {
            return "deferred";
        }

        if (containsAny(s, new String[] {
                "final decision", "decision is", "i pronounce", "steering council decided",
                "delegate decided", "bdfl pronouncement", "rough consensus is",
                "this is now accepted", "this is now rejected"
        })) {
            return "authority_closure";
        }

        if (containsAny(s, new String[] {
                "question", "open question", "can someone explain", "how do we resolve",
                "what is the decision", "where do we go from here"
        })) {
            return "open_question";
        }

        return "none";
    }

    public static boolean isUnresolvedObjection(String resolutionStatus) {
        return resolutionStatus != null && resolutionStatus.equals("unresolved_objection");
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
