package sentimentMiner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import connections.MysqlConnect;
import preferenceMiner.PreferenceDecisionResolver;

public class SentimentExtractor {

    public static void extractSentiments(int proposalNumber, String proposalIdentifier) {

        try {

            Connection connection = MysqlConnect.connect();
            Statement statement = connection.createStatement();

            String finalDecision = PreferenceDecisionResolver.getFinalDecision(
                    connection,
                    proposalNumber,
                    proposalIdentifier
            );

            String sql = "SELECT proposal, messageid, sentence, msgAuthorRole, date "
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

                String sentiment = SentimentClassifier.classify(sentence);

                boolean stress = SentimentClassifier.detectStressSignal(sentence);
                boolean toxicity = SentimentClassifier.detectToxicitySignal(sentence);

                if (
                    sentiment.equals("neutral") &&
                    !stress &&
                    !toxicity
                ) {
                    continue;
                }

                SentimentCandidate candidate = new SentimentCandidate();

                candidate.proposalIdentifier = proposalIdentifier;
                candidate.proposalNumber = proposalNumber;
                candidate.messageId = String.valueOf(rs.getInt("messageid"));
                candidate.sentence = sentence;
                candidate.authorRole = rs.getString("msgAuthorRole");

                candidate.sentimentLabel = sentiment;

                if (sentiment.equals("positive")) {
                    candidate.sentimentScore = 1.0;
                } else if (sentiment.equals("negative")) {
                    candidate.sentimentScore = -1.0;
                } else {
                    candidate.sentimentScore = 0.0;
                }

                candidate.emotionCategory = sentiment;
                candidate.stressSignal = stress;
                candidate.toxicitySignal = toxicity;

                candidate.governanceEra = GovernanceEraResolver.resolveEra(
                        rs.getString("date")
                );

                candidate.finalDecision = finalDecision;

                SentimentDatabaseWriter.saveCandidate(connection, candidate);
                saved++;
            }

            System.out.println("Sentiment extraction completed.");
            System.out.println("Sentences checked: " + checked);
            System.out.println("Sentiment candidates saved: " + saved);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
