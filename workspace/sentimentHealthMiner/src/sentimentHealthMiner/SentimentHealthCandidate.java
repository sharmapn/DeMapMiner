package sentimentHealthMiner;

/*
 * SentimentHealthCandidate
 *
 * Data model for one sentiment-bearing and/or community-health-relevant
 * sentence extracted from an OSS proposal discussion.
 */
public class SentimentHealthCandidate {

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

    public String sentimentCategories;
    public String primarySentimentCategory;
    public String sentimentPolarity;
    public String sentimentTarget;

    public String healthSignals;
    public String primaryHealthSignal;
    public String healthDimensions;
    public String primaryHealthDimension;

    public int intensityLevel;
    public String intensityLabel;

    public double healthSupportScore;
    public double healthRiskScore;
    public String overallHealthLabel;

    public String finalDecision;
    public String decisionRelationship;
}
