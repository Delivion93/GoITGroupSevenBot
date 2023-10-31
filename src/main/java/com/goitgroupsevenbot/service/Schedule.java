package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.service.jobs.CurrencyUpdateJob;
import com.goitgroupsevenbot.service.jobs.NewsletterJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Schedule {

    public void updateDada() {
        try {
            JobDetail jobCurrencyUpdate = JobBuilder.newJob(CurrencyUpdateJob.class)
                    .withIdentity("Currency List update")
                    .build();
            Trigger updateTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("every5min")
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever())
                    .build();
            org.quartz.Scheduler listUpdateScheduler = new StdSchedulerFactory().getScheduler();
            listUpdateScheduler.start();
            listUpdateScheduler.scheduleJob(jobCurrencyUpdate, updateTrigger);
        } catch (SchedulerException e){
            e.printStackTrace();
        }
    }
    public void notification(TelegramBot telegramBot){
        try {
            JobDetail jobSendNotification = JobBuilder.newJob(NewsletterJob.class)
                    .withIdentity("sendNewsletter")
                    .build();
            Trigger notificationTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyHourFrom9to18")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("0 0 8-17 * * ?"))
                    .build();

            Scheduler notificationScheduler = new StdSchedulerFactory().getScheduler();
            notificationScheduler.getContext().put("bot", telegramBot);
            notificationScheduler.start();
            notificationScheduler.scheduleJob(jobSendNotification, notificationTrigger);
        }
        catch (SchedulerException e){
            e.printStackTrace();
        }
    }
}
