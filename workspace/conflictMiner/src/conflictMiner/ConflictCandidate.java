package conflictMiner;

/*
 * ConflictCandidate
 *
 * Data model for one conflict-bearing sentence extracted from an OSS
 * proposal discussion.
 */
public class ConflictCandidate {

    public String proposalIdentifier;
    public int proposalNumber;

    public String messageId;
    public String threadId;
    public String parentMessageId;

    public String authorName;
    public String authorEmail;
    public String authorRole;

    public String messageDate;
    public String sentence;

    public String conflictTypes;
    public String primaryConflictType;
    public String conflictStance;
    public String conflictTarget;
    public String resolutionStatus;
    public String civilityStatus;

    public int intensityLevel;
    public String intensityLabel;
    public double score;

    public String finalDecision;
    public String decisionImpact;
    public boolean unresolvedObjection;
}
