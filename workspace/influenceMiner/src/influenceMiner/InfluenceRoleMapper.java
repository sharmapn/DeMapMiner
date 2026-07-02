package influenceMiner;

/*
 * InfluenceRoleMapper
 *
 * Placeholder role mapper for the first Influence Miner integration.
 * Future versions should map roles from existing DeMaP Miner database tables,
 * project-specific metadata, BIP/PEP author lists, GitHub identities,
 * mailing-list identities, and time-sensitive role records.
 */
public class InfluenceRoleMapper {

    public static String mapRole(String authorEmail, String authorName) {

        if (authorEmail == null && authorName == null) {
            return "unknown";
        }

        String email = authorEmail == null ? "" : authorEmail.toLowerCase();
        String name = authorName == null ? "" : authorName.toLowerCase();
        String combined = email + " " + name;

        if (combined.contains("guido") || combined.contains("van rossum")) {
            return "BDFL";
        }

        if (combined.contains("steering")) {
            return "steering council";
        }

        if (combined.contains("delegate")) {
            return "delegate";
        }

        if (combined.contains("maintainer")) {
            return "maintainer";
        }

        if (combined.contains("editor")) {
            return "editor";
        }

        if (combined.contains("core")) {
            return "core developer";
        }

        if (combined.contains("wallet")) {
            return "wallet provider";
        }

        if (combined.contains("miner")) {
            return "miner";
        }

        if (combined.contains("exchange")) {
            return "exchange";
        }

        // Most identities cannot be safely inferred from name/email alone.
        return "unknown";
    }
}
