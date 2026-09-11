package influenceMiner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import connections.MysqlConnect;

/*
 * InfluenceCsvExporter
 *
 * Writes the research result files (UTF-8, RFC-4180 quoting) to
 * InfluenceConfig.outputDir():
 *
 *   influence_candidates.csv              every stored candidate sentence
 *   influence_actor_summary.csv           per (proposal, actor) counts
 *   influence_actor_overall.csv           per actor across all proposals
 *   influence_proposal_summary.csv        per proposal counts
 *   influence_type_distribution.csv       RQ1 - multi-label type counts and %
 *   influence_direction_distribution.csv
 *   influence_scope_distribution.csv
 *   influence_outcome_alignment.csv       RQ4 - direction vs outcome, aligned counts
 *   influence_type_by_outcome.csv         RQ4 - type distribution per outcome
 *   influence_era_comparison.csv          RQ3 - BDFL era vs post-BDFL era
 *   influence_actor_type_matrix.csv       RQ2 - actor x type matrix (top actors)
 *   influence_role_distribution.csv       RQ2 - candidates per role
 *   influence_preference_overlap.csv      RQ5 - overlap with preference_candidates
 *
 * Usage:  java influenceMiner.InfluenceCsvExporter [pep] [outputDir]
 */
public class InfluenceCsvExporter {

    public static void main(String[] args) {
        String identifier = args.length > 0 ? args[0].toLowerCase() : "pep";
        String outputDir = args.length > 1 ? args[1] : InfluenceConfig.outputDir();
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return;
        }
        try {
            exportAll(connection, identifier, outputDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportAll(Connection connection, String identifier, String outputDir) throws Exception {
        InfluenceResultAggregator.Aggregates a = InfluenceResultAggregator.aggregate(connection, identifier);
        exportAll(connection, a, outputDir);
    }

    public static void exportAll(Connection connection, InfluenceResultAggregator.Aggregates a, String outputDir) throws Exception {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        exportCandidates(a, new File(dir, "influence_candidates.csv").getPath());
        exportActorSummary(a, new File(dir, "influence_actor_summary.csv").getPath());
        exportActorOverall(a, new File(dir, "influence_actor_overall.csv").getPath());
        exportProposalSummary(a, new File(dir, "influence_proposal_summary.csv").getPath());
        exportDistribution(a.typeDistribution, "influence_type", a.totalCandidates, new File(dir, "influence_type_distribution.csv").getPath());
        exportDistribution(a.directionDistribution, "influence_direction", a.totalCandidates, new File(dir, "influence_direction_distribution.csv").getPath());
        exportDistribution(a.scopeDistribution, "influence_scope", a.totalCandidates, new File(dir, "influence_scope_distribution.csv").getPath());
        exportDistribution(a.roleDistribution, "author_role", a.totalCandidates, new File(dir, "influence_role_distribution.csv").getPath());
        exportOutcomeAlignment(a, new File(dir, "influence_outcome_alignment.csv").getPath());
        exportCrossTab(a.typeByOutcome, "final_decision", "influence_type", new File(dir, "influence_type_by_outcome.csv").getPath());
        exportEraComparison(a, new File(dir, "influence_era_comparison.csv").getPath());
        exportActorTypeMatrix(a, 50, new File(dir, "influence_actor_type_matrix.csv").getPath());
        exportPreferenceOverlap(connection, a, new File(dir, "influence_preference_overlap.csv").getPath());
        System.out.println("CSV export completed in " + dir.getAbsolutePath());
    }

    /* ---- prototype-style entry points required by the coding brief ---- */

    public static void exportCandidates(Connection connection, String outputPath) throws Exception {
        exportCandidates(InfluenceResultAggregator.aggregate(connection, null), outputPath);
    }

    public static void exportActorSummary(Connection connection, String outputPath) throws Exception {
        exportActorSummary(InfluenceResultAggregator.aggregate(connection, null), outputPath);
    }

    public static void exportProposalSummary(Connection connection, String outputPath) throws Exception {
        exportProposalSummary(InfluenceResultAggregator.aggregate(connection, null), outputPath);
    }

    /* ---- individual files ---- */

    public static void exportCandidates(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row("id", "proposal_identifier", "proposal_number", "message_id", "author_name", "author_email",
                    "author_role", "message_date", "sentence", "influence_types", "primary_influence_type",
                    "influence_direction", "influence_scope", "influence_target", "influence_score", "evidence_cues",
                    "final_decision", "aligns_with_outcome", "decision_date", "days_before_decision", "decision_phase",
                    "governance_era", "extraction_scheme"));
            for (InfluenceCandidateRepository.StoredCandidate c : a.candidates) {
                w.println(row(String.valueOf(c.id), c.proposalIdentifier, String.valueOf(c.proposalNumber), c.messageId,
                        c.authorName, c.authorEmail, c.authorRole, c.messageDate, c.sentence, c.influenceTypes,
                        c.primaryInfluenceType, c.influenceDirection, c.influenceScope, c.influenceTarget,
                        String.valueOf(c.score), c.evidenceCues, c.finalDecision, String.valueOf(c.alignsWithOutcome),
                        c.decisionDate, InfluenceTemporalAnalyzer.isKnown(c.daysBeforeDecision) ? String.valueOf(c.daysBeforeDecision) : "",
                        c.decisionPhase, c.governanceEra, c.extractionScheme));
            }
        } finally {
            w.close();
        }
        System.out.println("Wrote " + a.candidates.size() + " rows to " + outputPath);
    }

