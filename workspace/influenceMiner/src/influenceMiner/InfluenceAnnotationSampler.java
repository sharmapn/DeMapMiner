package influenceMiner;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import connections.MysqlConnect;

/*
 * InfluenceAnnotationSampler
 *
 * Exports a manual-annotation sheet (influence_annotation_sample.csv) with
 * the predicted labels and empty human_* columns, so that two coders can
 * build the gold standard described in the paper.
 *
 * The sample is a union of four strata (duplicates removed):
 *   1. top-N by influence score
 *   2. N random candidates
 *   3. up to K per influence type (multi-label aware) chosen at random
 *   4. up to K per proposal outcome chosen at random
 *
 * Usage:  java influenceMiner.InfluenceAnnotationSampler [pep] [topN=100] [randomN=100] [perStratum=20] [seed=42]
 */
public class InfluenceAnnotationSampler {

    public static final String FILE_NAME = "influence_annotation_sample.csv";

    public static void main(String[] args) {
        String identifier = args.length > 0 ? args[0].toLowerCase() : "pep";
        int topN = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        int randomN = args.length > 2 ? Integer.parseInt(args[2]) : 100;
        int perStratum = args.length > 3 ? Integer.parseInt(args[3]) : 20;
        long seed = args.length > 4 ? Long.parseLong(args[4]) : 42L;

        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return;
        }
        try {
            List<InfluenceCandidateRepository.StoredCandidate> all = InfluenceCandidateRepository.loadAll(connection, identifier);
            String path = InfluenceConfig.outputFile(FILE_NAME).getPath();
            int n = exportSample(all, path, topN, randomN, perStratum, seed);
            System.out.println("Annotation sample: " + n + " rows written to " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int exportSample(List<InfluenceCandidateRepository.StoredCandidate> all, String outputPath,
                                   int topN, int randomN, int perStratum, long seed) throws Exception {

        Random random = new Random(seed);
        Set<InfluenceCandidateRepository.StoredCandidate> chosen = new LinkedHashSet<InfluenceCandidateRepository.StoredCandidate>();
        Map<InfluenceCandidateRepository.StoredCandidate, String> stratumOf = new HashMap<InfluenceCandidateRepository.StoredCandidate, String>();

        // 1. top scored
        List<InfluenceCandidateRepository.StoredCandidate> byScore = new ArrayList<InfluenceCandidateRepository.StoredCandidate>(all);
        Collections.sort(byScore, new Comparator<InfluenceCandidateRepository.StoredCandidate>() {
            public int compare(InfluenceCandidateRepository.StoredCandidate a, InfluenceCandidateRepository.StoredCandidate b) {
                return Double.compare(b.score, a.score);
            }
        });
        for (int i = 0; i < Math.min(topN, byScore.size()); i++) {
            add(chosen, stratumOf, byScore.get(i), "top_score");
        }

        // 2. random
        List<InfluenceCandidateRepository.StoredCandidate> shuffled = new ArrayList<InfluenceCandidateRepository.StoredCandidate>(all);
        Collections.shuffle(shuffled, random);
        for (int i = 0; i < Math.min(randomN, shuffled.size()); i++) {
            add(chosen, stratumOf, shuffled.get(i), "random");
        }

        // 3. per influence type
        for (int t = 0; t < InfluenceTypeDetector.TYPES.length; t++) {
            String type = InfluenceTypeDetector.TYPES[t];
            List<InfluenceCandidateRepository.StoredCandidate> pool = new ArrayList<InfluenceCandidateRepository.StoredCandidate>();
            for (InfluenceCandidateRepository.StoredCandidate c : shuffled) {
                if (c.hasType(type)) pool.add(c);
            }
            for (int i = 0; i < Math.min(perStratum, pool.size()); i++) {
                add(chosen, stratumOf, pool.get(i), "type:" + type);
            }
        }

        // 4. per outcome
        Set<String> outcomes = new HashSet<String>();
        for (InfluenceCandidateRepository.StoredCandidate c : all) outcomes.add(c.finalDecision);
        for (String outcome : outcomes) {
            List<InfluenceCandidateRepository.StoredCandidate> pool = new ArrayList<InfluenceCandidateRepository.StoredCandidate>();
            for (InfluenceCandidateRepository.StoredCandidate c : shuffled) {
                if (outcome.equals(c.finalDecision)) pool.add(c);
            }
            for (int i = 0; i < Math.min(perStratum, pool.size()); i++) {
                add(chosen, stratumOf, pool.get(i), "outcome:" + outcome);
            }
        }

        PrintWriter w = InfluenceCsvExporter.open(outputPath);
        try {
            w.println(InfluenceCsvExporter.row("id", "proposal_identifier", "proposal_number", "message_id", "author_name",
                    "author_email", "author_role", "message_date", "sentence", "predicted_influence_types",
                    "predicted_primary_influence_type", "predicted_direction", "predicted_scope", "predicted_target",
                    "score", "evidence_cues", "final_decision", "sample_stratum", "human_influence_types",
                    "human_primary_influence_type", "human_direction", "human_scope", "human_target", "human_is_influence",
                    "notes"));
            for (InfluenceCandidateRepository.StoredCandidate c : chosen) {
                w.println(InfluenceCsvExporter.row(String.valueOf(c.id), c.proposalIdentifier, String.valueOf(c.proposalNumber),
                        c.messageId, c.authorName, c.authorEmail, c.authorRole, c.messageDate, c.sentence, c.influenceTypes,
                        c.primaryInfluenceType, c.influenceDirection, c.influenceScope, c.influenceTarget,
                        String.valueOf(c.score), c.evidenceCues, c.finalDecision, stratumOf.get(c),
                        "", "", "", "", "", "", ""));
            }
        } finally {
            w.close();
        }
        return chosen.size();
    }

    private static void add(Set<InfluenceCandidateRepository.StoredCandidate> chosen,
                            Map<InfluenceCandidateRepository.StoredCandidate, String> stratumOf,
                            InfluenceCandidateRepository.StoredCandidate c, String stratum) {
        if (chosen.add(c)) {
            stratumOf.put(c, stratum);
        }
    }
}
