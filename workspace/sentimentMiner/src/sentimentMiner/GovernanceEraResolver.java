package sentimentMiner;

import java.time.LocalDate;

/*
 * Resolves whether a message belongs to:
 * - BDFL era
 * - Steering Council era
 *
 * Guido van Rossum stepped down as BDFL in July 2018.
 */
public class GovernanceEraResolver {

    public static String resolveEra(String dateString) {

        try {

            if (dateString == null || dateString.trim().length() < 10) {
                return "unknown";
            }

            LocalDate cutoff = LocalDate.of(2018, 7, 11);

            String cleaned = dateString.substring(0, 10);
            LocalDate messageDate = LocalDate.parse(cleaned);

            if (messageDate.isBefore(cutoff)) {
                return "bdfl_era";
            }

            return "steering_council_era";

        } catch (Exception e) {
            return "unknown";
        }
    }
}