    public static void exportActorSummary(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            List<String> header = new ArrayList<String>();
            header.add("proposal_identifier"); header.add("proposal_number"); header.add("author_name");
            header.add("author_email"); header.add("author_role"); header.add("total_influence_sentences");
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) header.add(InfluenceTypeDetector.TYPES[i] + "_count");
            header.add("supporting_count"); header.add("blocking_count"); header.add("revising_count"); header.add("neutral_count");
            header.add("average_score"); header.add("max_score");
            w.println(row(header));
            for (InfluenceResultAggregator.ActorSummary s : a.actorsByProposal.values()) {
                w.println(row(actorRow(s, false)));
            }
        } finally {
            w.close();
        }
        System.out.println("Wrote " + a.actorsByProposal.size() + " rows to " + outputPath);
    }

    public static void exportActorOverall(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            List<String> header = new ArrayList<String>();
            header.add("author_name"); header.add("author_email"); header.add("author_role"); header.add("proposals_involved");
            header.add("total_influence_sentences");
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) header.add(InfluenceTypeDetector.TYPES[i] + "_count");
            header.add("supporting_count"); header.add("blocking_count"); header.add("revising_count"); header.add("neutral_count");
            header.add("average_score"); header.add("max_score");
            w.println(row(header));
            for (InfluenceResultAggregator.ActorSummary s : InfluenceResultAggregator.topActorsByCount(a, Integer.MAX_VALUE)) {
                w.println(row(actorRow(s, true)));
            }
        } finally {
            w.close();
        }
        System.out.println("Wrote " + a.actorsOverall.size() + " rows to " + outputPath);
    }

    private static List<String> actorRow(InfluenceResultAggregator.ActorSummary s, boolean overall) {
        List<String> r = new ArrayList<String>();
        if (!overall) {
            r.add(s.proposalIdentifier);
            r.add(String.valueOf(s.proposalNumber));
        }
        r.add(s.authorName);
        r.add(s.authorEmail);
        r.add(s.authorRole);
        if (overall) r.add(String.valueOf(s.proposals.size()));
        r.add(String.valueOf(s.total));
        for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) r.add(String.valueOf(s.typeCounts.get(InfluenceTypeDetector.TYPES[i])));
        r.add(String.valueOf(s.supporting)); r.add(String.valueOf(s.blocking)); r.add(String.valueOf(s.revising)); r.add(String.valueOf(s.neutral));
        r.add(String.valueOf(InfluenceResultAggregator.round(s.averageScore())));
        r.add(String.valueOf(s.maxScore));
        return r;
    }

    public static void exportProposalSummary(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            List<String> header = new ArrayList<String>();
            header.add("proposal_identifier"); header.add("proposal_number"); header.add("final_decision"); header.add("outcome_group");
            header.add("total_influence_sentences"); header.add("total_actors");
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) header.add(InfluenceTypeDetector.TYPES[i] + "_count");
            header.add("supporting_count"); header.add("blocking_count"); header.add("revising_count"); header.add("neutral_count");
            header.add("internal_count"); header.add("external_count"); header.add("mixed_count"); header.add("unknown_scope_count");
            header.add("aligned_count"); header.add("aligned_ratio"); header.add("average_score"); header.add("max_score");
            w.println(row(header));
            for (InfluenceResultAggregator.ProposalSummary s : a.proposals.values()) {
                List<String> r = new ArrayList<String>();
                r.add(s.proposalIdentifier); r.add(String.valueOf(s.proposalNumber)); r.add(s.finalDecision);
                r.add(InfluenceOutcomeResolver.outcomeGroup(s.finalDecision));
                r.add(String.valueOf(s.total)); r.add(String.valueOf(s.actors.size()));
                for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) r.add(String.valueOf(s.typeCounts.get(InfluenceTypeDetector.TYPES[i])));
                r.add(String.valueOf(s.supporting)); r.add(String.valueOf(s.blocking)); r.add(String.valueOf(s.revising)); r.add(String.valueOf(s.neutral));
                r.add(String.valueOf(s.internal)); r.add(String.valueOf(s.external)); r.add(String.valueOf(s.mixed)); r.add(String.valueOf(s.unknownScope));
                r.add(String.valueOf(s.aligned)); r.add(String.valueOf(InfluenceResultAggregator.round(s.total == 0 ? 0 : (double) s.aligned / s.total)));
                r.add(String.valueOf(InfluenceResultAggregator.round(s.averageScore()))); r.add(String.valueOf(s.maxScore));
                w.println(row(r));
            }
        } finally {
            w.close();
        }
        System.out.println("Wrote " + a.proposals.size() + " rows to " + outputPath);
    }

    public static void exportDistribution(Map<String, Integer> dist, String label, int total, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row(label, "count", "percent_of_candidates"));
            for (Map.Entry<String, Integer> e : dist.entrySet()) {
                w.println(row(e.getKey(), String.valueOf(e.getValue()), pct(e.getValue().intValue(), total)));
            }
        } finally {
            w.close();
        }
    }

    public static void exportOutcomeAlignment(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row("section", "final_decision", "influence_direction", "count", "aligned", "not_aligned", "aligned_ratio"));
            for (Map.Entry<String, Map<String, Integer>> o : a.directionByOutcome.entrySet()) {
                for (Map.Entry<String, Integer> d : o.getValue().entrySet()) {
                    boolean aligns = InfluenceOutcomeAlignmentAnalyzer.aligns(d.getKey(), o.getKey());
                    int n = d.getValue().intValue();
                    w.println(row("direction_by_outcome", o.getKey(), d.getKey(), String.valueOf(n),
                            String.valueOf(aligns ? n : 0), String.valueOf(aligns ? 0 : n), aligns ? "1.0" : "0.0"));
                }
            }
            for (Map.Entry<String, int[]> e : a.alignmentByDirection.entrySet()) {
                int[] v = e.getValue();
                int n = v[0] + v[1];
                w.println(row("alignment_by_direction", "all", e.getKey(), String.valueOf(n), String.valueOf(v[0]),
                        String.valueOf(v[1]), String.valueOf(InfluenceResultAggregator.round(n == 0 ? 0 : (double) v[0] / n))));
            }
            w.println(row("overall", "all", "all", String.valueOf(a.totalCandidates), String.valueOf(a.totalAligned),
                    String.valueOf(a.totalCandidates - a.totalAligned),
                    String.valueOf(InfluenceResultAggregator.round(a.totalCandidates == 0 ? 0 : (double) a.totalAligned / a.totalCandidates))));
        } finally {
            w.close();
        }
    }

    public static void exportCrossTab(Map<String, Map<String, Integer>> table, String rowLabel, String colLabel, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row(rowLabel, colLabel, "count", "percent_within_" + rowLabel));
            for (Map.Entry<String, Map<String, Integer>> r : table.entrySet()) {
                int total = 0;
                for (Integer v : r.getValue().values()) total += v.intValue();
                for (Map.Entry<String, Integer> c : r.getValue().entrySet()) {
                    w.println(row(r.getKey(), c.getKey(), String.valueOf(c.getValue()), pct(c.getValue().intValue(), total)));
                }
            }
        } finally {
            w.close();
        }
    }

    public static void exportEraComparison(InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row("dimension", "value", "governance_era", "count", "percent_within_era", "split_date"));
            String split = InfluenceConfig.governanceSplitDate().toString();
            writeEraBlock(w, "influence_type", a.typeByEra, a.eraDistribution, split);
            writeEraBlock(w, "influence_direction", a.directionByEra, a.eraDistribution, split);
            writeEraBlock(w, "influence_scope", a.scopeByEra, a.eraDistribution, split);
            for (Map.Entry<String, Integer> e : a.eraDistribution.entrySet()) {
                w.println(row("total_candidates", "all", e.getKey(), String.valueOf(e.getValue()), "100.0", split));
            }
        } finally {
            w.close();
        }
    }

    private static void writeEraBlock(PrintWriter w, String dimension, Map<String, Map<String, Integer>> byEra,
                                      Map<String, Integer> eraTotals, String split) {
        for (Map.Entry<String, Map<String, Integer>> era : byEra.entrySet()) {
            Integer total = eraTotals.get(era.getKey());
            int t = total == null ? 0 : total.intValue();
            for (Map.Entry<String, Integer> v : era.getValue().entrySet()) {
                w.println(row(dimension, v.getKey(), era.getKey(), String.valueOf(v.getValue()), pct(v.getValue().intValue(), t), split));
            }
        }
    }

    public static void exportActorTypeMatrix(InfluenceResultAggregator.Aggregates a, int topN, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            List<String> header = new ArrayList<String>();
            header.add("author_name"); header.add("author_role"); header.add("proposals_involved"); header.add("total");
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) header.add(InfluenceTypeDetector.TYPES[i]);
            w.println(row(header));
            for (InfluenceResultAggregator.ActorSummary s : InfluenceResultAggregator.topActorsByCount(a, topN)) {
                List<String> r = new ArrayList<String>();
                r.add(s.authorName); r.add(s.authorRole); r.add(String.valueOf(s.proposals.size())); r.add(String.valueOf(s.total));
                for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) r.add(String.valueOf(s.typeCounts.get(InfluenceTypeDetector.TYPES[i])));
                w.println(row(r));
            }
        } finally {
            w.close();
        }
    }

    /*
     * RQ5: how much do Preference Miner and Influence Miner look at the same
     * sentences? Sentences are compared per proposal after lower-casing and
     * collapsing non-alphanumerics. Skipped when preference_candidates is absent.
     */
    public static void exportPreferenceOverlap(Connection connection, InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {
        PrintWriter w = open(outputPath);
        try {
            w.println(row("proposal_number", "influence_sentences", "preference_sentences", "in_both", "influence_only", "preference_only", "jaccard"));
            if (!InfluenceSchemaManager.tableExists(connection, "preference_candidates")) {
                w.println(row("n/a", "0", "0", "0", "0", "0", "preference_candidates table not found"));
                return;
            }
            Map<Integer, Set<String>> pref = new HashMap<Integer, Set<String>>();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT proposal_number, sentence FROM preference_candidates");
            while (rs.next()) {
                Integer p = Integer.valueOf(rs.getInt(1));
                Set<String> set = pref.get(p);
                if (set == null) {
                    set = new HashSet<String>();
                    pref.put(p, set);
                }
                set.add(norm(rs.getString(2)));
            }
            rs.close();
            st.close();

            Map<Integer, Set<String>> infl = new TreeMap<Integer, Set<String>>();
            for (InfluenceCandidateRepository.StoredCandidate c : a.candidates) {
                Integer p = Integer.valueOf(c.proposalNumber);
                Set<String> set = infl.get(p);
                if (set == null) {
                    set = new HashSet<String>();
                    infl.put(p, set);
                }
                set.add(norm(c.sentence));
            }

            Set<Integer> all = new java.util.TreeSet<Integer>(infl.keySet());
            all.addAll(pref.keySet());
            long tI = 0, tP = 0, tB = 0;
            for (Integer p : all) {
                Set<String> i = infl.containsKey(p) ? infl.get(p) : new HashSet<String>();
                Set<String> pr = pref.containsKey(p) ? pref.get(p) : new HashSet<String>();
                int both = 0;
                for (String s : i) if (pr.contains(s)) both++;
                int union = i.size() + pr.size() - both;
                tI += i.size(); tP += pr.size(); tB += both;
                w.println(row(String.valueOf(p), String.valueOf(i.size()), String.valueOf(pr.size()), String.valueOf(both),
                        String.valueOf(i.size() - both), String.valueOf(pr.size() - both),
                        String.valueOf(InfluenceResultAggregator.round(union == 0 ? 0 : (double) both / union))));
            }
            long union = tI + tP - tB;
            w.println(row("all", String.valueOf(tI), String.valueOf(tP), String.valueOf(tB), String.valueOf(tI - tB),
                    String.valueOf(tP - tB), String.valueOf(InfluenceResultAggregator.round(union == 0 ? 0 : (double) tB / union))));
        } finally {
            w.close();
        }
    }

    private static String norm(String s) {
        return s == null ? "" : s.toLowerCase().replaceAll("[^a-z0-9+\\-]+", " ").trim();
    }

    /* ---- CSV plumbing ---- */

    public static PrintWriter open(String path) throws Exception {
        File f = new File(path);
        if (f.getParentFile() != null && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        return new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
    }

    public static String row(String... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(csvEscape(values[i]));
        }
        return sb.toString();
    }

    public static String row(List<String> values) {
        return row(values.toArray(new String[values.size()]));
    }

    public static String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        String v = value.replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ');
        if (v.indexOf(',') >= 0 || v.indexOf('"') >= 0 || v.indexOf(';') >= 0 || v.startsWith(" ") || v.endsWith(" ")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    public static String pct(int part, int total) {
        if (total == 0) return "0.0";
        return String.valueOf(Math.round(part * 1000.0 / total) / 10.0);
    }

    /*
     * Minimal RFC-4180 parser for the optional input CSVs.
     */
    public static String[] parseCsvLine(String line) {
        List<String> out = new ArrayList<String>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else if (ch == '"') {
                quoted = true;
            } else if (ch == ',') {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[out.size()]);
    }
}
