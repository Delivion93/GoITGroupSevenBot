package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.config.BotConstance;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return BotConstance.BOT_NAME;
    }
    @Override
    public String getBotToken() {
        return BotConstance.BOT_TOKEN;
    }
}
