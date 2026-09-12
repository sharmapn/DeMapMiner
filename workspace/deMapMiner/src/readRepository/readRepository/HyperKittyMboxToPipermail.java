package readRepository.readRepository;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/*
 * HyperKittyMboxToPipermail
 *
 * Converts Mailman 3 / HyperKitty mbox exports (mail.python.org/archives/...
 * export/<list>-<yyyy>-<mm>.mbox.gz) into the pipermail monthly text format
 * (<yyyy>-<Month>.txt) that GenericMailingListReader_Main was written for.
 *
 * Differences handled:
 *   - folded (multi-line) headers are unfolded
 *   - RFC 2047 encoded words in From:/Subject: are decoded
 *   - MIME bodies: the first text/plain part is taken (recursively through
 *     multipart), quoted-printable / base64 transfer encodings and the part's
 *     charset are decoded; HTML-only messages fall back to a tag-stripped
 *     text/html part
 *   - the "From " separator line and the "From:" header are rewritten in
 *     pipermail style:  From local at domain  Tue Apr 17 03:46:20 2018
 *                       From: local at domain (Display Name)
 *   - body lines that start with "From " are escaped with ">" like pipermail
 *
 * Usage:
 *   java readRepository.readRepository.HyperKittyMboxToPipermail <inDir> <outDir>
 * Every *.mbox.gz (or *.mbox) in inDir becomes <yyyy>-<Month>.txt in outDir.
 * Months are taken from the file name (…-2019-07.mbox.gz) so that the output
 * sits next to the pipermail files of the same list.
 */
public class HyperKittyMboxToPipermail {

    private static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December" };

