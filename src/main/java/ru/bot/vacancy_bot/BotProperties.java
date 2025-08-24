package ru.bot.vacancy_bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

    // 🔎 Ключевое слово для поиска вакансий (например, "Java", "Spring Boot")
    private String query;

    // 📍 Регион (ID hh.ru: 1 = Москва, 2 = Санкт-Петербург, 113 = Россия)
    private List<String> area;

    // ⏳ За сколько дней искать вакансии (1 = последние сутки, 7 = неделя)
    private int periodDays;

    // 📄 Сколько вакансий брать за один запрос (максимум 100)
    private int perPage;

    // 🛰 User-Agent для запросов к API hh.ru (должен быть осмысленным)
    private String userAgent;

    // 🤖 Токен Telegram-бота от @BotFather
    private String token;

    // 💬 ID чата (юзер или группа), куда отправлять вакансии
    private String chatId;

    // ⏰ Период проверки вакансий в миллисекундах (300000 = 5 минут)
    private long checkInterval;

    // 🔎 Где искать (например, name = только в названии)
    private String searchField;
}