package com.goitgroupsevenbot.service.Jobs;

import com.goitgroupsevenbot.service.TelegramBot;
import org.quartz.*;

public class NewsletterJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = jobExecutionContext.getScheduler().getContext();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        TelegramBot bot = (TelegramBot) schedulerContext.get("bot");
        bot.sendNotification();

    }

}
