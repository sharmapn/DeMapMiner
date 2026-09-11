package influenceMiner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/*
 * InfluenceTemporalAnalyzer
 *
 * Replaces the placeholder "daysBeforeDecision = 5" with real values derived
 * from the message timestamp and the resolved decision date.
 *
 * daysBeforeDecision:
 *   > 0   message was sent that many days BEFORE the decision
 *   = 0   same day
 *   < 0   message was sent AFTER the decision (post-decision discussion)
 *   UNKNOWN_DAYS when either date is missing / unparseable.
 *
 * NOTE on the sentinel: the coding brief suggested -1 for "unknown", but a
 * genuine value of -1 (one day after the decision) is common in this data,
 * so we use Integer.MIN_VALUE instead and store NULL in the database.
 * Heuristics must call isKnown() before using the number.
 *
 * Decision phases (coarse, date-only, no thread structure yet):
 *   pre_draft         message before the proposal's created date
 *   early_discussion  within 30 days after creation
 *   active_debate     more than 30 days after creation and more than 60 days before decision
 *   revision          60..15 days before decision
 *   decision_window   14..0 days before decision
 *   post_decision     after the decision
 *   unknown           no usable dates
 */
public class InfluenceTemporalAnalyzer {

    public static final int UNKNOWN_DAYS = Integer.MIN_VALUE;

    public static final int DECISION_WINDOW_DAYS = 14;
    public static final int REVISION_WINDOW_DAYS = 60;
    public static final int EARLY_DISCUSSION_DAYS = 30;

    private static final DateTimeFormatter[] FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    public static boolean isKnown(int days) {
        return days != UNKNOWN_DAYS;
    }

    public static int daysBeforeDecision(String messageDate, String decisionDate) {
        LocalDateTime m = parseDateTime(messageDate);
        LocalDateTime d = parseDateTime(decisionDate);
        if (m == null || d == null) {
            return UNKNOWN_DAYS;
        }
        long days = ChronoUnit.DAYS.between(m.toLocalDate(), d.toLocalDate());
        if (days > Integer.MAX_VALUE || days < Integer.MIN_VALUE + 1) {
            return UNKNOWN_DAYS;
        }
        return (int) days;
    }

    public static int daysAfterCreation(String messageDate, String createdDate) {
        LocalDateTime m = parseDateTime(messageDate);
        LocalDateTime c = parseDateTime(createdDate);
        if (m == null || c == null) {
            return UNKNOWN_DAYS;
        }
        return (int) ChronoUnit.DAYS.between(c.toLocalDate(), m.toLocalDate());
    }

    public static String detectDecisionPhase(String messageDate, String decisionDate) {
        return detectDecisionPhase(messageDate, decisionDate, null);
    }

    public static String detectDecisionPhase(String messageDate, String decisionDate, String createdDate) {

        int before = daysBeforeDecision(messageDate, decisionDate);
        int sinceCreated = daysAfterCreation(messageDate, createdDate);

        if (!isKnown(before) && !isKnown(sinceCreated)) {
            return "unknown";
        }

        if (isKnown(sinceCreated) && sinceCreated < 0) {
            return "pre_draft";
        }

        if (isKnown(before)) {
            if (before < 0) return "post_decision";
            if (before <= DECISION_WINDOW_DAYS) return "decision_window";
            if (before <= REVISION_WINDOW_DAYS) return "revision";
        }

        if (isKnown(sinceCreated) && sinceCreated <= EARLY_DISCUSSION_DAYS) {
            return "early_discussion";
        }

        if (isKnown(before)) {
            return "active_debate";
        }

        // Only creation date known and message is well after it.
        return "active_debate";
    }

    public static String governanceEra(String messageDate) {
        LocalDateTime m = parseDateTime(messageDate);
        return InfluenceConfig.governanceEra(m == null ? null : m.toLocalDate());
    }

    /*
     * Lenient parser for the date strings that come back from JDBC getString()
     * on DATE / DATETIME / TIMESTAMP columns and from CSV files.
     */
    public static LocalDateTime parseDateTime(String value) {

        if (value == null) {
            return null;
        }
        String v = value.trim();
        if (v.length() == 0 || v.startsWith("0000-00-00")) {
            return null;
        }
        if (v.length() > 19 && v.charAt(10) == ' ' && v.indexOf('.') == 19) {
            v = v.substring(0, 19);
        }

        for (int i = 0; i < FORMATS.length; i++) {
            try {
                if (FORMATS[i].toString().contains("H")) {
                    return LocalDateTime.parse(v, FORMATS[i]);
                }
                return LocalDate.parse(v, FORMATS[i]).atStartOfDay();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static String toSqlDateTime(String value) {
        LocalDateTime dt = parseDateTime(value);
        if (dt == null) {
            return null;
        }
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