    private static final Pattern FILE_MONTH = Pattern.compile("(\\d{4})-(\\d{2})\\.mbox(\\.gz)?$");
    private static final Pattern ENCODED_WORD = Pattern.compile("=\\?([^?]+)\\?([BbQq])\\?([^?]*)\\?=");
    private static final Pattern ADDRESS = Pattern.compile("<([^<>@\\s]+)@([^<>\\s]+)>");
    private static final Pattern BARE_ADDRESS = Pattern.compile("([^<>@\\s\"(),]+)@([^<>\\s\"(),]+)");
    private static final Pattern CHARSET = Pattern.compile("(?i)charset=\"?([A-Za-z0-9._-]+)\"?");
    private static final Pattern BOUNDARY = Pattern.compile("(?i)boundary=\"?([^\";]+)\"?");

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: HyperKittyMboxToPipermail <inDir> <outDir>");
            return;
        }
        File in = new File(args[0]);
        File out = new File(args[1]);
        out.mkdirs();
        File[] files = in.listFiles();
        if (files == null) {
            System.out.println("no files in " + in);
            return;
        }
        java.util.Arrays.sort(files);
        int total = 0;
        for (File f : files) {
            Matcher m = FILE_MONTH.matcher(f.getName());
            if (!m.find()) continue;
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            File target = new File(out, year + "-" + MONTHS[month - 1] + ".txt");
            int n = convert(f, target);
            total += n;
            System.out.println(f.getName() + " -> " + target.getName() + " (" + n + " messages)");
        }
        System.out.println("Converted " + total + " messages into " + out);
    }

    public static int convert(File mbox, File target) throws Exception {
        BufferedReader br = open(mbox);
        PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));
        int n = 0;
        try {
            List<String> raw = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("From ") && !raw.isEmpty() && looksLikeSeparator(line)) {
                    if (writeMessage(raw, w)) n++;
                    raw.clear();
                }
                raw.add(line);
            }
            if (!raw.isEmpty() && writeMessage(raw, w)) n++;
        } finally {
            w.close();
            br.close();
        }
        return n;
    }

    private static boolean looksLikeSeparator(String line) {
        // "From someone@example.com Tue Apr 17 03:46:20 2018"
        return line.matches("From \\S+@\\S+ +[A-Z][a-z]{2} [A-Z][a-z]{2} +\\d{1,2} \\d{2}:\\d{2}:\\d{2} \\d{4}.*")
                || line.matches("From \\S+ +[A-Z][a-z]{2} [A-Z][a-z]{2} +\\d{1,2} \\d{2}:\\d{2}:\\d{2} \\d{4}.*");
    }

    private static boolean writeMessage(List<String> raw, PrintWriter w) {
        if (raw.isEmpty() || !raw.get(0).startsWith("From ")) return false;

        // split headers / body
        int i = 1;
        Map<String, String> headers = new LinkedHashMap<String, String>();
        String current = null;
        StringBuilder value = new StringBuilder();
        for (; i < raw.size(); i++) {
            String l = raw.get(i);
            if (l.length() == 0) { i++; break; }
            if ((l.startsWith(" ") || l.startsWith("\t")) && current != null) {
                value.append(' ').append(l.trim());
            } else {
                if (current != null) headers.put(current, value.toString().trim());
                int c = l.indexOf(':');
                if (c <= 0) { current = null; continue; }
                current = l.substring(0, c).trim().toLowerCase();
                value = new StringBuilder(l.substring(c + 1).trim());
            }
        }
        if (current != null) headers.put(current, value.toString().trim());

        List<String> bodyLines = raw.subList(Math.min(i, raw.size()), raw.size());
        String body = decodeBody(bodyLines, headers.get("content-type"), headers.get("content-transfer-encoding"));

        String from = decodeWords(nz(headers.get("from")));
        String[] addr = splitAddress(from);
        String local = addr[0], domain = addr[1], name = addr[2];
        String date = nz(headers.get("date"));
        String subject = decodeWords(nz(headers.get("subject"))).replaceAll("\\s+", " ");

        w.println("From " + local + " at " + domain + "  " + separatorDate(date, raw.get(0)));
        w.println("From: " + local + " at " + domain + (name.length() > 0 ? " (" + name + ")" : ""));
        w.println("Date: " + date);
        w.println("Subject: " + subject);
        if (headers.containsKey("in-reply-to")) w.println("In-Reply-To: " + headers.get("in-reply-to"));
        if (headers.containsKey("references")) w.println("References: " + headers.get("references"));
        if (headers.containsKey("message-id")) w.println("Message-ID: " + headers.get("message-id"));
        w.println();
        String[] lines = body.split("\r?\n");
        for (String l : lines) {
            if (l.startsWith("From ")) w.println(">" + l);
            else w.println(l);
        }
        w.println();
        return true;
    }

    /* ---------- body decoding ---------- */

    private static String decodeBody(List<String> lines, String contentType, String cte) {
        String ct = contentType == null ? "text/plain" : contentType;
        String lower = ct.toLowerCase();
        if (lower.startsWith("multipart/")) {
            Matcher b = BOUNDARY.matcher(ct);
            if (b.find()) {
                String plain = null, html = null;
                for (List<String> part : splitParts(lines, b.group(1).trim())) {
                    // part = its own headers + body
                    Map<String, String> ph = new LinkedHashMap<String, String>();
                    int k = 0;
                    String cur = null; StringBuilder val = new StringBuilder();
                    for (; k < part.size(); k++) {
                        String l = part.get(k);
                        if (l.length() == 0) { k++; break; }
                        if ((l.startsWith(" ") || l.startsWith("\t")) && cur != null) { val.append(' ').append(l.trim()); }
                        else {
                            if (cur != null) ph.put(cur, val.toString().trim());
                            int c = l.indexOf(':');
                            if (c <= 0) { cur = null; continue; }
                            cur = l.substring(0, c).trim().toLowerCase(); val = new StringBuilder(l.substring(c + 1).trim());
                        }
                    }
                    if (cur != null) ph.put(cur, val.toString().trim());
                    String pct = ph.get("content-type") == null ? "text/plain" : ph.get("content-type");
                    String disp = ph.get("content-disposition");
                    if (disp != null && disp.toLowerCase().startsWith("attachment")) continue;
                    String decoded = decodeBody(part.subList(Math.min(k, part.size()), part.size()), pct, ph.get("content-transfer-encoding"));
                    String pl = pct.toLowerCase();
                    if (pl.startsWith("multipart/")) {
                        if (plain == null && decoded.trim().length() > 0) plain = decoded;
                    } else if (pl.startsWith("text/plain")) {
                        if (plain == null) plain = decoded;
                    } else if (pl.startsWith("text/html")) {
                        if (html == null) html = decoded;
                    }
                }
                if (plain != null) return plain;
                if (html != null) return stripHtml(html);
                return "";
            }
        }
        if (lower.startsWith("text/html")) {
            return stripHtml(decodeTransfer(lines, cte, charsetOf(ct)));
        }
        if (lower.startsWith("text/") || lower.startsWith("message/") || contentType == null) {
            return decodeTransfer(lines, cte, charsetOf(ct));
        }
        return ""; // binary attachment etc.
    }

    private static List<List<String>> splitParts(List<String> lines, String boundary) {
        List<List<String>> parts = new ArrayList<List<String>>();
        List<String> cur = null;
        String sep = "--" + boundary;
        String end = sep + "--";
        for (String l : lines) {
            String t = l.trim();
            if (t.equals(end)) { if (cur != null) parts.add(cur); cur = null; break; }
            if (t.equals(sep)) { if (cur != null) parts.add(cur); cur = new ArrayList<String>(); continue; }
            if (cur != null) cur.add(l);
        }
        if (cur != null) parts.add(cur);
        return parts;
    }

    private static String decodeTransfer(List<String> lines, String cte, String charset) {
        String enc = cte == null ? "" : cte.trim().toLowerCase();
        Charset cs = safeCharset(charset);
        if (enc.equals("base64")) {
            StringBuilder sb = new StringBuilder();
            for (String l : lines) sb.append(l.trim());
            try {
                return new String(Base64.getMimeDecoder().decode(sb.toString()), cs);
            } catch (Exception e) {
                return join(lines);
            }
        }
        if (enc.equals("quoted-printable")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (String l : lines) {
                String s = l;
                boolean soft = s.endsWith("=");
                if (soft) s = s.substring(0, s.length() - 1);
                for (int k = 0; k < s.length(); k++) {
                    char c = s.charAt(k);
                    if (c == '=' && isHex(s, k + 1)) {
                        out.write(Integer.parseInt(s.substring(k + 1, k + 3), 16));
                        k += 2;
                    } else {
                        // the file was read as ISO-8859-1, so each char is one raw byte
                        out.write(c & 0xFF);
                    }
                }
                if (!soft) out.write('\n');
            }
            return new String(out.toByteArray(), cs);
        }
        // 7bit / 8bit: re-interpret the raw bytes in the declared charset
        return new String(join(lines).getBytes(Charset.forName("ISO-8859-1")), cs);
    }

    private static boolean isHex(String s, int k) {
        if (k + 1 >= s.length()) return false;
        return Character.digit(s.charAt(k), 16) >= 0 && Character.digit(s.charAt(k + 1), 16) >= 0;
    }

    private static String stripHtml(String html) {
        String t = html.replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>", " ");
        t = t.replaceAll("(?i)<br\\s*/?>|</p>|</div>|</li>|</tr>", "\n");
        t = t.replaceAll("<[^>]+>", " ");
        t = t.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"").replace("&#39;", "'");
        return t.replaceAll("[ \\t]+", " ").replaceAll("\\n{3,}", "\n\n").trim();
    }

    /* ---------- header helpers ---------- */

    static String decodeWords(String s) {
        if (s == null || s.indexOf("=?") < 0) return s == null ? "" : s;
        Matcher m = ENCODED_WORD.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String cs = m.group(1), kind = m.group(2), text = m.group(3);
            String decoded;
            try {
                if (kind.equalsIgnoreCase("B")) {
                    decoded = new String(Base64.getMimeDecoder().decode(text), safeCharset(cs));
                } else {
                    List<String> one = new ArrayList<String>();
                    one.add(text.replace('_', ' '));
                    decoded = decodeTransfer(one, "quoted-printable", cs).replace("\n", "");
                }
            } catch (Exception e) {
                decoded = text;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(decoded));
        }
        m.appendTail(sb);
        return sb.toString().replaceAll("\\?=\\s+=\\?", "?==?");
    }

    /* returns {local, domain, displayName} */
    static String[] splitAddress(String from) {
        String local = "unknown", domain = "unknown", name = "";
        Matcher m = ADDRESS.matcher(from);
        if (m.find()) {
            local = m.group(1); domain = m.group(2);
            name = from.substring(0, m.start()).trim();
        } else {
            Matcher b = BARE_ADDRESS.matcher(from);
            if (b.find()) {
                local = b.group(1); domain = b.group(2);
                String rest = (from.substring(0, b.start()) + " " + from.substring(b.end())).trim();
                Matcher p = Pattern.compile("\\(([^)]*)\\)").matcher(rest);
                if (p.find()) name = p.group(1).trim(); else name = rest;
            } else {
                name = from.trim();
            }
        }
        name = name.replace("\"", "").replace("'", "").trim();
        if (name.startsWith("via ") || name.length() == 0) {
            name = local;
        }
        // "Name via Python-Dev" (DMARC munging): keep the real name
        int via = name.toLowerCase().indexOf(" via ");
        if (via > 0) name = name.substring(0, via).trim();
        return new String[] { local, domain.replace(">", ""), name };
    }

    private static String separatorDate(String dateHeader, String sepLine) {
        // prefer the mbox separator's own timestamp ("... Tue Apr 17 03:46:20 2018")
        Matcher m = Pattern.compile("([A-Z][a-z]{2} [A-Z][a-z]{2} +\\d{1,2} \\d{2}:\\d{2}:\\d{2} \\d{4})").matcher(sepLine);
        if (m.find()) return m.group(1);
        String[] fmts = { "EEE, d MMM yyyy HH:mm:ss Z", "d MMM yyyy HH:mm:ss Z", "EEE, d MMM yyyy HH:mm Z" };
        for (String f : fmts) {
            try {
                Date d = new SimpleDateFormat(f, Locale.ENGLISH).parse(dateHeader.replaceAll("\\s*\\(.*\\)$", ""));
                return new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.ENGLISH).format(d);
            } catch (Exception ignored) {
            }
        }
        return "Mon Jan  1 00:00:00 1970";
    }

    private static String charsetOf(String contentType) {
        if (contentType == null) return "utf-8";
        Matcher m = CHARSET.matcher(contentType);
        return m.find() ? m.group(1) : "utf-8";
    }

    private static Charset safeCharset(String name) {
        try {
            return Charset.forName(name == null ? "UTF-8" : name.trim());
        } catch (Exception e) {
            return Charset.forName("UTF-8");
        }
    }

    private static BufferedReader open(File f) throws Exception {
        if (f.getName().endsWith(".gz")) {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)), "ISO-8859-1"));
        }
        return new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO-8859-1"));
    }

    private static String join(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String l : lines) sb.append(l).append('\n');
        return sb.toString();
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
