package influenceMiner;

public class InfluenceClassifier {

    public static String classify(String sentence) {

        if (sentence == null) {
            return InfluenceTaxonomy.UNKNOWN;
        }

        String s = sentence.toLowerCase();

        if (
            s.contains("future of python") ||
            s.contains("direction of the language") ||
            s.contains("python philosophy") ||
            s.contains("long term") ||
            s.contains("governance") ||
            s.contains("ecosystem")
        ) {
            return InfluenceTaxonomy.STRATEGIC;
        }

        if (
            s.contains("maintenance") ||
            s.contains("release") ||
            s.contains("testing") ||
            s.contains("documentation") ||
            s.contains("migration") ||
            s.contains("support burden")
        ) {
            return InfluenceTaxonomy.OPERATIONAL;
        }

        if (
            s.contains("syntax") ||
            s.contains("implementation") ||
            s.contains("performance") ||
            s.contains("runtime") ||
            s.contains("api") ||
            s.contains("parser")
        ) {
            return InfluenceTaxonomy.FUNCTIONAL;
        }

        if (
            s.contains("funded") ||
            s.contains("sponsored") ||
            s.contains("grant") ||
            s.contains("paid work") ||
            s.contains("company-funded")
        ) {
            return InfluenceTaxonomy.ECONOMIC;
        }

        if (
            s.contains("google") ||
            s.contains("microsoft") ||
            s.contains("red hat") ||
            s.contains("enterprise") ||
            s.contains("our company")
        ) {
            return InfluenceTaxonomy.ORGANIZATIONAL;
        }

        if (
            s.contains("community supports") ||
            s.contains("core developers") ||
            s.contains("maintainers") ||
            s.contains("typing community") ||
            s.contains("packaging community")
        ) {
            return InfluenceTaxonomy.COALITION;
        }

        if (
            s.contains("numpy") ||
            s.contains("django") ||
            s.contains("pypi") ||
            s.contains("scientific python") ||
            s.contains("downstream")
        ) {
            return InfluenceTaxonomy.ECOSYSTEM;
        }

        if (
            s.contains("bdfl") ||
            s.contains("steering council") ||
            s.contains("delegate") ||
            s.contains("pronouncement") ||
            s.contains("core team")
        ) {
            return InfluenceTaxonomy.AUTHORITY;
        }

        if (
            s.contains("security") ||
            s.contains("unsafe") ||
            s.contains("vulnerability") ||
            s.contains("attack surface") ||
            s.contains("privacy")
        ) {
            return InfluenceTaxonomy.SECURITY;
        }

        if (
            s.contains("backward compatibility") ||
            s.contains("break existing") ||
            s.contains("deprecation") ||
            s.contains("migration cost")
        ) {
            return InfluenceTaxonomy.COMPATIBILITY;
        }

        if (
            s.contains("java") ||
            s.contains("rust") ||
            s.contains("javascript") ||
            s.contains("posix") ||
            s.contains("unicode")
        ) {
            return InfluenceTaxonomy.STANDARDS;
        }

        if (
            s.contains("users want") ||
            s.contains("many users") ||
            s.contains("community demand") ||
            s.contains("real-world use case")
        ) {
            return InfluenceTaxonomy.USER_DEMAND;
        }

        return InfluenceTaxonomy.UNKNOWN;
    }
}
