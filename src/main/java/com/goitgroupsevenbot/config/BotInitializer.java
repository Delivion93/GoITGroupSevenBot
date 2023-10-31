package com.goitgroupsevenbot.config;

import com.goitgroupsevenbot.service.Schedule;
import com.goitgroupsevenbot.service.jobs.CurrencyUpdateJob;
import com.goitgroupsevenbot.service.jobs.NewsletterJob;
import com.goitgroupsevenbot.service.TelegramBot;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotInitializer {
    TelegramBot telegramBot;
    public BotInitializer() {
        telegramBot = new TelegramBot();
        Schedule schedule = new Schedule();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        schedule.updateDada();
        schedule.notification(telegramBot);
    }
}
