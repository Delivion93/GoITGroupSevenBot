package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.config.BotConstance;
import com.goitgroupsevenbot.entity.enums.Currency;
import com.goitgroupsevenbot.entity.domain.User;
import com.goitgroupsevenbot.entity.domain.CurrencyBankItem;
import com.goitgroupsevenbot.entity.enums.Banks;
import com.goitgroupsevenbot.entity.enums.NotificationTime;
import com.goitgroupsevenbot.entity.enums.NumberOfSymbolsAfterComma;
import com.goitgroupsevenbot.repository.CurrencyBankRepository;
import com.goitgroupsevenbot.repository.UserRepository;
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

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private UserRepository userRepository;
    @SneakyThrows
    public TelegramBot() {
        userRepository = new UserRepository();
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("start", "Головне меню"));
        listOfCommands.add(new BotCommand("info", "Отримати інформацію про курс валют"));
        listOfCommands.add(new BotCommand("settings", "Налаштування запиту інформації"));
        listOfCommands.add(new BotCommand("symbols", "Налаштування - Кількість символів після коми"));
        listOfCommands.add(new BotCommand("banks", "Налаштування - Банк"));
        listOfCommands.add(new BotCommand("currency", "Налаштування - Валюта"));
        listOfCommands.add(new BotCommand("notification", "Налаштування - Оповіщення"));
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
        switch (action) {
            case "START" -> {
                switch (param[1]) {
                    case "settings" -> settingsCommandReceived(message.getChatId());
                    case "info" -> getInfo(message.getChatId());
                }
            }
            case "NEXT" -> {
                switch (param[1]) {
                    case "symbol" -> banksCommandReceived(message.getChatId());
                    case "settings" -> symbolsCommandReceived(message.getChatId());
                    case "banks" -> currencyCommandReceived(message.getChatId());
                    case "currency" -> notificationCommandReceived(message.getChatId());
                    case "notification" -> getInfo(message.getChatId());
                }
            }
            case "BACK" -> {
                switch (param[1]) {
                    case "symbol" -> settingsCommandReceived(message.getChatId());
                    case "banks" -> symbolsCommandReceived(message.getChatId());
                    case "currency" -> banksCommandReceived(message.getChatId());
                    case "notification" -> currencyCommandReceived(message.getChatId());
                }
            }
            case "SETTINGS" -> {
                switch (param[1]) {
                    case "symbols" -> symbolsCommandReceived(message.getChatId());
                    case "bank" -> banksCommandReceived(message.getChatId());
                    case "currency" -> currencyCommandReceived(message.getChatId());
                    case "notification" -> notificationCommandReceived(message.getChatId());
                }
            }
            case "SYMBOL" -> symbolCallBackReceived(param[1], message);
            case "BANKS" -> banksCallBackReceived(param[1], message);
            case "CURRENCY_TARGET" -> currencyCallBackReceived(param[1], message);
            case "NOTIFICATION" -> notificationCallBackReceived(param[1], message);
        }
    }

    private void handelMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            //Looking for command.
            Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                //If command is present extract it.
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                //Response on command if it exists.
                switch (command) {
                    case "/start" -> startCommandReceived(message);
                    case "/info" -> getInfo(message.getChatId());
                    case "/settings" -> settingsCommandReceived(message.getChatId());
                    case "/symbols" -> symbolsCommandReceived(message.getChatId());
                    case "/banks" -> banksCommandReceived(message.getChatId());
                    case "/currency" -> currencyCommandReceived(message.getChatId());
                    case "/notification" -> notificationCommandReceived(message.getChatId());
                }
            }
        }
    }

    /**
     * Method for responding to the SYMBOL call back.
     *
     * @param param   Call back data.
     * @param message Message from user.
     */
    private void symbolCallBackReceived(String param, Message message) {
        NumberOfSymbolsAfterComma symbol = NumberOfSymbolsAfterComma.valueOf(param);
        userRepository.getById(message.getChatId()).setSymbols(symbol);
        String answer = "Оберіть кількість знаків після коми";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (NumberOfSymbolsAfterComma symbols : NumberOfSymbolsAfterComma.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getSymbols(), symbols)).callbackData("SYMBOL:" + symbols.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:symbol").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:symbol").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
    }

    /**
     * Method for responding to the BANKS call back.
     *
     * @param param   Call back data.
     * @param message Message from user.
     */
    private void banksCallBackReceived(String param, Message message) {
        Banks bank = Banks.valueOf(param);
        userRepository.getById(message.getChatId()).setBank(bank);
        String answer = "Оберіть банк для запиту курсу валют";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (Banks banks : Banks.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getBank(), banks)).callbackData("BANKS:" + banks.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:banks").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:banks").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
    }

    /**
     * Method for responding to the CURRENCY call back.
     *
     * @param param   Call back data.
     * @param message Message from user.
     */
    private void currencyCallBackReceived(String param, Message message) {
        Currency currencyTarget = Currency.valueOf(param);
        if (userRepository.getById(message.getChatId()).getCurrencyTarget().containsKey(currencyTarget)) {
            userRepository.getById(message.getChatId()).getCurrencyTarget().remove(currencyTarget);
            String answer = "Оберіть валюти";
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (Currency currency : Currency.values()) {
                rows.add(Collections.singletonList(
                        InlineKeyboardButton.builder()
                                .text(getButton(userRepository.getById(message.getChatId()).getCurrencyTarget(), currency))
                                .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
            }
            rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:currency").build(),
                    InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:currency").build()));
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
        } else {
            userRepository.getById(message.getChatId()).getCurrencyTarget().put(currencyTarget, currencyTarget);
            String answer = "Оберіть валюти";
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (Currency currency : Currency.values()) {
                rows.add(Collections.singletonList(
                        InlineKeyboardButton.builder()
                                .text(getButton(userRepository.getById(message.getChatId()).getCurrencyTarget(), currency))
                                .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
            }
            rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:currency").build(),
                    InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:currency").build()));
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
        }
    }

    /**
     * Method for responding to the SYMBOL call back.
     *
     * @param param   Call back data.
     * @param message Message from user.
     */
    private void notificationCallBackReceived(String param, Message message) {
        NotificationTime notification = NotificationTime.valueOf(param);
        userRepository.getById(message.getChatId()).setNotificationTime(notification);
        String answer = "Оберіть час оповіщення";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowThree = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFour = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFive = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.NINE)).callbackData("NOTIFICATION:" + NotificationTime.NINE.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.TEN)).callbackData("NOTIFICATION:" + NotificationTime.TEN.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.ELEVEN)).callbackData("NOTIFICATION:" + NotificationTime.ELEVEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.TWELVE)).callbackData("NOTIFICATION:" + NotificationTime.TWELVE.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.THIRTEEN)).callbackData("NOTIFICATION:" + NotificationTime.THIRTEEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.FOURTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FOURTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.FIFTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FIFTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.SIXTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SIXTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.SEVENTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SEVENTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.EIGHTEEN)).callbackData("NOTIFICATION:" + NotificationTime.EIGHTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(message.getChatId()).getNotificationTime(), NotificationTime.TURN_OF_NOTIFICATION)).callbackData("NOTIFICATION:" + NotificationTime.TURN_OF_NOTIFICATION.getSignature()).build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:notification").build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Отримати курс").callbackData("NEXT:notification").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        rows.add(buttonsRowThree);
        rows.add(buttonsRowFour);
        rows.add(buttonsRowFive);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
    }

    /**
     * Method for responding to the /notification command.
     *
     * @param chatId Chat's ID long.
     */
    private void notificationCommandReceived(Long chatId) {
        String answer = "Оберіть час оповіщення";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowThree = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFour = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFive = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.NINE)).callbackData("NOTIFICATION:" + NotificationTime.NINE.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.TEN)).callbackData("NOTIFICATION:" + NotificationTime.TEN.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.ELEVEN)).callbackData("NOTIFICATION:" + NotificationTime.ELEVEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.TWELVE)).callbackData("NOTIFICATION:" + NotificationTime.TWELVE.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.THIRTEEN)).callbackData("NOTIFICATION:" + NotificationTime.THIRTEEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.FOURTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FOURTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.FIFTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FIFTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.SIXTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SIXTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.SEVENTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SEVENTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.EIGHTEEN)).callbackData("NOTIFICATION:" + NotificationTime.EIGHTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getNotificationTime(), NotificationTime.TURN_OF_NOTIFICATION)).callbackData("NOTIFICATION:" + NotificationTime.TURN_OF_NOTIFICATION.getSignature()).build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:notification").build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Отримати курс").callbackData("NEXT:notification").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        rows.add(buttonsRowThree);
        rows.add(buttonsRowFour);
        rows.add(buttonsRowFive);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the /banks command.
     *
     * @param chatId Chat's ID long.
     */
    private void currencyCommandReceived(Long chatId) {
        String answer = "Оберіть валюти";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            rows.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(getButton(userRepository.getById(chatId).getCurrencyTarget(), currency))
                            .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
        }
        rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:currency").build(),
                InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:currency").build()));
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the /banks command.
     *
     * @param chatId Chat's ID long.
     */
    private void banksCommandReceived(Long chatId) {
        String answer = "Оберіть банк для запиту курсу валют";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (Banks banks : Banks.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getBank(), banks)).callbackData("BANKS:" + banks.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:banks").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:banks").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the /symbols command.
     *
     * @param chatId Chat's ID long.
     */
    private void symbolsCommandReceived(long chatId) {
        String answer = "Оберіть кількість знаків після коми";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (NumberOfSymbolsAfterComma symbols : NumberOfSymbolsAfterComma.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getSymbols(), symbols)).callbackData("SYMBOL:" + symbols.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:symbol").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:symbol").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the /settings command.
     *
     * @param chatId Chat's ID long.
     */
    private void settingsCommandReceived(long chatId) {
        String answer = "Оберіть налаштування";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTree = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Кількість символів після коми").callbackData("SETTINGS:symbols").build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Банк").callbackData("SETTINGS:bank").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Валюти").callbackData("SETTINGS:currency").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Час оповіщення").callbackData("SETTINGS:notification").build());
        buttonsRowTree.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:settings").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        rows.add(buttonsRowTree);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Util method to register a new user.
     *
     * @param message Message form user.
     */
    private void registerUser(Message message) {
        if (!isUserRegistered(message.getChatId())) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();
            User user = User.builder().chatId(chatId).firstName(chat.getFirstName()).lastName(chat.getLastName()).userName(chat.getUserName())
                    .symbols(NumberOfSymbolsAfterComma.TWO)
                    .bank(Banks.NABU)
                    .currencyTarget(new HashMap<>(Map.of(Currency.USD, Currency.USD)))
                    .notificationTime(NotificationTime.NINE)
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            userRepository.addUser(message.getChatId(),user);
            System.out.println("userRepository.getAll() = " + userRepository.getAll());
        }
    }

    /**
     * Method for responding to the /start command.
     *
     * @param message Message fom user.
     */
    private void startCommandReceived(Message message) {
        registerUser(message);
        User user = userRepository.getById(message.getChatId());

        String answer ="Цей бот відображає курси валют\nНалаштування :\nБанк -"
                +user.getBank().getName()
                +"\nВалюта :"+user.currencyToString()
                +"\nКількість знаків після коми :"+user.getSymbols().getNumber()
                +"\nЧас оповіщення :"+user.getNotificationTime().getText();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Settings").callbackData("START:settings").build());
        buttons.add(InlineKeyboardButton.builder().text("Info").callbackData("START:info").build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(message.getChatId(), answer, markup);
    }

    /**
     * Util method to send edit message with inline keyboard.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     * @param markup Markup with list of buttons.
     */
    private void sendEditMessageWithInlineKeyboard(long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText message = EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).replyMarkup(markup).build();
        executeEditMessage(message);
    }

    /**
     * Util method to send edit message.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     */
    private void sendEditMessage(long chatId, int messageId, String text) {
        EditMessageText message = EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).build();
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
        SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
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
     * @param chatId Chat id.
     */
    private boolean isUserRegistered(long chatId) {
        return userRepository.getAll().containsKey(chatId);
    }

    /**
     * Util method to send message with inline keyboard.
     *
     * @param chatId Chat's ID long.
     * @param text   Text to send.
     * @param markup Markup with list of buttons.
     */
    private void sendMessageWithInlineKeyboard(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = SendMessage.builder().chatId(chatId).text(text).replyMarkup(markup).build();
        executeMessage(message);
    }

    /**
     * Util method to mark up a pressed button in the "notification" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getButton(NotificationTime saved, NotificationTime current) {
        return saved == current ? current.getText() + "\uD83D\uDFE2" : current.getText();
    }

    /**
     * Util method to mark up a pressed button in the "currency" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getButton(Map<Currency, Currency> saved, Currency current) {

        return saved.containsKey(current) ? current.name() + "\uD83D\uDFE2" : current.name();
    }

    /**
     * Util method to mark up a pressed button in the "banks" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getButton(Banks saved, Banks current) {
        return saved == current ? current.getName() + "\uD83D\uDFE2" : current.getName();
    }

    /**
     * Util method to mark up a pressed button in the "number of symbols after comma" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getButton(NumberOfSymbolsAfterComma saved, NumberOfSymbolsAfterComma current) {
        return saved == current ? current.getNumber() + "\uD83D\uDFE2" : String.valueOf(current.getNumber());
    }

    public void sendNotification() {
        int currentHour = LocalTime.now().getHour()+BotConstance.UKRAINE_TIME_DIFFERENCE; //TODO: Часові зони
        userRepository.getAll().values().stream()
                .filter(it -> it.getNotificationTime().getTime() == currentHour)
                .forEach(it -> getInfo(it.getChatId()));
    }

    public void getInfo(Long chatId) {
        User user = userRepository.getById(chatId);
        if(user.getCurrencyTarget().size()==0){
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(InlineKeyboardButton.builder().text("Валюта").callbackData("NEXT:banks").build());
            rows.add(buttons);
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendMessageWithInlineKeyboard(user.getChatId(), "У вас не обрано жодної валюти. Оберіть валюту", markup);
        }
        else{
            StringBuilder sb = new StringBuilder();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(InlineKeyboardButton.builder().text("Налаштування").callbackData("START:settings").build());
            buttons.add(InlineKeyboardButton.builder().text("Отримати курс").callbackData("NEXT:notification").build());
            rows.add(buttons);
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

            List<CurrencyBankItem> listBanks = CurrencyBankRepository.getList().stream()
                    .filter(it -> it.getBanks().equals(user.getBank()))
                    .filter(it -> it.getCurrency().equals(user.getCurrencyTarget().get(it.getCurrency())))
                    .toList();
            for (CurrencyBankItem listBank : listBanks) {
                sb.append("Курс ")
                        .append(listBank.getBanks().getName())
                        .append(" UAH/")
                        .append(listBank.getCurrency().getText())
                        .append("\nКупівля: ")
                        .append(String.format(user.getSymbols().getExpression(), listBank.getRateBuy()))
                        .append("\nПродаж: ")
                        .append(String.format(user.getSymbols().getExpression(), listBank.getRateSell()))
                        .append("\n");
            }
            sendMessageWithInlineKeyboard(user.getChatId(), sb.toString(), markup);
        }
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
