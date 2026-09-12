package readRepository.readRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/*
 * DiscourseToPipermail
 *
 * Crawls discuss.python.org (Discourse) categories through the public JSON
 * API and writes every post as a pipermail-style message into monthly text
 * files, one folder per category, so that GenericMailingListReader_Main can
 * ingest the Steering-Council-era discussions exactly like the old mailing
 * lists:
 *
 *   <outDir>/discourse-peps/2023-January.txt
 *   From colesbury at discuss.python.org  Tue Jan 10 16:08:09 2023
 *   From: colesbury at discuss.python.org (Sam Gross)
 *   Date: Tue, 10 Jan 2023 16:08:09 +0000
 *   Subject: [PEPs] PEP 703 - Making the Global Interpreter Lock Optional in CPython
 *   In-Reply-To: <topic-22606-post-1@discuss.python.org>
 *   References: <topic-22606-post-1@discuss.python.org>
 *   Message-ID: <topic-22606-post-7@discuss.python.org>
 *
 * Usernames stand in for addresses (Discourse does not expose e-mail).
 * Quote blocks ([quote=...]...[/quote]) become "> " lines so that the
 * existing quote handling applies. Raw topic JSON is cached under
 * <outDir>/cache so that a crawl can be resumed and re-converted offline.
 *
 * Usage:
 *   java readRepository.readRepository.DiscourseToPipermail <outDir> <category-slug>:<id> [...]
 *   e.g.  ... C:/datasets/postBDFL_2026/discourse peps:19 core-dev:23 committers:5 ideas:6
 * Optional system properties:
 *   -Ddiscourse.delayMs=400   pause between requests (default 400)
 *   -Ddiscourse.maxTopics=N   stop after N new topics per category (testing)
 *   -Ddiscourse.since=2018-01-01  ignore topics created before this date
 */
public class DiscourseToPipermail {

    private static final String BASE = "https://discuss.python.org";
    private static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December" };

