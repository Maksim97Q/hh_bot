package ru.bot.vacancy_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VacancyBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(VacancyBotApplication.class, args);
    }
}
