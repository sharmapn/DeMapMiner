package influenceMiner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import connections.MysqlConnect;

/*
 * InfluenceResultAggregator
 *
 * Builds the summary tables and the in-memory distributions that the CSV
 * exporter and report generator use:
 *
 *   influence_actor_summary     one row per (proposal, actor)
 *   influence_proposal_summary  one row per proposal
 *
 * plus distributions by influence type (multi-label aware: a sentence tagged
 * "security,compatibility" counts once for each), direction, scope,
 * outcome alignment and governance era.
 *
 * Usage:  java influenceMiner.InfluenceResultAggregator [pep]
 */
public class InfluenceResultAggregator {

    public static class ActorSummary {
        public String proposalIdentifier;
        public int proposalNumber;
        public String actorKey;
        public String authorName;
        public String authorEmail;
        public String authorRole;
        public int total;
        public Map<String, Integer> typeCounts = zeroTypes();
        public int supporting, blocking, revising, neutral;
        public double scoreSum, maxScore;
        public Set<Integer> proposals = new HashSet<Integer>();
        /* role -> count, so the cross-proposal row can show the most frequent role */
        public Map<String, Integer> roleCounts = new LinkedHashMap<String, Integer>();

        public double averageScore() {
            return total == 0 ? 0.0 : scoreSum / total;
        }
    }

    public static class ProposalSummary {
        public String proposalIdentifier;
        public int proposalNumber;
        public String finalDecision;
        public int total;
        public Set<String> actors = new HashSet<String>();
        public Map<String, Integer> typeCounts = zeroTypes();
        public int supporting, blocking, revising, neutral;
        public int internal, external, mixed, unknownScope;
        public int aligned;
        public double scoreSum, maxScore;

        public double averageScore() {
            return total == 0 ? 0.0 : scoreSum / total;
        }
    }

    public static class Aggregates {
        public List<InfluenceCandidateRepository.StoredCandidate> candidates;
        public Map<String, ActorSummary> actorsByProposal = new LinkedHashMap<String, ActorSummary>();
        public Map<String, ActorSummary> actorsOverall = new LinkedHashMap<String, ActorSummary>();
        public Map<Integer, ProposalSummary> proposals = new TreeMap<Integer, ProposalSummary>();
        public Map<String, Integer> typeDistribution = zeroTypes();
        public Map<String, Integer> primaryTypeDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> directionDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> scopeDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> targetDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> roleDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> phaseDistribution = new TreeMap<String, Integer>();
        public Map<String, Integer> outcomeDistribution = new TreeMap<String, Integer>();
        /* outcome -> type -> count */
        public Map<String, Map<String, Integer>> typeByOutcome = new TreeMap<String, Map<String, Integer>>();
        /* outcome -> direction -> count */
        public Map<String, Map<String, Integer>> directionByOutcome = new TreeMap<String, Map<String, Integer>>();
        /* era -> type -> count */
        public Map<String, Map<String, Integer>> typeByEra = new TreeMap<String, Map<String, Integer>>();
        public Map<String, Map<String, Integer>> directionByEra = new TreeMap<String, Map<String, Integer>>();
        public Map<String, Map<String, Integer>> scopeByEra = new TreeMap<String, Map<String, Integer>>();
        public Map<String, Integer> eraDistribution = new TreeMap<String, Integer>();
        /* direction -> [aligned, notAligned] */
        public Map<String, int[]> alignmentByDirection = new TreeMap<String, int[]>();
        /* role -> type -> count */
        public Map<String, Map<String, Integer>> typeByRole = new TreeMap<String, Map<String, Integer>>();
        public int totalCandidates;
        public int totalAligned;
    }

