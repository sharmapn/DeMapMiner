package sentimentMiner;

public class SentimentCandidate {

    public int proposalNumber;
    public String proposalIdentifier;

    public String messageId;
    public String sentence;
    public String authorRole;

    public String sentimentLabel;
    public double sentimentScore;

    public String emotionCategory;

    public boolean stressSignal;
    public boolean toxicitySignal;

    public String governanceEra;

    public String finalDecision;

    public SentimentCandidate() {
    }
}
