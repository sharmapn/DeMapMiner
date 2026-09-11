package influenceMiner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import connections.MysqlConnect;

/*
 * InfluenceExtractor
 *
 * Main extraction pipeline for Influence Miner (heuristic-v2).
 *
 * For one proposal:
 *   1. resolve the real outcome / decision date / creation date  (InfluenceOutcomeResolver)
 *   2. load proposal-linked messages with schema discovery         (InfluenceMessageSource)
 *   3. clean each body and split into sentences                    (InfluenceTextPreprocessor)
 *   4. de-duplicate sentences within the proposal
 *   5. resolve the author's role                                   (InfluenceRoleMapper)
 *   6. detect influence types + evidence, direction, scope, target (detectors)
 *   7. compute temporal features                                   (InfluenceTemporalAnalyzer)
 *   8. score and keep candidates >= InfluenceConfig.minimumScore()  (InfluenceHeuristics)
 *   9. batch-insert into influence_candidates                      (InfluenceDatabaseWriter)
 *
 * Nothing here claims causality: a stored row is a candidate influence
 * SIGNAL whose direction can later be compared with the outcome.
 *
 * Parts that should be replaced by ML / NLP / LLM methods later: steps 3
 * (sentence splitting), 6 (all four detectors) and 8 (scoring).
 */
public class InfluenceExtractor {

    /*
     * Kept for callers of the first prototype; the configurable value is
     * InfluenceConfig.minimumScore().
     */
    public static final double MINIMUM_SCORE = InfluenceConfig.DEFAULT_MINIMUM_SCORE;

    public static class ExtractionStats {
        public int proposalNumber;
        public int messagesProcessed;
        public int messagesSkippedEmpty;
        public int messagesExcludedAutomated;
        public int sentencesProcessed;
        public int sentencesDuplicate;
        public int sentencesNoise;
        public int sentencesWithInfluenceType;
        public int candidatesSaved;
        public int messagesWithShiftedNames;
        public int namesRecovered;
        public String finalDecision = "unknown";
        public String decisionDate;
        public String outcomeSource;
        public boolean failed;
        public String error;

        public String toString() {
            return "proposal " + proposalNumber + ": messages=" + messagesProcessed + " (empty " + messagesSkippedEmpty + ", automated " + messagesExcludedAutomated
                    + "), sentences=" + sentencesProcessed + " (dup " + sentencesDuplicate + ", noise " + sentencesNoise
                    + "), shiftedNames=" + messagesWithShiftedNames + " (recovered " + namesRecovered + "), typed=" + sentencesWithInfluenceType + ", saved=" + candidatesSaved + ", outcome="
                    + finalDecision + (decisionDate != null ? " @ " + decisionDate : "") + " [" + outcomeSource + "]"
                    + (failed ? " FAILED: " + error : "");
        }
    }