    public static void main(String[] args) {
        String identifier = args.length > 0 ? args[0].toLowerCase() : "pep";
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return;
        }
        try {
            InfluenceSchemaManager.ensureSchema(connection);
            Aggregates a = aggregate(connection, identifier);
            aggregateActorSummary(connection, a);
            aggregateProposalSummary(connection, a);
            System.out.println("Aggregation completed: " + a.totalCandidates + " candidates, " + a.proposals.size()
                    + " proposals, " + a.actorsOverall.size() + " actors.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void aggregateActorSummary(Connection connection) throws Exception {
        aggregateActorSummary(connection, aggregate(connection, null));
    }

    public static void aggregateProposalSummary(Connection connection) throws Exception {
        aggregateProposalSummary(connection, aggregate(connection, null));
    }

    public static Aggregates aggregate(Connection connection, String proposalIdentifier) throws Exception {
        List<InfluenceCandidateRepository.StoredCandidate> rows = InfluenceCandidateRepository.loadAll(connection, proposalIdentifier);
        return aggregate(rows);
    }

    public static Aggregates aggregate(List<InfluenceCandidateRepository.StoredCandidate> rows) {

        Aggregates a = new Aggregates();
        a.candidates = rows;
        a.totalCandidates = rows.size();

        for (int i = 0; i < rows.size(); i++) {
            InfluenceCandidateRepository.StoredCandidate c = rows.get(i);
            String actorKey = InfluenceCandidateRepository.actorKey(c);
            String outcome = c.finalDecision == null ? "unknown" : c.finalDecision;
            String era = c.governanceEra == null ? InfluenceConfig.ERA_UNKNOWN : c.governanceEra;
            String role = c.authorRole == null ? "unknown" : c.authorRole;
            String direction = c.influenceDirection == null ? "neutral" : c.influenceDirection;
            String scope = c.influenceScope == null ? "unknown" : c.influenceScope;

            // per proposal+actor
            String pk = c.proposalNumber + "|" + actorKey;
            ActorSummary as = a.actorsByProposal.get(pk);
            if (as == null) {
                as = newActor(c, actorKey);
                a.actorsByProposal.put(pk, as);
            }
            addToActor(as, c);

            // overall actor
            ActorSummary ov = a.actorsOverall.get(actorKey);
            if (ov == null) {
                ov = newActor(c, actorKey);
                ov.proposalNumber = 0;
                a.actorsOverall.put(actorKey, ov);
            }
            addToActor(ov, c);
            ov.proposals.add(Integer.valueOf(c.proposalNumber));

            // per proposal
            ProposalSummary ps = a.proposals.get(Integer.valueOf(c.proposalNumber));
            if (ps == null) {
                ps = new ProposalSummary();
                ps.proposalIdentifier = c.proposalIdentifier;
                ps.proposalNumber = c.proposalNumber;
                ps.finalDecision = outcome;
                a.proposals.put(Integer.valueOf(c.proposalNumber), ps);
            }
            ps.total++;
            ps.actors.add(actorKey);
            ps.scoreSum += c.score;
            ps.maxScore = Math.max(ps.maxScore, c.score);
            if (c.alignsWithOutcome) ps.aligned++;
            if (direction.equals("supporting")) ps.supporting++;
            else if (direction.equals("blocking")) ps.blocking++;
            else if (direction.equals("revising")) ps.revising++;
            else ps.neutral++;
            if (scope.equals("internal")) ps.internal++;
            else if (scope.equals("external")) ps.external++;
            else if (scope.equals("mixed")) ps.mixed++;
            else ps.unknownScope++;

            // distributions
            String[] types = splitTypes(c.influenceTypes);
            for (int t = 0; t < types.length; t++) {
                inc(a.typeDistribution, types[t]);
                inc(ps.typeCounts, types[t]);
                inc(nested(a.typeByOutcome, outcome), types[t]);
                inc(nested(a.typeByEra, era), types[t]);
                inc(nested(a.typeByRole, role), types[t]);
            }
            inc(a.primaryTypeDistribution, c.primaryInfluenceType == null ? "none" : c.primaryInfluenceType);
            inc(a.directionDistribution, direction);
            inc(a.scopeDistribution, scope);
            inc(a.targetDistribution, c.influenceTarget == null ? "unknown" : c.influenceTarget);
            inc(a.roleDistribution, role);
            inc(a.phaseDistribution, c.decisionPhase == null ? "unknown" : c.decisionPhase);
            inc(a.outcomeDistribution, outcome);
            inc(a.eraDistribution, era);
            inc(nested(a.directionByOutcome, outcome), direction);
            inc(nested(a.directionByEra, era), direction);
            inc(nested(a.scopeByEra, era), scope);

            int[] al = a.alignmentByDirection.get(direction);
            if (al == null) {
                al = new int[2];
                a.alignmentByDirection.put(direction, al);
            }
            if (c.alignsWithOutcome) {
                al[0]++;
                a.totalAligned++;
            } else {
                al[1]++;
            }
        }
        return a;
    }

    public static void aggregateActorSummary(Connection connection, Aggregates a) throws Exception {

        Statement st = connection.createStatement();
        st.executeUpdate("DELETE FROM " + InfluenceSchemaManager.ACTOR_SUMMARY);
        st.close();

        StringBuilder cols = new StringBuilder("proposal_identifier, proposal_number, author_name, author_email, author_role, total_influence_sentences");
        StringBuilder qs = new StringBuilder("?, ?, ?, ?, ?, ?");
        for (int i = 0; i < InfluenceSchemaManager.TYPE_COUNT_COLUMNS.length; i++) {
            cols.append(", ").append(InfluenceSchemaManager.TYPE_COUNT_COLUMNS[i]);
            qs.append(", ?");
        }
        cols.append(", supporting_count, blocking_count, revising_count, neutral_count, average_score, max_score");
        qs.append(", ?, ?, ?, ?, ?, ?");

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + InfluenceSchemaManager.ACTOR_SUMMARY + " (" + cols + ") VALUES (" + qs + ")");
        int n = 0;
        for (ActorSummary s : a.actorsByProposal.values()) {
            int k = 1;
            ps.setString(k++, s.proposalIdentifier);
            ps.setInt(k++, s.proposalNumber);
            ps.setString(k++, s.authorName);
            ps.setString(k++, s.authorEmail);
            ps.setString(k++, s.authorRole);
            ps.setInt(k++, s.total);
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) {
                ps.setInt(k++, s.typeCounts.get(InfluenceTypeDetector.TYPES[i]).intValue());
            }
            ps.setInt(k++, s.supporting);
            ps.setInt(k++, s.blocking);
            ps.setInt(k++, s.revising);
            ps.setInt(k++, s.neutral);
            ps.setDouble(k++, round(s.averageScore()));
            ps.setDouble(k++, s.maxScore);
            ps.addBatch();
            if (++n % 500 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        System.out.println("influence_actor_summary: " + n + " rows written.");
    }

    public static void aggregateProposalSummary(Connection connection, Aggregates a) throws Exception {

        Statement st = connection.createStatement();
        st.executeUpdate("DELETE FROM " + InfluenceSchemaManager.PROPOSAL_SUMMARY);
        st.close();

        StringBuilder cols = new StringBuilder("proposal_identifier, proposal_number, final_decision, total_influence_sentences, total_actors");
        StringBuilder qs = new StringBuilder("?, ?, ?, ?, ?");
        for (int i = 0; i < InfluenceSchemaManager.TYPE_COUNT_COLUMNS.length; i++) {
            cols.append(", ").append(InfluenceSchemaManager.TYPE_COUNT_COLUMNS[i]);
            qs.append(", ?");
        }
        cols.append(", supporting_count, blocking_count, revising_count, neutral_count, internal_count, external_count, mixed_count, unknown_scope_count, average_score, max_score");
        qs.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + InfluenceSchemaManager.PROPOSAL_SUMMARY + " (" + cols + ") VALUES (" + qs + ")");
        int n = 0;
        for (ProposalSummary s : a.proposals.values()) {
            int k = 1;
            ps.setString(k++, s.proposalIdentifier);
            ps.setInt(k++, s.proposalNumber);
            ps.setString(k++, s.finalDecision);
            ps.setInt(k++, s.total);
            ps.setInt(k++, s.actors.size());
            for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) {
                ps.setInt(k++, s.typeCounts.get(InfluenceTypeDetector.TYPES[i]).intValue());
            }
            ps.setInt(k++, s.supporting);
            ps.setInt(k++, s.blocking);
            ps.setInt(k++, s.revising);
            ps.setInt(k++, s.neutral);
            ps.setInt(k++, s.internal);
            ps.setInt(k++, s.external);
            ps.setInt(k++, s.mixed);
            ps.setInt(k++, s.unknownScope);
            ps.setDouble(k++, round(s.averageScore()));
            ps.setDouble(k++, s.maxScore);
            ps.addBatch();
            if (++n % 500 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        System.out.println("influence_proposal_summary: " + n + " rows written.");
    }

    /* ---------- helpers shared with exporter / report ---------- */

    public static List<ActorSummary> topActorsByCount(Aggregates a, int n) {
        List<ActorSummary> list = new ArrayList<ActorSummary>(a.actorsOverall.values());
        Collections.sort(list, new Comparator<ActorSummary>() {
            public int compare(ActorSummary x, ActorSummary y) {
                if (y.total != x.total) return y.total - x.total;
                return Double.compare(y.averageScore(), x.averageScore());
            }
        });
        return list.size() > n ? list.subList(0, n) : list;
    }

    public static List<ActorSummary> topActorsByAverageScore(Aggregates a, int n, int minimumSentences) {
        List<ActorSummary> list = new ArrayList<ActorSummary>();
        for (ActorSummary s : a.actorsOverall.values()) {
            if (s.total >= minimumSentences) list.add(s);
        }
        Collections.sort(list, new Comparator<ActorSummary>() {
            public int compare(ActorSummary x, ActorSummary y) {
                int c = Double.compare(y.averageScore(), x.averageScore());
                return c != 0 ? c : y.total - x.total;
            }
        });
        return list.size() > n ? list.subList(0, n) : list;
    }

    public static List<ProposalSummary> proposalsByIntensity(Aggregates a, int n) {
        List<ProposalSummary> list = new ArrayList<ProposalSummary>(a.proposals.values());
        Collections.sort(list, new Comparator<ProposalSummary>() {
            public int compare(ProposalSummary x, ProposalSummary y) {
                return y.total - x.total;
            }
        });
        return list.size() > n ? list.subList(0, n) : list;
    }

    public static Map<String, Integer> zeroTypes() {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < InfluenceTypeDetector.TYPES.length; i++) {
            m.put(InfluenceTypeDetector.TYPES[i], Integer.valueOf(0));
        }
        return m;
    }

