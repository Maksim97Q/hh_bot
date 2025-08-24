package ru.bot.vacancy_bot;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class TelegramService {
    private final String token;
    private final String chatId;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public TelegramService(BotProperties props) {
        this.token = props.getToken();
        this.chatId = props.getChatId();
    }

    public void sendMessage(String text) {
        try {
            String body = "chat_id=" + URLEncoder.encode(chatId, StandardCharsets.UTF_8) +
                    "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                throw new IllegalStateException("Telegram error: " + resp.statusCode() + " body=" + resp.body());
            }
        } catch (Exception e) {
            System.err.println("Ошибка отправки в Telegram: " + e.getMessage());
        }
    }
}