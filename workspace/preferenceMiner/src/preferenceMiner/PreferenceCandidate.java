package preferenceMiner;

/*
 * Stores one extracted preference candidate.
 */
public class PreferenceCandidate {

    public int proposalNumber;
    public String proposalIdentifier;

    public String messageId;
    public String authorName;
    public String authorEmail;
    public String authorRole;

    public String messageDate;
    public String sentence;

    public String polarity;
    public double score;

    public String finalDecision;
    public boolean alignsWithDecision;

    public PreferenceCandidate() {
    }
}
