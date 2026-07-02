package influenceMiner;

/*
 * Detects the likely target being influenced.
 * This helps separate influence on the proposal itself from influence on
 * implementation, governance, security, compatibility, ecosystem, or users.
 */
public class InfluenceTargetDetector {

    public static String detectTarget(String sentence) {

        if (sentence == null) {
            return "unknown";
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("governance") ||
            s.contains("steering council") ||
            s.contains("bdfl") ||
            s.contains("delegate") ||
            s.contains("decision process") ||
            s.contains("authority")
        ) {
            return "governance";
        }

        if (
            s.contains("implement") ||
            s.contains("implementation") ||
            s.contains("release") ||
            s.contains("testing") ||
            s.contains("deploy") ||
            s.contains("migration") ||
            s.contains("maintenance")
        ) {
            return "implementation";
        }

        if (
            s.contains("security") ||
            s.contains("attack") ||
            s.contains("vulnerability") ||
            s.contains("privacy") ||
            s.contains("consensus failure") ||
            s.contains("exploit")
        ) {
            return "security";
        }

        if (
            s.contains("compatibility") ||
            s.contains("compatible") ||
            s.contains("incompatible") ||
            s.contains("breaking change") ||
            s.contains("breaks existing") ||
            s.contains("interoperability")
        ) {
            return "compatibility";
        }

        if (
            s.contains("ecosystem") ||
            s.contains("downstream") ||
            s.contains("wallet") ||
            s.contains("exchange") ||
            s.contains("miner") ||
            s.contains("library") ||
            s.contains("third party")
        ) {
            return "ecosystem";
        }

        if (
            s.contains("users") ||
            s.contains("beginners") ||
            s.contains("newcomers") ||
            s.contains("user demand") ||
            s.contains("pain point")
        ) {
            return "users";
        }

        if (
            s.contains("pep") ||
            s.contains("bip") ||
            s.contains("proposal") ||
            s.contains("this change") ||
            s.contains("this idea")
        ) {
            return "proposal";
        }

        return "unknown";
    }
}
