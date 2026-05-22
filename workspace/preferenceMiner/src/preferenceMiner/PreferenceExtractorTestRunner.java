package preferenceMiner;

/*
 * PreferenceExtractorTestRunner
 *
 * Small console runner for testing Preference Miner before GUI integration.
 *
 * Why this exists:
 * - The backend should be tested before adding buttons/tables to the GUI.
 * - This allows us to run extraction for one proposal and check whether rows
 *   are inserted into preference_candidates.
 *
 * Usage example:
 * - Run this class from Eclipse.
 * - Change proposalNumber and proposalIdentifier below as needed.
 */
public class PreferenceExtractorTestRunner {

    public static void main(String[] args) {

        int proposalNumber = 308;
        String proposalIdentifier = "pep";

        System.out.println("Starting Preference Miner backend test...");
        System.out.println("Proposal: " + proposalIdentifier + " " + proposalNumber);

        PreferenceExtractor.extractPreferences(proposalNumber, proposalIdentifier);

        System.out.println("Preference Miner backend test completed.");
    }
}
