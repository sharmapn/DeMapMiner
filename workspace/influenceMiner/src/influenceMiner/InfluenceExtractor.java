package influenceMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;
import preferenceMiner.PreferenceDecisionResolver;

public class InfluenceExtractor {

    public static void extractInfluences(int proposalNumber, String proposalIdentifier) {

        try {

            Connection connection = MysqlConnect.connect();
            Statement statement = connection.createStatement();

            String finalDecision = PreferenceDecisionResolver.getFinalDecision(
                    connection,
                    proposalNumber,
                    proposalIdentifier
            );

            String sql = "SELECT proposal, messageid, sentence, msgAuthorRole, dateDiff "
                    + "FROM allsentences "
                    + "WHERE proposal = " + proposalNumber + " "
                    + "AND sentence IS NOT NULL";

            ResultSet rs = statement.executeQuery(sql);

            int checked = 0;
            int saved = 0;

            while (rs.next()) {

                checked++;

                String sentence = rs.getString("sentence");

                if (sentence == null || sentence.trim().length() < 5) {
                    continue;
                }

                String category = InfluenceClassifier.classify(sentence);

                if (category.equals(InfluenceTaxonomy.UNKNOWN)) {
                    continue;
                }

                InfluenceCandidate candidate = new InfluenceCandidate();

                candidate.proposalIdentifier = proposalIdentifier;
                candidate.proposalNumber = proposalNumber;
                candidate.messageId = String.valueOf(rs.getInt("messageid"));
                candidate.sentence = sentence;
                candidate.authorRole = rs.getString("msgAuthorRole");

                candidate.influenceCategory = category;
                candidate.influenceSubcategory = category;

                candidate.influenceDirection = "neutral";
                candidate.influenceScore = 1.0;

                candidate.finalDecision = finalDecision;
                candidate.alignsWithDecision = false;

                InfluenceDatabaseWriter.saveCandidate(connection, candidate);
                saved++;
            }

            System.out.println("Influence extraction completed.");
            System.out.println("Sentences checked: " + checked);
            System.out.println("Influence candidates saved: " + saved);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
