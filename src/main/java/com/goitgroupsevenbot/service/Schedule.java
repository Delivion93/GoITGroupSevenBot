package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.service.jobs.CurrencyUpdateJob;
import com.goitgroupsevenbot.service.jobs.NewsletterJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
/**
 * Class for starting all Quartz jobs.
 *
 * @author Abramov Artem
 * @version 1.0.0 28.10.2023
 */
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
                    .withIdentity("Every hour  from 9 to 18")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 8-19 * * ?"))
                    .build();
//
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
