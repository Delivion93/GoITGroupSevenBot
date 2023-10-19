package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.config.BotConstance;
import com.goitgroupsevenbot.entity.User;
import com.goitgroupsevenbot.repository.UserList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;
import java.util.Optional;

public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) { //If we have update call back (button pressed).
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) { //If we have update message.
            handelMessage(update.getMessage());
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {

    }

    private void handelMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            //Looking for command.
            Optional<MessageEntity> commandEntity = message.getEntities()
                    .stream()
                    .filter(e -> "bot_command".equals(e.getType()))
                    .findFirst();
            if (commandEntity.isPresent()) {
                //If command is present extract it.
                String command = message.getText()
                        .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                //Response on command if it exists.
                switch (command) {
                    case "/start":

                }
            }
        }
    }
    private void registerUser(Message message) {
        boolean isPresent = UserList.userList.stream()
                .map(User::getChatId)
                .anyMatch(it-> it == message.getChatId());

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
