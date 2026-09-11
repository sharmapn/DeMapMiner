package influenceMiner;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connections.MysqlConnect;

/*
 * InfluenceBatchRunner
 *
 * Runs Influence Miner extraction over one proposal, a range, or every
 * proposal that has messages. Continues after per-proposal errors and
 * writes a summary log to <outputDir>/influence_batch_log.txt.
 *
 * Usage:
 *   java influenceMiner.InfluenceBatchRunner pep 572
 *   java influenceMiner.InfluenceBatchRunner pep 1 800
 *   java influenceMiner.InfluenceBatchRunner pep all
 * Options (anywhere on the command line):
 *   --clear        delete previous influence_candidates rows for the
 *                  proposals being processed before extracting
 *   --clear-all    delete ALL previous rows for the identifier first
 *   --keep         never delete (default is --clear, so re-runs do not
 *                  duplicate rows)
 *   --limit N      process at most N proposals (useful for smoke tests)
 *   --quiet        one line per proposal instead of per-message details
 */
public class InfluenceBatchRunner {

    public static class BatchSummary {
        public int proposalsProcessed;
        public int proposalsFailed;
        public long messagesProcessed;
        public long sentencesProcessed;
        public long candidatesSaved;
        public List<InfluenceExtractor.ExtractionStats> perProposal = new ArrayList<InfluenceExtractor.ExtractionStats>();
        public LocalDateTime started = LocalDateTime.now();
        public LocalDateTime finished;
    }

    private static boolean clearBefore = true;
    private static boolean clearAll = false;
    private static Integer limit = null;

    public static void main(String[] args) {

        List<String> positional = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.equals("--clear")) clearBefore = true;
            else if (a.equals("--clear-all")) clearAll = true;
            else if (a.equals("--keep")) clearBefore = false;
            else if (a.equals("--limit") && i + 1 < args.length) limit = Integer.valueOf(args[++i]);
            else if (a.equals("--quiet")) { /* reserved */ }
            else positional.add(a);
        }

        if (positional.isEmpty()) {
            System.out.println("Usage: InfluenceBatchRunner <identifier> <number> | <from> <to> | all   [--clear|--keep] [--limit N]");
            System.out.println("   e.g. InfluenceBatchRunner pep 572");
            return;
        }

        String identifier = positional.get(0).toLowerCase();
        try {
            if (positional.size() == 1 || positional.get(1).equalsIgnoreCase("all")) {
                runForAllProposals(identifier);
            } else if (positional.size() == 2) {
                runForProposal(Integer.parseInt(positional.get(1)), identifier);
            } else {
                runForProposalRange(Integer.parseInt(positional.get(1)), Integer.parseInt(positional.get(2)), identifier);
            }
        } catch (NumberFormatException e) {
            System.err.println("Proposal numbers must be integers: " + positional);
        }
    }

    public static BatchSummary runForProposal(int proposalNumber, String proposalIdentifier) {
        List<Integer> one = new ArrayList<Integer>();
        one.add(Integer.valueOf(proposalNumber));
        return run(one, proposalIdentifier);
    }

    public static BatchSummary runForProposalRange(int startProposalNumber, int endProposalNumber, String proposalIdentifier) {
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return new BatchSummary();
        }
        try {
            List<Integer> numbers = InfluenceMessageSource.listProposalNumbers(connection,
                    Integer.valueOf(Math.min(startProposalNumber, endProposalNumber)),
                    Integer.valueOf(Math.max(startProposalNumber, endProposalNumber)));
            return run(numbers, proposalIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
            return new BatchSummary();
        }
    }

    public static BatchSummary runForAllProposals(String proposalIdentifier) {
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return new BatchSummary();
        }
        try {
            List<Integer> numbers = InfluenceMessageSource.listProposalNumbers(connection, null, null);
            return run(numbers, proposalIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
            return new BatchSummary();
        }
    }

    public static BatchSummary run(List<Integer> proposalNumbers, String proposalIdentifier) {

        BatchSummary summary = new BatchSummary();
        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return summary;
        }

        PrintWriter log = openLog();
        try {
            InfluenceSchemaManager.ensureSchema(connection);

            if (limit != null && proposalNumbers.size() > limit.intValue()) {
                proposalNumbers = proposalNumbers.subList(0, limit.intValue());
            }

            String header = "Influence Miner batch started " + summary.started + " identifier=" + proposalIdentifier
                    + " proposals=" + proposalNumbers.size() + " minimumScore=" + InfluenceConfig.minimumScore()
                    + " governanceSplit=" + InfluenceConfig.governanceSplitDate();
            System.out.println(header);
            log.println(header);

            if (clearAll) {
                InfluenceSchemaManager.clearCandidates(connection, proposalIdentifier, null);
            }

            for (int i = 0; i < proposalNumbers.size(); i++) {
                int number = proposalNumbers.get(i).intValue();
                System.out.println("-----------------------------------");
                System.out.println("Processing proposal " + number + " (" + (i + 1) + "/" + proposalNumbers.size() + ")");

                InfluenceExtractor.ExtractionStats stats;
                try {
                    if (clearBefore && !clearAll) {
                        InfluenceSchemaManager.clearCandidates(connection, proposalIdentifier, Integer.valueOf(number));
                    }
                    stats = InfluenceExtractor.extractInfluence(connection, number, proposalIdentifier);
                } catch (Exception e) {
                    stats = new InfluenceExtractor.ExtractionStats();
                    stats.proposalNumber = number;
                    stats.failed = true;
                    stats.error = e.toString();
                    e.printStackTrace();
                }

                summary.perProposal.add(stats);
                if (stats.failed) {
                    summary.proposalsFailed++;
                } else {
                    summary.proposalsProcessed++;
                }
                summary.messagesProcessed += stats.messagesProcessed;
                summary.sentencesProcessed += stats.sentencesProcessed;
                summary.candidatesSaved += stats.candidatesSaved;

                System.out.println("Messages processed: " + stats.messagesProcessed);
                System.out.println("Sentences processed: " + stats.sentencesProcessed);
                System.out.println("Influence candidates saved: " + stats.candidatesSaved);
                System.out.println("Outcome: " + stats.finalDecision + (stats.decisionDate != null ? " (" + stats.decisionDate + ")" : "")
                        + " source=" + stats.outcomeSource);
                System.out.println("Completed proposal " + number + (stats.failed ? " WITH ERROR: " + stats.error : ""));
                log.println(stats);
                log.flush();
            }

            summary.finished = LocalDateTime.now();
            String[] footer = {
                    "Influence Miner batch completed.",
                    "Total proposals processed: " + summary.proposalsProcessed + (summary.proposalsFailed > 0 ? " (failed: " + summary.proposalsFailed + ")" : ""),
                    "Total messages processed: " + summary.messagesProcessed,
                    "Total sentences processed: " + summary.sentencesProcessed,
                    "Total candidates saved: " + summary.candidatesSaved,
                    "Elapsed: " + java.time.Duration.between(summary.started, summary.finished).toMinutes() + " min"
            };
            System.out.println("===================================");
            for (int i = 0; i < footer.length; i++) {
                System.out.println(footer[i]);
                log.println(footer[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.println("Batch aborted: " + e);
        } finally {
            log.close();
        }
        return summary;
    }

    private static PrintWriter openLog() {
        try {
            File f = InfluenceConfig.outputFile("influence_batch_log.txt");
            return new PrintWriter(new FileWriter(f, true));
        } catch (Exception e) {
            System.err.println("Could not open batch log, logging to console only: " + e.getMessage());
            return new PrintWriter(System.out) {
                public void close() {
                    flush();
                }
            };
        }
    }
}
