package ru.bot.vacancy_bot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static ru.bot.vacancy_bot.HhService.SEEN_FILE;

@Component
public class VacancyScheduler {
    private final HhService hhService;
    private final TelegramService telegramService;

    public VacancyScheduler(HhService hhService, TelegramService telegramService) {
        this.hhService = hhService;
        this.telegramService = telegramService;
    }

    @Scheduled(fixedDelayString = "${bot.check-interval}")
    public void checkVacancies() {
        Set<String> seen = hhService.loadSeenIds(SEEN_FILE);
        System.out.println("HH Notifier. Известных вакансий: " + seen.size());

        try {
            List<Vacancy> vacancies = hhService.fetchVacancies();
            int sent = 0;
            for (Vacancy v : vacancies) {
                if (seen.contains(v.id())) continue;
                String msg = "🔔 Новая вакансия:" +
                        "\n" + v.name() +
                        "\n" + "Опубликовано: " + v.publishedAt() +
                        "\n" + v.url();
                telegramService.sendMessage(msg);
                seen.add(v.id());
                hhService.appendSeenId(v.id());
                sent++;
            }
            System.out.println("Отправлено новых вакансий: " + sent);
        } catch (Exception e) {
            System.err.println("Ошибка при проверке вакансий: " + e.getMessage());
        }
    }
}