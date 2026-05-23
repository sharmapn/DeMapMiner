package sentimentMiner;

public class SentimentExtractorTestRunner {

    public static void main(String[] args) {

        int proposalNumber = 572;
        String proposalIdentifier = "pep";

        System.out.println("Starting Sentiment Miner backend test...");

        SentimentExtractor.extractSentiments(
                proposalNumber,
                proposalIdentifier
        );

        System.out.println("Sentiment Miner backend test completed.");
    }
}
