package conflictMiner;

/*
 * ConflictOutcomeImpactAnalyzer
 *
 * Compares local conflict stance with the final proposal decision.
 * This is descriptive only. It does not claim causal impact.
 */
public class ConflictOutcomeImpactAnalyzer {

    public static String analyzeImpact(String stance, String resolutionStatus, String finalDecision) {

        String st = safeLower(stance);
        String rs = safeLower(resolutionStatus);
        String decision = safeLower(finalDecision);

        if (decision.length() == 0 || decision.equals("unknown")) {
            return "outcome_unknown";
        }

        if (rs.equals("unresolved_objection")) {
            if (isRejectedDecision(decision)) {
                return "unresolved_objection_aligned_with_rejection";
            }
            if (isAcceptedDecision(decision)) {
                return "unresolved_objection_overridden_by_acceptance";
            }
            if (isDeferredDecision(decision)) {
                return "unresolved_objection_aligned_with_deferral";
            }
            return "unresolved_objection_outcome_unclear";
        }

        if (st.equals("oppose") || st.equals("concern") || st.equals("rebuttal")) {
            if (isRejectedDecision(decision)) {
                return "opposition_aligned_with_rejection";
            }
            if (isAcceptedDecision(decision)) {
                return "opposition_overridden_by_acceptance";
            }
            if (isDeferredDecision(decision)) {
                return "opposition_aligned_with_deferral";
            }
        }

        if (st.equals("support")) {
            if (isAcceptedDecision(decision)) {
                return "support_aligned_with_acceptance";
            }
            if (isRejectedDecision(decision)) {
                return "support_overridden_by_rejection";
            }
        }

        if (st.equals("compromise")) {
            if (isAcceptedDecision(decision)) {
                return "compromise_preceded_acceptance";
            }
            if (isDeferredDecision(decision)) {
                return "compromise_preceded_deferral";
            }
            return "compromise_outcome_unclear";
        }

        if (st.equals("procedural")) {
            return "procedural_conflict_" + decision;
        }

        return "no_clear_alignment";
    }

    private static boolean isAcceptedDecision(String decision) {
        return decision.contains("accept") || decision.contains("final") || decision.contains("implemented");
    }

    private static boolean isRejectedDecision(String decision) {
        return decision.contains("reject") || decision.contains("withdraw") || decision.contains("superseded");
    }

    private static boolean isDeferredDecision(String decision) {
        return decision.contains("defer") || decision.contains("postpone") || decision.contains("draft");
    }

    private static String safeLower(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase();
    }
}
