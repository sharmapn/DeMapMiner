package preferenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * PreferenceDecisionResolver
 *
 * Retrieves the final proposal outcome from the database.
 *
 * IMPORTANT:
 * The current implementation is intentionally generic because the exact
 * DeMaP Miner schema may differ between installations.
 *
 * You should later adapt:
 * - table names,
 * - proposal columns,
 * - decision/state columns.
 */
public class PreferenceDecisionResolver {

    public static String getFinalDecision(
            Connection connection,
            int proposalNumber,
            String proposalIdentifier
    ) {

        try {

            Statement statement = connection.createStatement();

            /*
             * Placeholder query.
             *
             * TODO:
             * Replace with actual DeMaP Miner proposal/state table query.
             */
            String sql =
                    "SELECT finalState FROM proposals WHERE proposalNumber = "
                    + proposalNumber;

            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {

                String finalState = rs.getString("finalState");

                if (finalState != null) {
                    return finalState.toLowerCase();
                }
            }

        } catch (Exception e) {
            System.out.println("Could not resolve final decision.");
            e.printStackTrace();
        }

        /*
         * Safe fallback.
         */
        return "unknown";
    }
}
