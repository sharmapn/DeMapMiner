package influenceMiner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import connections.MysqlConnect;

/*
 * InfluencePipeline
 *
 * Convenience runner that executes the whole Influence Miner workflow:
 *   extraction -> aggregation -> CSV export -> annotation sample -> report
 *
 * Usage:
 *   java influenceMiner.InfluencePipeline pep 572
 *   java influenceMiner.InfluencePipeline pep 1 800
 *   java influenceMiner.InfluencePipeline pep all
 *   java influenceMiner.InfluencePipeline pep --no-extract     (re-run the reporting steps only)
 * The InfluenceBatchRunner options (--clear, --keep, --clear-all, --limit N) are passed through.
 */
public class InfluencePipeline {

    public static void main(String[] args) {

        boolean extract = true;
        List<String> forwarded = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--no-extract")) extract = false;
            else forwarded.add(args[i]);
        }
        String identifier = forwarded.isEmpty() ? "pep" : forwarded.get(0).toLowerCase();

        if (extract) {
            InfluenceBatchRunner.main(forwarded.toArray(new String[forwarded.size()]));
        }

        Connection connection = MysqlConnect.connect();
        if (connection == null) {
            System.err.println("No database connection.");
            return;
        }
        try {
            InfluenceSchemaManager.ensureSchema(connection);
            InfluenceResultAggregator.Aggregates a = InfluenceResultAggregator.aggregate(connection, identifier);
            InfluenceResultAggregator.aggregateActorSummary(connection, a);
            InfluenceResultAggregator.aggregateProposalSummary(connection, a);
            InfluenceMessageRanker.rebuild(connection, identifier);   // message-based ranking (MBS), Sept 2026
            InfluenceCsvExporter.exportAll(connection, a, InfluenceConfig.outputDir());
            InfluenceAnnotationSampler.exportSample(a.candidates, InfluenceConfig.outputFile(InfluenceAnnotationSampler.FILE_NAME).getPath(),
                    100, 100, 20, 42L);
            InfluenceReportGenerator.generateReport(connection, a, InfluenceConfig.outputFile(InfluenceReportGenerator.FILE_NAME).getPath());
            System.out.println("Influence Miner pipeline finished. Outputs in " + InfluenceConfig.outputDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
