package preferenceMiner;

public class PreferenceRoleMapper {

    public static String mapRole(String authorEmail, String authorName) {

        if (authorEmail == null && authorName == null) {
            return "unknown";
        }

        // Placeholder implementation.
        // Future versions should map roles from existing DeMaP Miner database tables.
        return "unknown";
    }
}
