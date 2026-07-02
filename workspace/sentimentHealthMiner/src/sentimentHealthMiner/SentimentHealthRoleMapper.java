package sentimentHealthMiner;

/*
 * SentimentHealthRoleMapper
 *
 * Lightweight role mapping. This first version uses simple name/email cues.
 * Later versions should connect to project role metadata and proposal-specific
 * role tables.
 */
public class SentimentHealthRoleMapper {

    public static String mapRole(String authorEmail, String authorName) {

        String combined = "";

        if (authorEmail != null) {
            combined += authorEmail.toLowerCase() + " ";
        }
        if (authorName != null) {
            combined += authorName.toLowerCase();
        }

        if (combined.trim().length() == 0) {
            return "unknown_contributor";
        }

        if (combined.contains("guido") || combined.contains("van rossum")) {
            return "bdfl_or_former_bdfl";
        }

        if (combined.contains("steering") || combined.contains("council")) {
            return "steering_council";
        }

        if (combined.contains("delegate")) {
            return "proposal_delegate";
        }

        if (combined.contains("editor")) {
            return "bip_or_pep_editor";
        }

        if (combined.contains("core")) {
            return "core_developer";
        }

        if (combined.contains("maintainer")) {
            return "maintainer";
        }

        if (combined.contains("python.org") || combined.contains("python")) {
            return "python_contributor";
        }

        if (combined.contains("bitcoin")) {
            return "bitcoin_contributor";
        }

        return "contributor";
    }
}