    private static final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL).build();
    private static long delayMs = Long.parseLong(System.getProperty("discourse.delayMs", "400"));
    private static int maxTopics = Integer.parseInt(System.getProperty("discourse.maxTopics", "0"));
    private static String since = System.getProperty("discourse.since", "2018-01-01");

    private static File outDir;
    private static File cacheDir;
    private static int requests = 0;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: DiscourseToPipermail <outDir> <slug>:<categoryId> [...]");
            return;
        }
        outDir = new File(args[0]);
        cacheDir = new File(outDir, "cache");
        cacheDir.mkdirs();
        for (int i = 1; i < args.length; i++) {
            String[] parts = args[i].split(":");
            crawlCategory(parts[0], Integer.parseInt(parts[1]));
        }
        System.out.println("Done. HTTP requests: " + requests);
    }

    /* ---------- crawling ---------- */

    private static void crawlCategory(String slug, int categoryId) throws Exception {
        File listDir = new File(outDir, "discourse-" + slug);
        listDir.mkdirs();
        Set<Long> seen = new HashSet<Long>();
        File doneFile = new File(listDir, ".topics-done");
        if (doneFile.exists()) {
            for (String l : Files.readAllLines(doneFile.toPath())) {
                if (l.trim().length() > 0) seen.add(Long.valueOf(l.trim()));
            }
        }
        System.out.println("Category " + slug + " (" + categoryId + "): " + seen.size() + " topics already done");

        int page = 0;
        int newTopics = 0;
        int emptyPages = 0;
        while (true) {
            String json = get(BASE + "/c/" + slug + "/" + categoryId + ".json?page=" + page);
            if (json == null) break;
            Map<String, Object> root = asMap(Json.parse(json));
            Map<String, Object> topicList = asMap(root.get("topic_list"));
            List<Object> topics = asList(topicList == null ? null : topicList.get("topics"));
            if (topics == null || topics.isEmpty()) break;
            int processedOnPage = 0;
            for (Object o : topics) {
                Map<String, Object> t = asMap(o);
                long id = ((Number) t.get("id")).longValue();
                String created = str(t.get("created_at"));
                if (created.length() >= 10 && created.substring(0, 10).compareTo(since) < 0) continue;
                if (seen.contains(Long.valueOf(id))) continue;
                try {
                    int posts = crawlTopic(slug, id, listDir);
                    seen.add(Long.valueOf(id));
                    appendLine(doneFile, String.valueOf(id));
                    newTopics++;
                    processedOnPage++;
                    System.out.println("  " + slug + " topic " + id + " (" + posts + " posts) " + str(t.get("title")));
                } catch (Exception e) {
                    System.out.println("  topic " + id + " FAILED: " + e);
                }
                if (maxTopics > 0 && newTopics >= maxTopics) {
                    System.out.println("maxTopics reached for " + slug);
                    return;
                }
            }
            if (processedOnPage == 0) emptyPages++; else emptyPages = 0;
            if (topicList.get("more_topics_url") == null) break;
            page++;
        }
        System.out.println("Category " + slug + " listing: " + newTopics + " new topics, " + seen.size() + " total");

        // The category listing is capped at a few hundred topics in any order, so
        // enumerate the rest through the search API, one calendar month at a time
        // ("#slug after:YYYY-MM-01 before:YYYY-MM-01" returns every topic with a
        // post in that window).
        java.time.LocalDate month = java.time.LocalDate.parse(since).withDayOfMonth(1);
        java.time.LocalDate end = java.time.LocalDate.now().plusMonths(1).withDayOfMonth(1);
        int viaSearch = 0;
        while (month.isBefore(end)) {
            java.time.LocalDate next = month.plusDays(15).isBefore(month.plusMonths(1)) ? month.plusDays(15) : month.plusMonths(1);
            for (int sp = 1; sp < 40; sp++) {
                // in:first = topic-opening posts only, so each topic is returned once and a
                // half-month window stays well under the 50-result cap
                String q = "%23" + slug + "%20in%3Afirst%20after%3A" + month + "%20before%3A" + next + "%20order%3Alatest";
                Thread.sleep(4500); // anonymous search trips at ~15-20 requests per minute on discuss.python.org
                String json = get(BASE + "/search.json?q=" + q + "&page=" + sp);
                if (json == null) break;
                Map<String, Object> root = asMap(Json.parse(json));
                List<Object> topics = asList(root.get("topics"));
                if (topics.isEmpty()) break;
                for (Object o : topics) {
                    Map<String, Object> t = asMap(o);
                    long id = ((Number) t.get("id")).longValue();
                    if (seen.contains(Long.valueOf(id))) continue;
                    try {
                        int posts = crawlTopic(slug, id, listDir);
                        seen.add(Long.valueOf(id));
                        appendLine(doneFile, String.valueOf(id));
                        newTopics++;
                        viaSearch++;
                        System.out.println("  " + slug + " topic " + id + " (" + posts + " posts) [search " + month + "] " + str(t.get("title")));
                    } catch (Exception e) {
                        System.out.println("  topic " + id + " FAILED: " + e);
                    }
                    if (maxTopics > 0 && newTopics >= maxTopics) {
                        System.out.println("maxTopics reached for " + slug);
                        return;
                    }
                }
                if (Boolean.TRUE != root.get("more_full_page_results") && topics.size() < 50) break;
            }
            month = next;
        }
        System.out.println("Category " + slug + ": " + newTopics + " new topics this run (" + viaSearch + " via search), " + seen.size() + " total");
    }

    private static int crawlTopic(String slug, long topicId, File listDir) throws Exception {
        File cache = new File(cacheDir, "topic-" + topicId + ".json");
        List<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
        String title;
        if (cache.exists()) {
            Map<String, Object> saved = asMap(Json.parse(new String(Files.readAllBytes(cache.toPath()), StandardCharsets.UTF_8)));
            title = str(saved.get("title"));
            for (Object p : asList(saved.get("posts"))) posts.add(asMap(p));
        } else {
            String json = get(BASE + "/t/" + topicId + ".json?include_raw=1");
            if (json == null) throw new IOException("no topic json");
            Map<String, Object> topic = asMap(Json.parse(json));
            title = str(topic.get("title"));
            Map<String, Object> stream = asMap(topic.get("post_stream"));
            List<Object> first = asList(stream.get("posts"));
            Set<Long> have = new HashSet<Long>();
            for (Object p : first) {
                Map<String, Object> pm = asMap(p);
                posts.add(pm);
                have.add(((Number) pm.get("id")).longValue());
            }
            List<Object> ids = asList(stream.get("stream"));
            List<Long> missing = new ArrayList<Long>();
            for (Object o : ids) {
                long pid = ((Number) o).longValue();
                if (!have.contains(Long.valueOf(pid))) missing.add(Long.valueOf(pid));
            }
            for (int i = 0; i < missing.size(); i += 20) {
                StringBuilder q = new StringBuilder(BASE + "/t/" + topicId + "/posts.json?include_raw=1");
                for (int k = i; k < Math.min(i + 20, missing.size()); k++) q.append("&post_ids[]=").append(missing.get(k));
                String pj = get(q.toString());
                if (pj == null) continue;
                Map<String, Object> pr = asMap(Json.parse(pj));
                Map<String, Object> ps = asMap(pr.get("post_stream"));
                for (Object p : asList(ps.get("posts"))) posts.add(asMap(p));
            }
            // keep only what we need in the cache
            Map<String, Object> saved = new LinkedHashMap<String, Object>();
            saved.put("id", Long.valueOf(topicId));
            saved.put("title", title);
            saved.put("slug", slug);
            List<Object> slim = new ArrayList<Object>();
            for (Map<String, Object> p : posts) {
                Map<String, Object> s = new LinkedHashMap<String, Object>();
                for (String k : new String[] { "id", "post_number", "username", "name", "created_at", "reply_to_post_number", "raw", "cooked" }) {
                    if (p.get(k) != null) s.put(k, p.get(k));
                }
                if (s.get("raw") != null) s.remove("cooked");
                slim.add(s);
            }
            saved.put("posts", slim);
            Files.write(cache.toPath(), Json.write(saved).getBytes(StandardCharsets.UTF_8));
        }
        writePosts(slug, topicId, title, posts, listDir);
        return posts.size();
    }

    /* ---------- output ---------- */

    private static void writePosts(String slug, long topicId, String title, List<Map<String, Object>> posts, File listDir) throws Exception {
        posts.sort((a, b) -> Integer.compare(num(a.get("post_number")), num(b.get("post_number"))));
        Map<Integer, Long> idByNumber = new LinkedHashMap<Integer, Long>();
        for (Map<String, Object> p : posts) idByNumber.put(Integer.valueOf(num(p.get("post_number"))), Long.valueOf(((Number) p.get("id")).longValue()));
        String tag = "[" + capitalise(slug) + "] ";
        String cleanTitle = title.replace(" | peps.python.org", "").replaceAll("\\s+", " ").trim();

        Map<String, PrintWriter> writers = new LinkedHashMap<String, PrintWriter>();
        try {
            for (Map<String, Object> p : posts) {
                String createdIso = str(p.get("created_at"));
                OffsetDateTime dt;
                try {
                    dt = OffsetDateTime.parse(createdIso);
                } catch (Exception e) {
                    continue;
                }
                Date date = Date.from(dt.toInstant());
                String monthFile = dt.getYear() + "-" + MONTHS[dt.getMonthValue() - 1] + ".txt";
                PrintWriter w = writers.get(monthFile);
                if (w == null) {
                    w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(listDir, monthFile), true), "UTF-8"));
                    writers.put(monthFile, w);
                }
                String user = str(p.get("username")).toLowerCase();
                String name = str(p.get("name"));
                if (name.trim().length() == 0) name = user;
                int number = num(p.get("post_number"));
                long postId = ((Number) p.get("id")).longValue();
                String msgId = "<topic-" + topicId + "-post-" + postId + "@discuss.python.org>";
                String parent = null;
                Object rt = p.get("reply_to_post_number");
                if (rt != null && idByNumber.containsKey(Integer.valueOf(num(rt)))) {
                    parent = "<topic-" + topicId + "-post-" + idByNumber.get(Integer.valueOf(num(rt))) + "@discuss.python.org>";
                } else if (number > 1 && idByNumber.containsKey(Integer.valueOf(1))) {
                    parent = "<topic-" + topicId + "-post-" + idByNumber.get(Integer.valueOf(1)) + "@discuss.python.org>";
                }
                String subject = tag + (number > 1 ? "Re: " : "") + cleanTitle;
                String body = p.get("raw") != null ? str(p.get("raw")) : stripHtml(str(p.get("cooked")));
                body = convertQuotes(body);

                SimpleDateFormat sep = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.ENGLISH);
                SimpleDateFormat rfc = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                sep.setTimeZone(TimeZone.getTimeZone("UTC"));
                rfc.setTimeZone(TimeZone.getTimeZone("UTC"));

                w.println("From " + user + " at discuss.python.org  " + sep.format(date));
                w.println("From: " + user + " at discuss.python.org (" + name + ")");
                w.println("Date: " + rfc.format(date));
                w.println("Subject: " + subject);
                if (parent != null) {
                    w.println("In-Reply-To: " + parent);
                    w.println("References: " + parent);
                }
                w.println("Message-ID: " + msgId);
                w.println("X-Discourse-Topic: " + BASE + "/t/" + topicId + "/" + number);
                w.println();
                for (String l : body.split("\r?\n")) {
                    w.println(l.startsWith("From ") ? ">" + l : l);
                }
                w.println();
            }
        } finally {
            for (PrintWriter w : writers.values()) w.close();
        }
    }

    /* [quote="user, post:3, topic:123"] ... [/quote]  ->  "> " lines */
    static String convertQuotes(String body) {
        StringBuilder out = new StringBuilder();
        int depth = 0;
        for (String line : body.split("\r?\n")) {
            String t = line.trim();
            if (t.toLowerCase().startsWith("[quote")) {
                depth++;
                String who = t.replaceAll("(?i)\\[quote=\"?([^,\"\\]]+).*", "$1");
                if (!who.equals(t)) out.append(who).append(" wrote:\n");
                continue;
            }
            if (t.equalsIgnoreCase("[/quote]")) {
                if (depth > 0) depth--;
                continue;
            }
            if (depth > 0) out.append("> ").append(line).append('\n');
            else out.append(line).append('\n');
        }
        return out.toString();
    }

    private static String stripHtml(String html) {
        String t = html.replaceAll("(?i)<br\\s*/?>|</p>|</div>|</li>", "\n");
        t = t.replaceAll("<[^>]+>", " ");
        return t.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"").replace("&#39;", "'");
    }

    /* ---------- HTTP ---------- */

    private static String get(String url) throws Exception {
        for (int attempt = 0; attempt < 6; attempt++) {
            Thread.sleep(delayMs);
            HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("User-Agent", "DeMapMiner/1.0 (academic research tool; contact: pankajeshwara@gmail.com)")
                    .header("Accept", "*/*") /* Discourse rate-limits explicit application/json far more aggressively */.timeout(Duration.ofSeconds(60)).GET().build();
            HttpResponse<String> resp;
            try {
                resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.out.println("  network error, retrying: " + e.getMessage());
                Thread.sleep(5000L * (attempt + 1));
                continue;
            }
            requests++;
            int code = resp.statusCode();
            if (code == 200) return resp.body();
            if (code == 404 || code == 403 || code == 410) {
                System.out.println("  HTTP " + code + " for " + url);
                return null;
            }
            if (code == 429 || code >= 500) {
                long wait = 65000L; // the search quota is per minute: wait for the window to reset
                String ra = resp.headers().firstValue("Retry-After").orElse(null);
                if (ra != null) {
                    try { wait = Math.max(wait, Long.parseLong(ra.trim()) * 1000L); } catch (NumberFormatException ignored) { }
                }
                System.out.println("  HTTP " + code + ", waiting " + (wait / 1000) + "s  " + url);
                Thread.sleep(wait);
                continue;
            }
            System.out.println("  HTTP " + code + " for " + url);
            return null;
        }
        return null;
    }

    /* ---------- helpers ---------- */

    private static void appendLine(File f, String line) throws IOException {
        Files.write(f.toPath(), (line + "\n").getBytes(StandardCharsets.UTF_8),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
    }

    private static String capitalise(String slug) {
        if (slug.equals("peps")) return "PEPs";
        if (slug.equals("core-dev")) return "Core-Dev";
        return Character.toUpperCase(slug.charAt(0)) + slug.substring(1);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        return o instanceof Map ? (Map<String, Object>) o : null;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object o) {
        return o instanceof List ? (List<Object>) o : new ArrayList<Object>();
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private static int num(Object o) {
        return o instanceof Number ? ((Number) o).intValue() : 0;
    }

    /* ---------- minimal JSON ---------- */

    static class Json {
        private final String s;
        private int i;

        private Json(String s) {
            this.s = s;
        }

        static Object parse(String s) {
            Json j = new Json(s);
            j.ws();
            Object v = j.value();
            return v;
        }

        private Object value() {
            ws();
            char c = s.charAt(i);
            if (c == '{') return object();
            if (c == '[') return array();
            if (c == '"') return string();
            if (c == 't') { i += 4; return Boolean.TRUE; }
            if (c == 'f') { i += 5; return Boolean.FALSE; }
            if (c == 'n') { i += 4; return null; }
            return number();
        }

        private Map<String, Object> object() {
            Map<String, Object> m = new LinkedHashMap<String, Object>();
            i++; ws();
            if (s.charAt(i) == '}') { i++; return m; }
            while (true) {
                ws();
                String k = string();
                ws(); i++; // :
                Object v = value();
                m.put(k, v);
                ws();
                if (s.charAt(i) == ',') { i++; continue; }
                i++; // }
                return m;
            }
        }

        private List<Object> array() {
            List<Object> l = new ArrayList<Object>();
            i++; ws();
            if (s.charAt(i) == ']') { i++; return l; }
            while (true) {
                l.add(value());
                ws();
                if (s.charAt(i) == ',') { i++; continue; }
                i++; // ]
                return l;
            }
        }

        private String string() {
            StringBuilder b = new StringBuilder();
            i++; // opening quote
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') return b.toString();
                if (c == '\\') {
                    char e = s.charAt(i++);
                    switch (e) {
                        case 'n': b.append('\n'); break;
                        case 't': b.append('\t'); break;
                        case 'r': b.append('\r'); break;
                        case 'b': b.append('\b'); break;
                        case 'f': b.append('\f'); break;
                        case 'u': b.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                        default: b.append(e);
                    }
                } else {
                    b.append(c);
                }
            }
        }

        private Object number() {
            int start = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
            String n = s.substring(start, i);
            if (n.indexOf('.') >= 0 || n.indexOf('e') >= 0 || n.indexOf('E') >= 0) return Double.valueOf(n);
            return Long.valueOf(n);
        }

        private void ws() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }

        static String write(Object o) {
            StringBuilder b = new StringBuilder();
            write(o, b);
            return b.toString();
        }

        @SuppressWarnings("unchecked")
        private static void write(Object o, StringBuilder b) {
            if (o == null) { b.append("null"); return; }
            if (o instanceof String) { b.append('"'); escape((String) o, b); b.append('"'); return; }
            if (o instanceof Number || o instanceof Boolean) { b.append(o); return; }
            if (o instanceof Map) {
                b.append('{');
                boolean first = true;
                for (Map.Entry<String, Object> e : ((Map<String, Object>) o).entrySet()) {
                    if (!first) b.append(',');
                    first = false;
                    b.append('"'); escape(e.getKey(), b); b.append("\":");
                    write(e.getValue(), b);
                }
                b.append('}');
                return;
            }
            if (o instanceof List) {
                b.append('[');
                boolean first = true;
                for (Object e : (List<Object>) o) {
                    if (!first) b.append(',');
                    first = false;
                    write(e, b);
                }
                b.append(']');
            }
        }

        private static void escape(String s, StringBuilder b) {
            for (int k = 0; k < s.length(); k++) {
                char c = s.charAt(k);
                switch (c) {
                    case '"': b.append("\\\""); break;
                    case '\\': b.append("\\\\"); break;
                    case '\n': b.append("\\n"); break;
                    case '\r': b.append("\\r"); break;
                    case '\t': b.append("\\t"); break;
                    default:
                        if (c < 0x20) b.append(String.format("\\u%04x", (int) c)); else b.append(c);
                }
            }
        }
    }
}
