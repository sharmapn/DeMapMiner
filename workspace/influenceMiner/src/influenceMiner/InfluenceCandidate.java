package influenceMiner;

/*
 * Stores one extracted influence candidate.
 *
 * Influence Miner is sentence-based in this first prototype.
 * Each stored candidate represents one sentence that may have influenced
 * the direction, revision, acceptance, rejection, or delay of a proposal.
 */
public class InfluenceCandidate {

    public int proposalNumber;
    public String proposalIdentifier;

    public String messageId;
    public String authorName;
    public String authorEmail;
    public String authorRole;

    public String messageDate;
    public String sentence;

    public String influenceTypes;
    public String primaryInfluenceType;
    public String influenceScope;
    public String influenceDirection;
    public String influenceTarget;
    public double score;

    public String finalDecision;
    public boolean alignsWithOutcome;

    public InfluenceCandidate() {
    }
}