    public static String[] splitTypes(String influenceTypes) {
        if (influenceTypes == null || influenceTypes.trim().length() == 0 || influenceTypes.equals("none")) {
            return new String[0];
        }
        String[] parts = influenceTypes.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

    public static void inc(Map<String, Integer> m, String key) {
        Integer v = m.get(key);
        m.put(key, Integer.valueOf(v == null ? 1 : v.intValue() + 1));
    }

    public static Map<String, Integer> nested(Map<String, Map<String, Integer>> outer, String key) {
        Map<String, Integer> m = outer.get(key);
        if (m == null) {
            m = new TreeMap<String, Integer>();
            outer.put(key, m);
        }
        return m;
    }

    public static double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    private static ActorSummary newActor(InfluenceCandidate c, String actorKey) {
        ActorSummary s = new ActorSummary();
        s.proposalIdentifier = c.proposalIdentifier;
        s.proposalNumber = c.proposalNumber;
        s.actorKey = actorKey;
        s.authorName = c.authorName;
        s.authorEmail = c.authorEmail;
        s.authorRole = c.authorRole;
        return s;
    }

    private static void addToActor(ActorSummary s, InfluenceCandidate c) {
        s.total++;
        s.scoreSum += c.score;
        s.maxScore = Math.max(s.maxScore, c.score);
        String[] types = splitTypes(c.influenceTypes);
        for (int t = 0; t < types.length; t++) inc(s.typeCounts, types[t]);
        String d = c.influenceDirection == null ? "neutral" : c.influenceDirection;
        if (d.equals("supporting")) s.supporting++;
        else if (d.equals("blocking")) s.blocking++;
        else if (d.equals("revising")) s.revising++;
        else s.neutral++;
        // across proposals a person may be author, delegate and plain core developer;
        // label the actor with the role they held most often
        inc(s.roleCounts, c.authorRole == null ? "unknown" : c.authorRole);
        String best = null;
        int bestN = -1;
        for (Map.Entry<String, Integer> e : s.roleCounts.entrySet()) {
            if (e.getValue().intValue() > bestN) {
                bestN = e.getValue().intValue();
                best = e.getKey();
            }
        }
        s.authorRole = best;
        if ((s.authorName == null || s.authorName.length() == 0) && c.authorName != null) s.authorName = c.authorName;
    }
}
