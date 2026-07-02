package sentimentHealthMiner;

/*
 * SentimentHealthExtractorTestRunner
 *
 * Simple command-line runner for testing Sentiment/Health Miner without
 * launching the full DeMaP Miner GUI.
 *
 * Usage examples:
 *   java sentimentHealthMiner.SentimentHealthExtractorTestRunner 572 pep
 *   java sentimentHealthMiner.SentimentHealthExtractorTestRunner 141 bip
 */
public class SentimentHealthExtractorTestRunner {

    public static void main(String[] args) {

        int proposalNumber = 572;
        String proposalIdentifier = "pep";

        if (args != null && args.length > 0) {
            try {
                proposalNumber = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Could not parse proposal number. Using default: " + proposalNumber);
            }
        }

        if (args != null && args.length > 1) {
            proposalIdentifier = args[1];
        }

        System.out.println("Starting Sentiment/Health Miner test run.");
        System.out.println("proposalIdentifier: " + proposalIdentifier);
        System.out.println("proposalNumber: " + proposalNumber);

        SentimentHealthExtractor.extractSentimentHealth(proposalNumber, proposalIdentifier);
    }
}
