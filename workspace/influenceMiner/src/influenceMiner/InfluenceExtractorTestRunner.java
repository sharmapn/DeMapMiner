package influenceMiner;

public class InfluenceExtractorTestRunner {

    public static void main(String[] args) {

        int proposalNumber = 308;
        String proposalIdentifier = "pep";

        System.out.println("Starting Influence Miner backend test...");

        InfluenceExtractor.extractInfluences(
                proposalNumber,
                proposalIdentifier
        );

        System.out.println("Influence Miner backend test completed.");
    }
}
