package ru.bot.vacancy_bot;

public record Vacancy(
        String id,
        String name,
        String url,
        String publishedAt
) {
}