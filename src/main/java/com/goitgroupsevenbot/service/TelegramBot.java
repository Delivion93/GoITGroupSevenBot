package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.config.BotConstance;
import com.goitgroupsevenbot.entity.User;
import com.goitgroupsevenbot.repository.UserList;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelegramBot extends TelegramLongPollingBot {
    @SneakyThrows
    public TelegramBot() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("start", "Start bot."));
        listOfCommands.add(new BotCommand("register", "Register user."));
        this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) { //If we have update call back (button pressed).
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) { //If we have update message.
            handelMessage(update.getMessage());
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        //We know in what format we gat data (cos we set up it when create the buttons), so we extract data.
        System.out.println("callbackQuery.getData() = " + callbackQuery.getData());
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        System.out.println("action = " + action);
        if (action.equals("REGISTRATION")) {
            if (isUserRegistered(message)) {
                nextButtonPressed(message.getChatId(), message.getMessageId(),"registration");
            } else {
                registerCommandReceived(message.getChatId());
            }
        } else if (action.equals("REGISTER")) {
            if (isUserRegistered(message)) {
                sendMessage(message.getChatId(), "You have been registered.");
            } else {
                if (param[1].equals("yes")) {
                    registerUser(message);
                    nextButtonPressed(message.getChatId(), message.getMessageId(),"registration");
                } else if (param[1].equals("no")) {
                    answerToUnregisteredUser(message.getChatId());
                }
            }
        }
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
                        startCommandReceived(message.getChatId());
                        break;
                    case "/register":
                        registerCommandReceived(message.getChatId());
                }
            }
        }
    }
    /**
     * Method for responding to the registered user when he finishes the registration.
     *
     * @param chatId Chat's ID long.
     */
    private void nextButtonPressed(Long chatId, int messageId, String whereItWasPressed) {
        String answer = "You have been registered.";
        System.out.println(answer);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Next")
                .callbackData("NEXT:"+whereItWasPressed)
                .build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        sendEditMessageWithInlineKeyboard(chatId, messageId,answer, markup);
    }

    /**
     * Method for responding to the /register command.
     *
     * @param chatId Chat's ID long.
     */
    private void registerCommandReceived(Long chatId) {
        String answer = "Do you really want to register?";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Yes")
                .callbackData("REGISTER:yes")
                .build());
        buttons.add(InlineKeyboardButton.builder()
                .text("No")
                .callbackData("REGISTER:no")
                .build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the unregistered user.
     *
     * @param chatId Chat's ID long.
     */
    private void answerToUnregisteredUser(Long chatId) {
        String answer = "You are not registered yet, please register by clicking on the " +
                "\"Register\" button to continue working.";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Register")
                .callbackData("REGISTER:yes")
                .build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Util method to register a new user.
     *
     * @param message Message form user.
     */
    private void registerUser(Message message) {
        if (!isUserRegistered(message)) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();
            User user = User.builder()
                    .chatId(chatId)
                    .firstName(chat.getFirstName())
                    .lastName(chat.getLastName())
                    .userName(chat.getUserName())
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            UserList.userList.put(chatId, user);
            UserList.userList.forEach((key, value) ->
                    System.out.println(key + ": " + value));
        }
    }

    /**
     * Method for responding to the /start command.
     *
     * @param chatId Chat's ID long.
     */
    private void startCommandReceived(long chatId) {
        String answer = "This bot displays exchange rates, " +
                "to take advantage of all the features please register " +
                "by clicking the \"Register\" button";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder()
                .text("Register")
                .callbackData("REGISTRATION:register")
                .build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Util method to send edit message with inline keyboard.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     * @param markup Markup with list of buttons.
     */
    private void sendEditMessageWithInlineKeyboard(long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        System.out.println("text = " + text);
        EditMessageText message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(markup)
                .build();
        executeEditMessage(message);
    }

    /**
     * Util method to send edit message.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     */
    private void sendEditMessage(long chatId,int messageId, String text) {
        EditMessageText message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .build();
        executeEditMessage(message);
    }

    /**
     * Util method to execute sending edit message.
     *
     * @param message object of SendMessage.
     */
    @SneakyThrows
    private void executeEditMessage(EditMessageText message) {
        execute(message);
    }

    /**
     * Util method to send message.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     */
    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        executeMessage(message);
    }

    /**
     * Util method to send message with inline keyboard.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     * @param markup Markup with list of buttons.
     */
    private void sendMessageWithInlineKeyboard(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        executeMessage(message);
    }

    /**
     * Util method to execute sending message.
     *
     * @param message object of SendMessage.
     */
    @SneakyThrows
    private void executeMessage(SendMessage message) {
        execute(message);
    }

    /**
     * Util method to check if user registered.
     *
     * @param message Message from user.
     */
    private boolean isUserRegistered(Message message) {
        return UserList.userList.containsKey(message.getChatId());
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