    /*
     * Prototype-compatible entry point: opens the shared connection, makes
     * sure the schema exists, and extracts one proposal.
     */
    public static void extractInfluence(int proposalNumber, String proposalIdentifier) {
        try {
            Connection connection = MysqlConnect.connect();
            if (connection == null) {
                System.err.println("Database connection is null.");
                return;
            }
            InfluenceSchemaManager.ensureSchema(connection);
            ExtractionStats stats = extractInfluence(connection, proposalNumber, proposalIdentifier);
            System.out.println(stats);
            System.out.println("Influence extraction completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ExtractionStats extractInfluence(Connection connection, int proposalNumber, String proposalIdentifier) {

        ExtractionStats stats = new ExtractionStats();
        stats.proposalNumber = proposalNumber;
        String identifier = proposalIdentifier == null ? "pep" : proposalIdentifier.trim().toLowerCase();

        try {
            InfluenceOutcome outcome = InfluenceOutcomeResolver.resolveOutcome(connection, identifier, proposalNumber);
            stats.finalDecision = outcome.finalDecision;
            stats.decisionDate = outcome.decisionDate;
            stats.outcomeSource = outcome.source;

            List<InfluenceMessageSource.Message> messages = InfluenceMessageSource.loadMessages(connection, proposalNumber);
            List<InfluenceCandidate> candidates = new ArrayList<InfluenceCandidate>();
            Set<String> seen = new HashSet<String>();
            double minimumScore = InfluenceConfig.minimumScore();

            for (int mi = 0; mi < messages.size(); mi++) {

                InfluenceMessageSource.Message message = messages.get(mi);

                // commit notifications / tracker robots are not discussion
                if (InfluenceConfig.isExcludedMessage(message.authorEmail, message.mailingList)) {
                    stats.messagesExcludedAutomated++;
                    continue;
                }
                if (message.body == null || message.body.trim().length() == 0) {
                    stats.messagesSkippedEmpty++;
                    continue;
                }
                stats.messagesProcessed++;

                InfluenceAuthorResolver.Identity author = InfluenceAuthorResolver.resolve(connection, message);
                if (author.rowUnreliable) {
                    stats.messagesWithShiftedNames++;
                    if (author.nameRecovered) stats.namesRecovered++;
                }

                String role = InfluenceRoleMapper.mapRole(connection, identifier, proposalNumber,
                        author.email, author.name, author.datasetRole, message.messageDate);

                int daysBeforeDecision = InfluenceTemporalAnalyzer.daysBeforeDecision(message.messageDate, outcome.decisionDate);
                String phase = InfluenceTemporalAnalyzer.detectDecisionPhase(message.messageDate, outcome.decisionDate, outcome.createdDate);
                String era = InfluenceTemporalAnalyzer.governanceEra(message.messageDate);

                String cleaned = InfluenceTextPreprocessor.cleanMessageBody(message.body, author.name);
                List<String> sentences = InfluenceTextPreprocessor.splitIntoSentenceList(cleaned);

                for (int si = 0; si < sentences.size(); si++) {

                    String sentence = sentences.get(si).trim();
                    stats.sentencesProcessed++;

                    if (InfluenceTextPreprocessor.isNoiseSentence(sentence)) {
                        stats.sentencesNoise++;
                        continue;
                    }

                    String key = sentence.toLowerCase().replaceAll("[^a-z0-9+\\-]+", " ").trim();
                    if (!seen.add(key)) {
                        stats.sentencesDuplicate++;
                        continue;
                    }

                    String influenceTypes = InfluenceTypeDetector.detectTypes(sentence);
                    if (influenceTypes.equals("none")) {
                        continue;
                    }
                    stats.sentencesWithInfluenceType++;

                    String primaryInfluenceType = InfluenceTypeDetector.primaryType(influenceTypes);
                    String influenceDirection = InfluenceDirectionDetector.detectDirection(sentence);
                    String influenceScope = InfluenceScopeDetector.detectScope(sentence, role);
                    String influenceTarget = InfluenceTargetDetector.detectTarget(sentence);

                    double score = InfluenceHeuristics.scoreSentence(
                            sentence, role, influenceTypes, influenceDirection, influenceScope, influenceTarget, daysBeforeDecision);

                    if (score < minimumScore) {
                        continue;
                    }

                    InfluenceCandidate candidate = new InfluenceCandidate();
                    candidate.proposalIdentifier = identifier;
                    candidate.proposalNumber = proposalNumber;
                    candidate.messageId = message.messageId;
                    candidate.authorName = displayName(author.name);
                    candidate.authorEmail = author.email.length() == 0 ? null : author.email;
                    candidate.authorRole = role;
                    candidate.messageDate = message.messageDate;
                    candidate.sentence = sentence;
                    candidate.influenceTypes = influenceTypes;
                    candidate.primaryInfluenceType = primaryInfluenceType;
                    candidate.influenceScope = influenceScope;
                    candidate.influenceDirection = influenceDirection;
                    candidate.influenceTarget = influenceTarget;
                    candidate.score = score;
                    candidate.evidenceCues = InfluenceTypeDetector.detectEvidenceCues(sentence);
                    candidate.finalDecision = outcome.finalDecision;
                    candidate.alignsWithOutcome = InfluenceOutcomeAlignmentAnalyzer.aligns(influenceDirection, outcome.finalDecision);
                    candidate.decisionDate = outcome.decisionDate;
                    candidate.daysBeforeDecision = daysBeforeDecision;
                    candidate.decisionPhase = phase;
                    candidate.governanceEra = era;

                    candidates.add(candidate);
                }
            }

            stats.candidatesSaved = InfluenceDatabaseWriter.saveCandidates(connection, candidates);

        } catch (Exception e) {
            stats.failed = true;
            stats.error = e.getClass().getSimpleName() + ": " + e.getMessage();
            e.printStackTrace();
        }

        return stats;
    }

    /*
     * "From marko ristin" -> "marko ristin"; keeps the original casing.
     */
    private static String displayName(String name) {
        if (name == null) return null;
        String n = name.replace("\t", " ").trim();
        if (n.toLowerCase().startsWith("from ")) {
            n = n.substring(5).trim();
        }
        return n.replaceAll("\\s+", " ");
    }
}
