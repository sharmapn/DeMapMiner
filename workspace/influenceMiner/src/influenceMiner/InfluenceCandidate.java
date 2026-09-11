package influenceMiner;

/*
 * Stores one extracted influence candidate.
 *
 * Influence Miner is sentence-based in this prototype. Each candidate is one
 * sentence that carries an influence SIGNAL - the fields below describe the
 * signal, not a proven effect on the decision.
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

    /* cue phrases that produced the type labels, e.g. "security: attack surface|vulnerability" */
    public String evidenceCues;

    public String finalDecision;
    public boolean alignsWithOutcome;

    /* temporal features; daysBeforeDecision == InfluenceTemporalAnalyzer.UNKNOWN_DAYS when unknown */
    public String decisionDate;
    public int daysBeforeDecision = InfluenceTemporalAnalyzer.UNKNOWN_DAYS;
    public String decisionPhase = "unknown";
    public String governanceEra = InfluenceConfig.ERA_UNKNOWN;

    public InfluenceCandidate() {
    }

    public boolean hasType(String type) {
        if (influenceTypes == null || type == null) return false;
        String[] parts = influenceTypes.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].trim().equals(type)) return true;
        }
        return false;
    }
}
