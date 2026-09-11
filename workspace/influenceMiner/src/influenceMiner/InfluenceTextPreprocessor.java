package influenceMiner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * InfluenceTextPreprocessor
 *
 * Turns a raw mailing-list message body into a list of candidate sentences.
 *
 * Stage 1 - line-level cleaning (cleanMessageBody):
 *   - drops quoted text ("> ...", "On ... wrote:", forwarded header blocks)
 *   - drops signature blocks ("-- ", "Regards,", "Cheers," ...)
 *   - drops embedded PEP/BIP text attachments ("PEP: 572 / Title: ...")
 *   - drops obvious code / traceback / diff lines
 *   - replaces URLs with <URL>
 *   - normalises whitespace
 * Stage 2 - sentence splitting (splitIntoSentences):
 *   - paragraph-aware split on . ! ? followed by whitespace
 *   - keeps short vote lines such as "+1" and "-1" as their own sentences
 *   - protects common abbreviations and "PEP 572" style references
 *
 * This is deliberately rule-based and dependency-free so that it runs
 * anywhere the existing DeMaP Miner code runs. Later versions can swap in
 * OpenNLP / Stanford CoreNLP for sentence splitting; the interface stays.
 *
 * Do NOT over-clean: the research depends on real discussion content, so
 * every rule here is meant to remove only text that is clearly not the
 * author's own contribution to the thread.
 */
public class InfluenceTextPreprocessor {

