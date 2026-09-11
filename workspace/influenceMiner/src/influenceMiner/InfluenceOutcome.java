package influenceMiner;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * InfluenceOutcome
 *
 * The resolved outcome of one proposal, used for outcome alignment and
 * temporal features. finalDecision is always one of the normalised values
 * in InfluenceOutcomeResolver.NORMALISED_STATUSES ("unknown" if nothing
 * could be found). Dates are ISO strings (yyyy-MM-dd[ HH:mm:ss]) or null.
 */
public class InfluenceOutcome {

    public String finalDecision;
    public String decisionDate;

    /* First accepted/rejected decision (PEP 308 was rejected in 2003 and accepted in 2005). */
    public String firstDecision;
    public String firstDecisionDate;

    /* Proposal creation date from pepdetails/proposaldetails, if known. */
    public String createdDate;

    /* Where the outcome came from: accrejpeps, states, csv, none. */
    public String source;

    public InfluenceOutcome() {
        this.finalDecision = "unknown";
        this.decisionDate = null;
        this.firstDecision = null;
        this.firstDecisionDate = null;
        this.createdDate = null;
        this.source = "none";
    }

    public InfluenceOutcome(String finalDecision, String decisionDate) {
        this();
        this.finalDecision = finalDecision == null ? "unknown" : finalDecision;
        this.decisionDate = decisionDate;
    }

    public boolean isKnown() {
        return finalDecision != null && !finalDecision.equals("unknown");
    }

    public LocalDateTime decisionDateTime() {
        return InfluenceTemporalAnalyzer.parseDateTime(decisionDate);
    }

    public LocalDate createdLocalDate() {
        LocalDateTime dt = InfluenceTemporalAnalyzer.parseDateTime(createdDate);
        return dt == null ? null : dt.toLocalDate();
    }

    public String toString() {
        return "InfluenceOutcome[" + finalDecision + " @ " + decisionDate + ", first=" + firstDecision
                + " @ " + firstDecisionDate + ", created=" + createdDate + ", source=" + source + "]";
    }
}
