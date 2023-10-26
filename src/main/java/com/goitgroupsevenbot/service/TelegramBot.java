package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.config.BotConstance;
import com.goitgroupsevenbot.entity.*;
import com.goitgroupsevenbot.entity.Currency;
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
import java.time.LocalTime;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    @SneakyThrows
    public TelegramBot() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("start", "Start bot."));
        listOfCommands.add(new BotCommand("info", "Information about this bot."));
        listOfCommands.add(new BotCommand("settings", "Sett up all settings."));
        listOfCommands.add(new BotCommand("symbols", "Number of symbols after comma."));
        listOfCommands.add(new BotCommand("banks", "Choose a bank."));
        listOfCommands.add(new BotCommand("currency", "Choose a currency."));
        listOfCommands.add(new BotCommand("notification", "Choose a time for notification."));
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
        if (action.equals("START")) {
            if (param[1].equals("settings")) {
                settingsCommandReceived(message.getChatId());
            } else if (param[1].equals("info")) {
                infoCommandReceived(message.getChatId());
            }
        } else if (action.equals("NEXT")) {
            if (param[1].equals("symbol")) {
                banksCommandReceived(message.getChatId());
            } else if (param[1].equals("settings")) {
                symbolsCommandReceived(message.getChatId());
            } else if (param[1].equals("banks")) {
                currencyCommandReceived(message.getChatId());
            } else if (param[1].equals("currency")) {
                notificationCommandReceived(message.getChatId());
            } else if (param[1].equals("notification")) {
                //TODO: Add method which send to user answer with currency rate.
                sendMessage(message.getChatId(), "Hear should be answer with currency rate!!!");
            }

        } else if (action.equals("BACK")) {
            if (param[1].equals("symbol")) {
                settingsCommandReceived(message.getChatId());
            } else if (param[1].equals("banks")) {
                symbolsCommandReceived(message.getChatId());
            } else if (param[1].equals("currency")) {
                banksCommandReceived(message.getChatId());
            } else if (param[1].equals("notification")) {
                currencyCommandReceived(message.getChatId());
            }
        } else if (action.equals("SETTINGS")) {
            if (param[1].equals("symbols")) {
                symbolsCommandReceived(message.getChatId());
            } else if (param[1].equals("bank")) {
                banksCommandReceived(message.getChatId());
            } else if (param[1].equals("currency")) {
                currencyCommandReceived(message.getChatId());
            } else if (param[1].equals("notification")) {
                notificationCommandReceived(message.getChatId());
            }
        } else if (action.equals("SYMBOL")) {
            NumberOfSymbolsAfterComma symbol = NumberOfSymbolsAfterComma.valueOf(param[1]);
            UserList.userList.get(message.getChatId()).setSymbols(symbol);
            String answer = "Please choose the number of symbols after comma:";
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
            for (NumberOfSymbolsAfterComma symbols : NumberOfSymbolsAfterComma.values()) {
                buttonsRowOne.add(InlineKeyboardButton.builder().text(getSymbolButton(UserList.userList.get(message.getChatId()).getSymbols(), symbols)).callbackData("SYMBOL:" + symbols.getSignature()).build());
            }
            buttonsRowTwo.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:symbol").build());
            buttonsRowTwo.add(InlineKeyboardButton.builder().text("Next").callbackData("NEXT:symbol").build());
            rows.add(buttonsRowOne);
            rows.add(buttonsRowTwo);
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
        } else if (action.equals("BANKS")) {
            Banks bank = Banks.valueOf(param[1]);
            UserList.userList.get(message.getChatId()).setBank(bank);
            String answer = "Please choose the bank:";
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
            for (Banks banks : Banks.values()) {
                buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getBank(), banks)).callbackData("BANKS:" + banks.getSignature()).build());
            }
            buttonsRowTwo.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:banks").build());
            buttonsRowTwo.add(InlineKeyboardButton.builder().text("Next").callbackData("NEXT:banks").build());
            rows.add(buttonsRowOne);
            rows.add(buttonsRowTwo);
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
        } else if (action.equals("CURRENCY_TARGET")) {
            Currency currencyTarget = Currency.valueOf(param[1]);
            if (UserList.userList.get(message.getChatId()).getCurrencyTarget().containsKey(currencyTarget)) {
                UserList.userList.get(message.getChatId()).getCurrencyTarget().remove(currencyTarget);
                String answer = "Please choose the currency:";
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                for (Currency currency : Currency.values()) {
                    rows.add(Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(UserList.userList.get(message.getChatId()).getCurrencyTarget(), currency))
                                    .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
                }
                rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Back").callbackData("BACK:currency").build(),
                        InlineKeyboardButton.builder().text("Next").callbackData("NEXT:currency").build()));
                InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
                sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
            } else {
                UserList.userList.get(message.getChatId()).getCurrencyTarget().put(currencyTarget, currencyTarget);
                String answer = "Please choose the currency:";
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                for (Currency currency : Currency.values()) {
                    rows.add(Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(UserList.userList.get(message.getChatId()).getCurrencyTarget(), currency))
                                    .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
                }
                rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Back").callbackData("BACK:currency").build(),
                        InlineKeyboardButton.builder().text("Next").callbackData("NEXT:currency").build()));
                InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
                sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
            }
        } else if (action.equals("NOTIFICATION")) {
            NotificationTime notification = NotificationTime.valueOf(param[1]);
            UserList.userList.get(message.getChatId()).setNotificationTime(notification);
            String answer = "Please select notification time:";
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowThree = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowFour = new ArrayList<>();
            List<InlineKeyboardButton> buttonsRowFive = new ArrayList<>();
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.NINE)).callbackData("NOTIFICATION:" + NotificationTime.NINE.getSignature()).build());
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.TEN)).callbackData("NOTIFICATION:" + NotificationTime.TEN.getSignature()).build());
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.ELEVEN)).callbackData("NOTIFICATION:" + NotificationTime.ELEVEN.getSignature()).build());
            buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.TWELVE)).callbackData("NOTIFICATION:" + NotificationTime.TWELVE.getSignature()).build());
            buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.THIRTEEN)).callbackData("NOTIFICATION:" + NotificationTime.THIRTEEN.getSignature()).build());
            buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.FOURTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FOURTEEN.getSignature()).build());
            buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.FIFTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FIFTEEN.getSignature()).build());
            buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.SIXTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SIXTEEN.getSignature()).build());
            buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.SEVENTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SEVENTEEN.getSignature()).build());
            buttonsRowFour.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.EIGHTEEN)).callbackData("NOTIFICATION:" + NotificationTime.EIGHTEEN.getSignature()).build());
            buttonsRowFour.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(message.getChatId()).getNotificationTime(), NotificationTime.TURN_OF_NOTIFICATION)).callbackData("NOTIFICATION:" + NotificationTime.TURN_OF_NOTIFICATION.getSignature()).build());
            buttonsRowFive.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:notification").build());
            buttonsRowFive.add(InlineKeyboardButton.builder().text("Get rate").callbackData("NEXT:notification").build());
            rows.add(buttonsRowOne);
            rows.add(buttonsRowTwo);
            rows.add(buttonsRowThree);
            rows.add(buttonsRowFour);
            rows.add(buttonsRowFive);
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
            sendEditMessageWithInlineKeyboard(message.getChatId(), message.getMessageId(), answer, markup);
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
                    case "/start":
                        startCommandReceived(message);
                        break;
                    case "/info":
                        infoCommandReceived(message.getChatId());
                        break;
                    case "/settings":
                        settingsCommandReceived(message.getChatId());
                        break;
                    case "/symbols":
                        symbolsCommandReceived(message.getChatId());
                        break;
                    case "/banks":
                        banksCommandReceived(message.getChatId());
                        break;
                    case "/currency":
                        currencyCommandReceived(message.getChatId());
                        break;
                    case "/notification":
                        notificationCommandReceived(message.getChatId());
                        break;
                }
            }
        }
    }

    /**
     * Method for responding to the /notification command.
     *
     * @param chatId Chat's ID long.
     */
    private void notificationCommandReceived(Long chatId) {
        String answer = "Please select notification time:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowThree = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFour = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFive = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.NINE)).callbackData("NOTIFICATION:" + NotificationTime.NINE.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.TEN)).callbackData("NOTIFICATION:" + NotificationTime.TEN.getSignature()).build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.ELEVEN)).callbackData("NOTIFICATION:" + NotificationTime.ELEVEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.TWELVE)).callbackData("NOTIFICATION:" + NotificationTime.TWELVE.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.THIRTEEN)).callbackData("NOTIFICATION:" + NotificationTime.THIRTEEN.getSignature()).build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.FOURTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FOURTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.FIFTEEN)).callbackData("NOTIFICATION:" + NotificationTime.FIFTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.SIXTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SIXTEEN.getSignature()).build());
        buttonsRowThree.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.SEVENTEEN)).callbackData("NOTIFICATION:" + NotificationTime.SEVENTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.EIGHTEEN)).callbackData("NOTIFICATION:" + NotificationTime.EIGHTEEN.getSignature()).build());
        buttonsRowFour.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getNotificationTime(), NotificationTime.TURN_OF_NOTIFICATION)).callbackData("NOTIFICATION:" + NotificationTime.TURN_OF_NOTIFICATION.getSignature()).build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:notification").build());
        buttonsRowFive.add(InlineKeyboardButton.builder().text("Get rate").callbackData("NEXT:notification").build());
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
        String answer = "Please choose the currency:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            rows.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(UserList.userList.get(chatId).getCurrencyTarget(), currency))
                            .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
        }
        rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Back").callbackData("BACK:currency").build(),
                InlineKeyboardButton.builder().text("Next").callbackData("NEXT:currency").build()));
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(chatId, answer, markup);
    }

    /**
     * Method for responding to the /banks command.
     *
     * @param chatId Chat's ID long.
     */
    private void banksCommandReceived(Long chatId) {
        String answer = "Please choose the bank:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (Banks banks : Banks.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getBankButton(UserList.userList.get(chatId).getBank(), banks)).callbackData("BANKS:" + banks.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:banks").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Next").callbackData("NEXT:banks").build());
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
        String answer = "Please choose the number of symbols after comma:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (NumberOfSymbolsAfterComma symbols : NumberOfSymbolsAfterComma.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder().text(getSymbolButton(UserList.userList.get(chatId).getSymbols(), symbols)).callbackData("SYMBOL:" + symbols.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Back").callbackData("BACK:symbol").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Next").callbackData("NEXT:symbol").build());
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
        String answer = "Please choose the options:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTree = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Symbols after comma").callbackData("SETTINGS:symbols").build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Bank").callbackData("SETTINGS:bank").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Currency").callbackData("SETTINGS:currency").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Notification").callbackData("SETTINGS:notification").build());
        buttonsRowTree.add(InlineKeyboardButton.builder().text("Next").callbackData("NEXT:settings").build());
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
                    .currencyTarget(new HashMap<>())
                    .notificationTime(NotificationTime.TURN_OF_NOTIFICATION)
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            UserList.userList.put(chatId, user);
            UserList.userList.forEach((key, value) -> System.out.println(key + ": " + value));
        }
    }

    /**
     * Method for responding to the /start command.
     *
     * @param message Message fom user.
     */
    private void startCommandReceived(Message message) {
        registerUser(message);
        String answer = "This bot displays exchange rates";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Settings").callbackData("START:settings").build());
        buttons.add(InlineKeyboardButton.builder().text("Info").callbackData("START:info").build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
        sendMessageWithInlineKeyboard(message.getChatId(), answer, markup);
    }

    /**
     * Method for responding to the /start command.
     *
     * @param chatId Chat's ID long.
     */
    private void infoCommandReceived(long chatId) {
        String answer = "This bot displays exchange rates.\n " + "You can specify the number of symbols after comma, " +
                "select the bank from which you want to receive information, " +
                "select the currency, and also select the time to receive notifications, " +
                "by clicking on the \"Settings\" button:";
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Settings").callbackData("START:settings").build());
        rows.add(buttons);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();
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
        return UserList.userList.containsKey(chatId);
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
    private String getBankButton(NotificationTime saved, NotificationTime current) {
        return saved == current ? current.getText() + "\uD83D\uDDF8" : current.getText();
    }

    /**
     * Util method to mark up a pressed button in the "currency" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getCurrencyButton(Map<Currency, Currency> saved, Currency current) {

        return saved.containsKey(current) ? current.name() + "\uD83D\uDDF8" : current.name();
    }

    /**
     * Util method to mark up a pressed button in the "banks" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getBankButton(Banks saved, Banks current) {
        return saved == current ? current.getName() + "\uD83D\uDDF8" : current.getName();
    }

    /**
     * Util method to mark up a pressed button in the "number of symbols after comma" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    private String getSymbolButton(NumberOfSymbolsAfterComma saved, NumberOfSymbolsAfterComma current) {
        return saved == current ? current.getNumber() + "\uD83D\uDDF8" : current.getNumber() + "";
    }

    @Override
    public String getBotUsername() {
        return BotConstance.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConstance.BOT_TOKEN;
    }

    public void sendNotification() {
        int currentHour = LocalTime.now().getHour();

        UserList.userList.values().stream()
                .filter(it -> it.getNotificationTime().getTime() == 12)
                .forEach(it -> sendMessage(it.getChatId(), "message")); // TODO: Change method sendMessage() to getInfo()
    }
}