    private static final Pattern URL_PATTERN =
            Pattern.compile("(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);

    private static final Pattern QUOTE_LINE =
            Pattern.compile("^\\s*(>+|\\|)\\s?.*|^\\s*[A-Za-z0-9_.-]+>\\s+.*");

    private static final Pattern WROTE_LINE =
            Pattern.compile("^\\s*(on\\s.*\\bwrote:?\\s*$|.*\\bwrote:\\s*$|.*\\bwrites:\\s*$|.*\\bschrieb:\\s*$|.*\\ba écrit\\s*:\\s*$)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern HEADER_LINE =
            Pattern.compile("^\\s*(from|to|cc|bcc|subject|date|sent|message-id|in-reply-to|references|reply-to)\\s*:\\s.*",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern FORWARDED_LINE =
            Pattern.compile("^\\s*-+\\s*(original message|forwarded message|begin forwarded message)\\s*-+\\s*$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern SIGNATURE_SEPARATOR =
            Pattern.compile("^\\s*-{2,3}\\s*$");

    private static final Pattern SIGNATURE_WORD =
            Pattern.compile("^\\s*(regards|best regards|kind regards|warm regards|best|cheers|thanks|thank you|thx|sincerely|yours|--\\s*\\w+.*|\\w+\\s*--)\\s*[,.!]?\\s*$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern PEP_ATTACHMENT_START =
            Pattern.compile("^\\s*(pep|bip)\\s*:\\s*\\d+\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern PEP_ATTACHMENT_HEADER =
            Pattern.compile("^\\s*(title|version|last-modified|author|status|type|content-type|created|python-version|post-history|resolution|discussions-to|layer|comments-summary|license|requires|replaces|superseded-by)\\s*:\\s.*",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern DIVIDER_LINE =
            Pattern.compile("^\\s*[-=_*~#]{4,}\\s*$");

    private static final Pattern CODE_LINE =
            Pattern.compile("^\\s*(>>>|\\.\\.\\.|def |class |import |from \\S+ import|return |print\\(|if .*:\\s*$|for .*:\\s*$|while .*:\\s*$|try:|except.*:|@\\w+|\\+\\+\\+ |--- |diff --|index [0-9a-f]+\\.\\.|@@ .* @@|traceback \\(most recent call last\\)|file \".*\", line \\d+).*",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern CODE_HEAVY =
            Pattern.compile(".*[{}\\[\\]();=<>]{3,}.*");

    private static final Pattern MAILMAN_FOOTER =
            Pattern.compile("^\\s*(_{5,}|python-\\w+ mailing list|https?://mail\\.python\\.org|unsubscribe:.*|code of conduct:.*|message archived at.*)\\s*$",
                    Pattern.CASE_INSENSITIVE);

    /* Gmail-style quote intros and header debris that survive DeMaP Miner's own cleaning:
     *   "2018-04-20 14:54 GMT-07:00 Mike Miller <python-dev at mgmiller.net>:"
     *   "<pbftau$r2d$1@blaine.gmane.org> <CAExdVNn...@mail.gmail.com>"
     *   "Chris Barker via Python-Dev <python-dev at python.org>:" */
    private static final Pattern DATED_QUOTE_INTRO =
            Pattern.compile("^\\s*\\d{4}-\\d{2}-\\d{2}\\s+\\d{1,2}:\\d{2}.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern ANGLE_ADDRESS =
            Pattern.compile("<[^<>\\s]+(@| at )[^<>\\s]+>");
    private static final Pattern MESSAGE_ID_LINE =
            Pattern.compile("^\\s*(<[^<>\\s]+@[^<>\\s]+>\\s*)+$");

    private static final Pattern VOTE_LINE =
            Pattern.compile("^\\s*[+-]\\s?[01](\\.\\d)?\\b.*", Pattern.CASE_INSENSITIVE);

    /*
     * Abbreviations after which a period should not end a sentence.
     */
    private static final String[] ABBREVIATIONS = {
            "e.g", "i.e", "etc", "vs", "cf", "mr", "mrs", "ms", "dr", "prof", "no", "fig", "approx",
            "st", "jr", "sr", "inc", "ltd", "co", "p.s", "ps", "a.k.a", "aka", "resp", "ca"
    };

    public static String cleanMessageBody(String body) {
        return cleanMessageBody(body, null);
    }

    public static String cleanMessageBody(String body, String authorName) {

        if (body == null) {
            return "";
        }

        String text = body.replace("\r\n", "\n").replace("\r", "\n");
        text = URL_PATTERN.matcher(text).replaceAll("<URL>");

        String[] lines = text.split("\n");
        StringBuilder out = new StringBuilder();

        boolean inSignature = false;
        boolean inAttachment = false;
        boolean inCode = false;
        int attachmentHeaderRun = 0;
        String firstName = firstName(authorName);

        for (int i = 0; i < lines.length; i++) {

            String raw = lines[i];
            String trimmed = raw.trim();
            String lower = trimmed.toLowerCase();

            if (trimmed.length() == 0) {
                out.append("\n\n");
                inCode = false;
                continue;
            }

            if (MAILMAN_FOOTER.matcher(trimmed).matches()) {
                continue;
            }

            // Signature block: everything after "-- " or a closing word is dropped.
            if (SIGNATURE_SEPARATOR.matcher(trimmed).matches()) {
                inSignature = true;
                continue;
            }
            if (!inSignature && isSignatureLine(lower, firstName)) {
                inSignature = true;
                continue;
            }
            if (inSignature) {
                // A signature normally ends the message, but some people put
                // their sig before a quoted reply. Stop skipping once we see a
                // clearly new paragraph of prose longer than a sig line.
                if (trimmed.length() > 120 && looksLikeProse(trimmed)) {
                    inSignature = false;
                } else {
                    continue;
                }
            }

            // Quoted / forwarded material is another author's text.
            if (QUOTE_LINE.matcher(raw).matches()) {
                continue;
            }
            if (WROTE_LINE.matcher(trimmed).matches()) {
                continue;
            }
            if (HEADER_LINE.matcher(trimmed).matches() || FORWARDED_LINE.matcher(trimmed).matches()) {
                continue;
            }
            if (DIVIDER_LINE.matcher(trimmed).matches()) {
                continue;
            }
            if (DATED_QUOTE_INTRO.matcher(trimmed).matches() || MESSAGE_ID_LINE.matcher(trimmed).matches()) {
                continue;
            }
            if (ANGLE_ADDRESS.matcher(trimmed).find() && (trimmed.endsWith(":") || countMatches(ANGLE_ADDRESS, trimmed) >= 2
                    || lower.contains(" via ") || lower.startsWith("on "))) {
                continue;
            }

            // Embedded proposal text ("PEP: 572" followed by RFC-822 style headers).
            if (PEP_ATTACHMENT_START.matcher(trimmed).matches()) {
                inAttachment = true;
                attachmentHeaderRun = 1;
                continue;
            }
            if (inAttachment) {
                if (PEP_ATTACHMENT_HEADER.matcher(trimmed).matches()) {
                    attachmentHeaderRun++;
                    continue;
                }
                // Once past the header block we keep skipping the attached
                // proposal body until the end of the message: the author's own
                // words almost always come before the attachment.
                if (attachmentHeaderRun >= 2) {
                    continue;
                }
                inAttachment = false;
            }

            // Code blocks, diffs, tracebacks.
            if (CODE_LINE.matcher(trimmed).matches() || (CODE_HEAVY.matcher(trimmed).matches() && !looksLikeProse(trimmed))) {
                inCode = true;
                continue;
            }
            if (inCode) {
                if (looksLikeProse(trimmed)) {
                    inCode = false;
                } else {
                    continue;
                }
            }

            out.append(raw.trim()).append("\n");
        }

        return out.toString()
                .replaceAll("[\\t ]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    /*
     * Splits cleaned text into sentences. Paragraph boundaries always end a
     * sentence; inside a paragraph we join wrapped lines and split on
     * terminal punctuation followed by whitespace, protecting abbreviations
     * and proposal references such as "PEP 572." at line ends.
     */
    public static String[] splitIntoSentences(String cleanedBody) {
        List<String> result = splitIntoSentenceList(cleanedBody);
        return result.toArray(new String[result.size()]);
    }

    public static List<String> splitIntoSentenceList(String cleanedBody) {

        List<String> sentences = new ArrayList<String>();
        if (cleanedBody == null || cleanedBody.trim().length() == 0) {
            return sentences;
        }

        String[] paragraphs = cleanedBody.split("\\n\\s*\\n");

        for (int p = 0; p < paragraphs.length; p++) {

            String paragraph = paragraphs[p].trim();
            if (paragraph.length() == 0) {
                continue;
            }

            // Vote-style one-liners ("+1", "-1 from me") stay whole.
            String[] lines = paragraph.split("\n");
            StringBuilder joined = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (VOTE_LINE.matcher(line).matches() && line.length() < 80) {
                    if (joined.length() > 0) {
                        addSentences(sentences, joined.toString());
                        joined.setLength(0);
                    }
                    sentences.add(line);
                    continue;
                }
                if (joined.length() > 0) {
                    joined.append(' ');
                }
                joined.append(line);
            }
            if (joined.length() > 0) {
                addSentences(sentences, joined.toString());
            }
        }

        return sentences;
    }

    private static void addSentences(List<String> sentences, String paragraph) {

        String text = protectAbbreviations(paragraph);
        String[] parts = text.split("(?<=[.!?])\\s+(?=[\"'(\\[]?[A-Z0-9+\\-<])");

        for (int i = 0; i < parts.length; i++) {
            String s = restoreAbbreviations(parts[i]).trim();
            if (s.length() == 0) {
                continue;
            }
            sentences.add(s);
        }
    }

    private static String protectAbbreviations(String text) {
        String t = text;
        for (int i = 0; i < ABBREVIATIONS.length; i++) {
            String abbr = ABBREVIATIONS[i];
            t = Pattern.compile("(?i)\\b" + Pattern.quote(abbr) + "\\.")
                    .matcher(t).replaceAll(Matcher.quoteReplacement(abbr.replace(".", "<DOT>") + "<DOT>"));
        }
        // Version numbers and decimals: 3.7 , 2.0
        t = t.replaceAll("(\\d)\\.(\\d)", "$1<DOT>$2");
        return t;
    }

    private static String restoreAbbreviations(String text) {
        return text.replace("<DOT>", ".");
    }

    private static int countMatches(Pattern p, String text) {
        Matcher m = p.matcher(text);
        int n = 0;
        while (m.find()) n++;
        return n;
    }

    private static boolean isSignatureLine(String lower, String firstName) {
        if (SIGNATURE_WORD.matcher(lower).matches()) {
            return true;
        }
        if (firstName != null && firstName.length() > 1) {
            // "-- Chris", "Chris", "ChrisA" alone on a line near the end
            if (lower.equals(firstName) || lower.equals("-" + firstName) || lower.equals("--" + firstName)
                    || lower.equals("- " + firstName) || lower.equals("-- " + firstName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean looksLikeProse(String line) {
        int letters = 0;
        int spaces = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isLetter(c)) letters++;
            else if (c == ' ') spaces++;
        }
        return letters >= 12 && spaces >= 3 && letters > line.length() / 2;
    }

    private static String firstName(String authorName) {
        if (authorName == null) {
            return null;
        }
        String n = authorName.trim().toLowerCase();
        if (n.startsWith("from ")) {
            n = n.substring(5).trim();
        }
        int sp = n.indexOf(' ');
        return sp > 0 ? n.substring(0, sp) : n;
    }

    /*
     * Cheap sentence-level noise filter used after splitting.
     */
    public static boolean isNoiseSentence(String sentence) {

        if (sentence == null) {
            return true;
        }
        String s = sentence.trim();
        String lower = s.toLowerCase();

        if (s.length() < 8) {
            return !VOTE_LINE.matcher(s).matches();
        }
        int words = s.split("\\s+").length;
        if (words < 3 && !VOTE_LINE.matcher(s).matches()) {
            return true;
        }
        if (words > 120) {
            return true;
        }
        if (lower.startsWith("<url>") && words < 6) {
            return true;
        }
        if (lower.matches(".*\\(\\d{4}-\\d{2}-\\d{2}\\).*\\b(opened|closed) by\\b.*")) {
            return true;
        }
        if (ANGLE_ADDRESS.matcher(s).find() || MESSAGE_ID_LINE.matcher(s).matches()) {
            return true;
        }
        if (lower.matches("^(hi|hello|hey|dear)\\b.{0,40}$")) {
            return true;
        }
        int letters = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) letters++;
        }
        return letters < s.length() / 2;
    }
}
