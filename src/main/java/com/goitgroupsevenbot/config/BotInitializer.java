package com.goitgroupsevenbot.config;

import com.goitgroupsevenbot.service.Jobs.CurrencyUpdateJob;
import com.goitgroupsevenbot.service.Jobs.NewsletterJob;
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
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


        /* Schedule tasks not related to updates via Quartz */
        try {

            /* Instantiate the job that will call the bot function */
            JobDetail jobSendNotification = JobBuilder.newJob(NewsletterJob.class)
                    .withIdentity("sendNewsletter")
                    .build();

            JobDetail jobCurrencyUpdate = JobBuilder.newJob(CurrencyUpdateJob.class)
                    .withIdentity("Currency List update")
                    .build();

            /* Define a notificationTrigger for the call */
            Trigger notificationTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyHourFrom9to18")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 9-17 * * ?"))
                    .build();


            Trigger updateTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("every5min")
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(4).repeatForever())
                    .build();

            /* Create a notificationScheduler to manage triggers */
            Scheduler notificationScheduler = new StdSchedulerFactory().getScheduler();
            notificationScheduler.getContext().put("bot", telegramBot);
            notificationScheduler.start();
            notificationScheduler.scheduleJob(jobSendNotification, notificationTrigger);
            Scheduler listUpdateScheduler = new StdSchedulerFactory().getScheduler();
            listUpdateScheduler.start();
            listUpdateScheduler.scheduleJob(jobCurrencyUpdate,updateTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
