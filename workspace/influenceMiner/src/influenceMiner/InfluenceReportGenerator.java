package influenceMiner;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import connections.MysqlConnect;

/*
 * InfluenceReportGenerator
 *
 * Writes a plain-text summary (influence_report.txt) that can be pasted
 * into the "preliminary results" section of the Influence Miner paper.
 * All numbers are descriptive; the wording deliberately says "aligns with"
 * and "is associated with", never "caused".
 *
 * Usage:  java influenceMiner.InfluenceReportGenerator [pep]
 */
public class InfluenceReportGenerator {

    public static final String FILE_NAME = "influence_report.txt";

    public static void main(String[] args) {
        String identifier = args.length > 0 ? args[0].toLowerCase() : "pep";
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return;
        }
        try {
            generateReport(connection, identifier, InfluenceConfig.outputFile(FILE_NAME).getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateReport(Connection connection, String outputPath) throws Exception {
        generateReport(connection, (String) null, outputPath);
    }

    public static void generateReport(Connection connection, String identifier, String outputPath) throws Exception {
        InfluenceResultAggregator.Aggregates a = InfluenceResultAggregator.aggregate(connection, identifier);
        generateReport(connection, a, outputPath);
    }

    public static void generateReport(Connection connection, InfluenceResultAggregator.Aggregates a, String outputPath) throws Exception {

        PrintWriter w = InfluenceCsvExporter.open(outputPath);
        try {
            int total = a.totalCandidates;
            long messages = countMessages(connection, a);

            h1(w, "INFLUENCE MINER - PRELIMINARY RESULTS REPORT");
            w.println("Generated: " + LocalDateTime.now());
            w.println("Extraction scheme: " + InfluenceConfig.EXTRACTION_SCHEME + " (rule-based heuristics; see limitations)");
            w.println("Minimum score: " + InfluenceConfig.minimumScore());
            w.println("Governance split date: " + InfluenceConfig.governanceSplitDate() + " (bdfl_era before, post_bdfl_era on/after)");
            w.println();

            h2(w, "1. Corpus coverage");
            w.println("Proposals with influence candidates : " + a.proposals.size());
            w.println("Messages linked to those proposals  : " + (messages >= 0 ? String.valueOf(messages) : "n/a"));
            w.println("Influence candidate sentences       : " + total);
            w.println("Distinct actors                     : " + a.actorsOverall.size());
            w.println("(sentence and message totals per batch run are in influence_batch_log.txt)");
            w.println();

            h2(w, "2. RQ1 - Influence mechanisms (multi-label; a sentence can carry several types)");
            table(w, a.typeDistribution, total, "type");
            w.println();
            w.println("Primary (first-listed) type only:");
            table(w, a.primaryTypeDistribution, total, "primary type");
            w.println();

            h2(w, "3. RQ2 - Who exercises influence");
            w.println("Candidates by author role:");
            table(w, a.roleDistribution, total, "role");
            w.println();
            w.println("Top 15 actors by number of influence candidates:");
            w.println(String.format("  %-32s %-18s %5s %6s %8s  %s", "actor", "role", "PEPs", "n", "avg", "top types"));
            for (InfluenceResultAggregator.ActorSummary s : InfluenceResultAggregator.topActorsByCount(a, 15)) {
                w.println(String.format("  %-32s %-18s %5d %6d %8.2f  %s", cut(s.authorName, 32), cut(s.authorRole, 18),
                        s.proposals.size(), s.total, s.averageScore(), topTypes(s.typeCounts, 3)));
            }
            w.println();
            w.println("Top 15 actors by average influence score (minimum 20 candidates):");
            w.println(String.format("  %-32s %-18s %5s %6s %8s", "actor", "role", "PEPs", "n", "avg"));
            for (InfluenceResultAggregator.ActorSummary s : InfluenceResultAggregator.topActorsByAverageScore(a, 15, 20)) {
                w.println(String.format("  %-32s %-18s %5d %6d %8.2f", cut(s.authorName, 32), cut(s.authorRole, 18),
                        s.proposals.size(), s.total, s.averageScore()));
            }
            w.println();
            w.println("Type mix by role (percent of that role's candidates):");
            crossTab(w, a.typeByRole);
            w.println();

            h2(w, "4. RQ3 - Governance eras");
            table(w, a.eraDistribution, total, "era");
            w.println();
            w.println("Influence types by era (percent within era):");
            crossTab(w, a.typeByEra);
            w.println();
            w.println("Direction by era:");
            crossTab(w, a.directionByEra);
            w.println();
            w.println("Scope by era:");
            crossTab(w, a.scopeByEra);
            w.println();

            h2(w, "5. Direction and scope");
            w.println("Direction:");
            table(w, a.directionDistribution, total, "direction");
            w.println("Scope (internal vs external influence):");
            table(w, a.scopeDistribution, total, "scope");
            w.println("Target:");
            table(w, a.targetDistribution, total, "target");
            w.println("Decision phase of the message:");
            table(w, a.phaseDistribution, total, "phase");
            w.println();

            h2(w, "6. RQ4 - Alignment with proposal outcomes (association, not causation)");
            w.println("Candidates by proposal outcome:");
            table(w, a.outcomeDistribution, total, "outcome");
            w.println();
            w.println("Influence types by outcome (percent within outcome):");
            crossTab(w, a.typeByOutcome);
            w.println();
            w.println("Direction by outcome (percent within outcome):");
            crossTab(w, a.directionByOutcome);
            w.println();
            w.println("Direction-outcome alignment (supporting~accepted/final/active, blocking~rejected/withdrawn/deferred,");
            w.println("revising~deferred/superseded/draft/provisional):");
            for (Map.Entry<String, int[]> e : a.alignmentByDirection.entrySet()) {
                int[] v = e.getValue();
                int n = v[0] + v[1];
                w.println(String.format("  %-12s aligned %6d / %6d  (%s%%)", e.getKey(), v[0], n, InfluenceCsvExporter.pct(v[0], n)));
            }
            w.println(String.format("  %-12s aligned %6d / %6d  (%s%%)", "overall", a.totalAligned, total, InfluenceCsvExporter.pct(a.totalAligned, total)));
            w.println();

            h2(w, "7. Proposals with the highest influence intensity");
            w.println(String.format("  %-6s %-11s %6s %6s %5s %5s %5s %5s  %s", "PEP", "outcome", "n", "actors", "sup", "blk", "rev", "neu", "top types"));
            for (InfluenceResultAggregator.ProposalSummary s : InfluenceResultAggregator.proposalsByIntensity(a, 20)) {
                w.println(String.format("  %-6d %-11s %6d %6d %5d %5d %5d %5d  %s", s.proposalNumber, cut(s.finalDecision, 11), s.total,
                        s.actors.size(), s.supporting, s.blocking, s.revising, s.neutral, topTypes(s.typeCounts, 3)));
            }
            w.println();

            h2(w, "8. Controversial mechanisms (unilateral, corporate_interest, gatekeeping, exit_threat, incivility, backchannel, procedural_control)");
            controversialSection(w, a);

            h2(w, "9. Limitations of the current heuristic method");
            w.println("- Labels come from cue-phrase rules (InfluenceTypeDetector, InfluenceDirectionDetector, ...); no");
            w.println("  gold standard has been applied yet, so precision/recall are unknown. Use");
            w.println("  influence_annotation_sample.csv to build one.");
            w.println("- A stored row is a candidate influence SIGNAL. Alignment between direction and outcome is");
            w.println("  descriptive association; it does not show that the sentence changed the decision.");
            w.println("- Roles are taken from the dataset's authorsrole2020 labels and pepdetails; most community");
            w.println("  members remain 'community member' or 'unknown'. Roles are only partly time-sensitive.");
            w.println("- Decision dates come from accrejpeps / the PEP state history; proposals without a terminal");
            w.println("  state have no temporal features (days_before_decision is NULL).");
            w.println("- Quoted text, signatures and attached PEP text are removed heuristically; some quoted");
            w.println("  material survives and some genuine prose inside code-like lines is dropped.");
            w.println("- The governance-era split is a single date; messages are not linked to the governance");
            w.println("  process that actually decided each PEP.");
            w.println("- Multi-label counts sum to more than the number of candidates.");
        } finally {
            w.close();
        }
        System.out.println("Report written to " + outputPath);
    }

    private static long countMessages(Connection connection, InfluenceResultAggregator.Aggregates a) {
        if (a.proposals.isEmpty()) return 0;
        try {
            InfluenceMessageSource.Columns c = InfluenceMessageSource.columns(connection);
            StringBuilder in = new StringBuilder();
            for (Integer p : a.proposals.keySet()) {
                if (in.length() > 0) in.append(',');
                in.append(p);
            }
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + InfluenceMessageSource.MESSAGE_TABLE
                    + " WHERE `" + c.proposal + "` IN (" + in + ")");
            long n = rs.next() ? rs.getLong(1) : -1;
            rs.close();
            st.close();
            return n;
        } catch (Exception e) {
            return -1;
        }
    }

    private static void h1(PrintWriter w, String t) {
        w.println("=".repeat(t.length()));
        w.println(t);
        w.println("=".repeat(t.length()));
    }


    /*
     * Section on the seven controversial mechanisms: how often each occurs, who carries
     * it (role and era shares), the actors who use it most, and the highest-scoring
     * example sentences per mechanism so the labels can be eyeballed.
     */
    private static void controversialSection(PrintWriter w, InfluenceResultAggregator.Aggregates a) {
        int total = a.totalCandidates;
        int flagged = 0;
        for (InfluenceCandidateRepository.StoredCandidate c : a.candidates) {
            if (InfluenceTypeDetector.hasControversial(c.influenceTypes)) flagged++;
        }
        w.println("Candidates carrying at least one controversial mechanism: " + flagged + " / " + total
                + " (" + InfluenceCsvExporter.pct(flagged, total) + "%)");
        w.println();
        w.println(String.format("  %-20s %8s %7s", "mechanism", "n", "% cand"));
        for (int i = 0; i < InfluenceTypeDetector.CONTROVERSIAL_TYPES.length; i++) {
            String t = InfluenceTypeDetector.CONTROVERSIAL_TYPES[i];
            Integer n = a.typeDistribution.get(t);
            int v = n == null ? 0 : n.intValue();
            w.println(String.format("  %-20s %8d %7s", t, v, InfluenceCsvExporter.pct(v, total)));
        }
        w.println();

        w.println("Share of each role's candidates that carry the mechanism (percent):");
        crossTabRows(w, a.typeByRole, a.roleDistribution, InfluenceTypeDetector.CONTROVERSIAL_TYPES);
        w.println();
        w.println("Share of each era's candidates that carry the mechanism (percent):");
        crossTabRows(w, a.typeByEra, a.eraDistribution, InfluenceTypeDetector.CONTROVERSIAL_TYPES);
        w.println();

        for (int i = 0; i < InfluenceTypeDetector.CONTROVERSIAL_TYPES.length; i++) {
            String t = InfluenceTypeDetector.CONTROVERSIAL_TYPES[i];
            w.println("-- " + t);
            w.println("   top actors (n = candidates of this actor carrying the mechanism):");
            java.util.List<InfluenceResultAggregator.ActorSummary> actors =
                    new java.util.ArrayList<InfluenceResultAggregator.ActorSummary>(a.actorsOverall.values());
            final String tt = t;
            java.util.Collections.sort(actors, new java.util.Comparator<InfluenceResultAggregator.ActorSummary>() {
                public int compare(InfluenceResultAggregator.ActorSummary x, InfluenceResultAggregator.ActorSummary y) {
                    return y.typeCounts.get(tt).intValue() - x.typeCounts.get(tt).intValue();
                }
            });
            int shown = 0;
            for (InfluenceResultAggregator.ActorSummary s : actors) {
                int n = s.typeCounts.get(t).intValue();
                if (n == 0 || shown >= 8) break;
                w.println(String.format("     %-32s %-18s %5d of %5d (%s%% of the actor's candidates)",
                        cut(s.authorName, 32), cut(s.authorRole, 18), n, s.total, InfluenceCsvExporter.pct(n, s.total)));
                shown++;
            }
            w.println("   highest-scoring examples:");
            java.util.List<InfluenceCandidateRepository.StoredCandidate> ex =
                    new java.util.ArrayList<InfluenceCandidateRepository.StoredCandidate>();
            for (InfluenceCandidateRepository.StoredCandidate c : a.candidates) {
                if (c.hasType(t)) ex.add(c);
            }
            java.util.Collections.sort(ex, new java.util.Comparator<InfluenceCandidateRepository.StoredCandidate>() {
                public int compare(InfluenceCandidateRepository.StoredCandidate x, InfluenceCandidateRepository.StoredCandidate y) {
                    return Double.compare(y.score, x.score);
                }
            });
            java.util.Set<String> seenActors = new java.util.HashSet<String>();
            shown = 0;
            for (InfluenceCandidateRepository.StoredCandidate c : ex) {
                String key = InfluenceCandidateRepository.actorKey(c);
                if (seenActors.contains(key)) continue;   // one example per actor keeps the list varied
                seenActors.add(key);
                w.println(String.format("     [PEP %d, %s, %s, %.1f] %s", c.proposalNumber, cut(c.authorName, 24),
                        cut(c.authorRole, 16), c.score, cut(c.sentence, 200)));
                w.println("         cues: " + cut(c.evidenceCues, 160));
                if (++shown >= 5) break;
            }
            w.println();
        }
    }

    /* rows = groups (roles/eras), columns = the given types; cell = percent of the group's candidates */
    private static void crossTabRows(PrintWriter w, Map<String, Map<String, Integer>> t, Map<String, Integer> groupTotals, String[] types) {
        StringBuilder head = new StringBuilder(String.format("  %-22s %7s", "group", "n"));
        for (int i = 0; i < types.length; i++) head.append(String.format(" %10s", cut(types[i], 10)));
        w.println(head.toString());
        for (Map.Entry<String, Map<String, Integer>> e : t.entrySet()) {
            Integer gt = groupTotals.get(e.getKey());
            int n = gt == null ? 0 : gt.intValue();
            StringBuilder row = new StringBuilder(String.format("  %-22s %7d", cut(e.getKey(), 22), n));
            for (int i = 0; i < types.length; i++) {
                Integer v = e.getValue().get(types[i]);
                row.append(String.format(" %10s", InfluenceCsvExporter.pct(v == null ? 0 : v.intValue(), n)));
            }
            w.println(row.toString());
        }
    }

    private static void h2(PrintWriter w, String t) {
        w.println(t);
        w.println("-".repeat(t.length()));
    }

    private static void table(PrintWriter w, Map<String, Integer> m, int total, String label) {
        w.println(String.format("  %-22s %8s %7s", label, "count", "%"));
        for (Map.Entry<String, Integer> e : m.entrySet()) {
            w.println(String.format("  %-22s %8d %6s%%", e.getKey(), e.getValue(), InfluenceCsvExporter.pct(e.getValue().intValue(), total)));
        }
    }

    private static void crossTab(PrintWriter w, Map<String, Map<String, Integer>> t) {
        for (Map.Entry<String, Map<String, Integer>> r : t.entrySet()) {
            int total = 0;
            for (Integer v : r.getValue().values()) total += v.intValue();
            StringBuilder sb = new StringBuilder();
            sb.append("  ").append(String.format("%-20s", cut(r.getKey(), 20))).append(" n=").append(String.format("%-6d", total));
            for (Map.Entry<String, Integer> c : r.getValue().entrySet()) {
                sb.append(' ').append(c.getKey()).append('=').append(InfluenceCsvExporter.pct(c.getValue().intValue(), total)).append('%');
            }
            w.println(sb);
        }
    }

    private static String topTypes(Map<String, Integer> counts, int n) {
        List<Map.Entry<String, Integer>> list = new java.util.ArrayList<Map.Entry<String, Integer>>(counts.entrySet());
        java.util.Collections.sort(list, new java.util.Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> x, Map.Entry<String, Integer> y) {
                return y.getValue().intValue() - x.getValue().intValue();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(n, list.size()); i++) {
            if (list.get(i).getValue().intValue() == 0) break;
            if (sb.length() > 0) sb.append(", ");
            sb.append(list.get(i).getKey()).append('(').append(list.get(i).getValue()).append(')');
        }
        return sb.toString();
    }

    private static String cut(String s, int n) {
        if (s == null) return "";
        return s.length() > n ? s.substring(0, n - 1) + "~" : s;
    }
}
