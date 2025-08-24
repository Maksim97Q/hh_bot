package ru.bot.vacancy_bot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HhService {
    private final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final Path SEEN_FILE = Path.of("seen_ids.txt");
    private final BotProperties props;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public HhService(BotProperties props) {
        this.props = props;
    }

    public List<Vacancy> fetchVacancies() throws Exception {
        List<Vacancy> result = new ArrayList<>();

        String area = props.getArea().stream()
                .map(a -> "&area=" + a)
                .collect(Collectors.joining());

        String url = "https://api.hh.ru/vacancies" +
                "?text=" + URLEncoder.encode(props.getQuery(), StandardCharsets.UTF_8) +
                "&search_field=" + props.getSearchField() +
                area +
                "&period=" + props.getPeriodDays() +
                "&per_page=" + props.getPerPage() +
                "&order_by=publication_time";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", props.getUserAgent())
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new IllegalStateException("HH API error: " + resp.statusCode() + " body=" + resp.body());
        }

        JSONObject json = new JSONObject(resp.body());
        JSONArray items = json.optJSONArray("items");
        if (items == null) return result;

        for (int i = 0; i < items.length(); i++) {
            JSONObject v = items.getJSONObject(i);
            String id = v.getString("id");
            String name = v.optString("name", "(без названия)");
            String link = v.optString("alternate_url", "");
            String publishedRaw  = v.optString("published_at", "");

            String published = "";
            if (!publishedRaw.isEmpty()) {
                LocalDateTime localDateTime = LocalDateTime.parse(publishedRaw, parser);
                published = localDateTime.format(formatter);
            }
            result.add(new Vacancy(id, name, link, published));
        }
        return result;
    }

    public Set<String> loadSeenIds(Path file) {
        Set<String> set = new HashSet<>();
        try {
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file)) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) set.add(trimmed);
                }
            } else {
                Files.createFile(file);
            }
        } catch (IOException e) {
            System.err.println("Не удалось прочитать/создать " + file + ": " + e.getMessage());
        }
        return set;
    }

    public void appendSeenId(String id) {
        try {
            Files.writeString(SEEN_FILE, id + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Не удалось записать в " + SEEN_FILE + ": " + e.getMessage());
        }
    }
}