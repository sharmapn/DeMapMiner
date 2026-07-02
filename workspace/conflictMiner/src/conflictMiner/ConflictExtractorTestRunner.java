package conflictMiner;

/*
 * ConflictExtractorTestRunner
 *
 * Small command-line runner for testing Conflict Miner without opening the GUI.
 *
 * Usage examples:
 *   java conflictMiner.ConflictExtractorTestRunner 572 pep
 *   java conflictMiner.ConflictExtractorTestRunner 2 bip
 */
public class ConflictExtractorTestRunner {

    public static void main(String[] args) {

        if (args == null || args.length < 2) {
            System.out.println("Usage: java conflictMiner.ConflictExtractorTestRunner <proposalNumber> <proposalIdentifier>");
            System.out.println("Example: java conflictMiner.ConflictExtractorTestRunner 572 pep");
            return;
        }

        try {
            int proposalNumber = Integer.parseInt(args[0]);
            String proposalIdentifier = args[1].toLowerCase();

            System.out.println("Conflict Miner test runner started.");
            System.out.println("proposalNumber: " + proposalNumber);
            System.out.println("proposalIdentifier: " + proposalIdentifier);

            ConflictExtractor.extractConflict(proposalNumber, proposalIdentifier);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
