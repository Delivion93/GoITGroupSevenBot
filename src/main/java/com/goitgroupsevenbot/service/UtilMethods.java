package com.goitgroupsevenbot.service;

import com.goitgroupsevenbot.entity.enums.*;
import com.goitgroupsevenbot.entity.enums.Currency;
import com.goitgroupsevenbot.repository.UserRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class UtilMethods {
    private UserRepository userRepository;
    public UtilMethods(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Util method to build keyboard for symbol command.
     *
     * @param chatId Chat id.
     */
    public InlineKeyboardMarkup symbolsButtons(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        for (NumberOfSymbolsAfterComma symbols : NumberOfSymbolsAfterComma.values()) {
            buttonsRowOne.add(InlineKeyboardButton.builder()
                    .text(getButton(userRepository
                            .getById(chatId).getSymbols(), symbols))
                    .callbackData("SYMBOL:" + symbols.getSignature()).build());
        }
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:symbol").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:symbol").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to build keyboard for notification command.
     *
     * @param chatId Chat id.
     */
    public InlineKeyboardMarkup notificationButtons(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTimeZone = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowThree = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFour = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowFive = new ArrayList<>();
        for (TimeZones zones : TimeZones.values()) {
            buttonsRowTimeZone.add(InlineKeyboardButton.builder().text(getButton(userRepository.getById(chatId).getTimeZone(), zones)).callbackData("NOTIFICATION_TIME_ZONE:" + zones.getSignature()).build());
        }
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
        rows.add(buttonsRowTimeZone);
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        rows.add(buttonsRowThree);
        rows.add(buttonsRowFour);
        rows.add(buttonsRowFive);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to build keyboard for start command.
     */
    public InlineKeyboardMarkup startButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Налаштування").callbackData("START:settings").build());
        buttons.add(InlineKeyboardButton.builder().text("Отримати курс").callbackData("START:info").build());
        rows.add(buttons);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to build keyboard for settings command.
     */
    public InlineKeyboardMarkup settingsButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowOne = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTwo = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRowTree = new ArrayList<>();
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Кількість знаків після коми").callbackData("SETTINGS:symbols").build());
        buttonsRowOne.add(InlineKeyboardButton.builder().text("Банк").callbackData("SETTINGS:bank").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Валюти").callbackData("SETTINGS:currency").build());
        buttonsRowTwo.add(InlineKeyboardButton.builder().text("Час оповіщення").callbackData("SETTINGS:notification").build());
        buttonsRowTree.add(InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:settings").build());
        rows.add(buttonsRowOne);
        rows.add(buttonsRowTwo);
        rows.add(buttonsRowTree);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to build keyboard for banks command.
     *
     * @param chatId Chat id.
     */
    public InlineKeyboardMarkup banksButtons(Long chatId) {
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
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to build keyboard for currency command.
     *
     * @param chatId Chat id.
     */
    public InlineKeyboardMarkup currencyButtons(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            rows.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(getButton(userRepository.getById(chatId).getCurrencyTarget(), currency))
                            .callbackData("CURRENCY_TARGET:" + currency.name()).build()));
        }
        rows.add(Arrays.asList(InlineKeyboardButton.builder().text("Назад").callbackData("BACK:currency").build(),
                InlineKeyboardButton.builder().text("Далі").callbackData("NEXT:currency").build()));
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
    /**
     * Util method to build keyboard for getInfo command when currency is empty.
     *
     */
    public InlineKeyboardMarkup getInfoButtons(){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Налаштування").callbackData("START:settings").build());
        buttons.add(InlineKeyboardButton.builder().text("Отримати курс").callbackData("NEXT:notification").build());
        rows.add(buttons);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
    /**
     * Util method to build keyboard for getInfo command when currency is empty.
     *
     */
    public InlineKeyboardMarkup getInfoEmptyCurrencyButtons(){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Валюта").callbackData("NEXT:banks").build());
        rows.add(buttons);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    /**
     * Util method to mark up a pressed button in the "notification" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    public String getButton(TimeZones saved, TimeZones current) {
        return saved == current ? current.getName() + "\uD83D\uDFE2" : current.getName();
    }
    /**
     * Util method to mark up a pressed button in the "notification" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    public String getButton(NotificationTime saved, NotificationTime current) {
        return saved == current ? current.getText() + "\uD83D\uDFE2" : current.getText();
    }

    /**
     * Util method to mark up a pressed button in the "currency" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    public String getButton(Map<Currency, Currency> saved, Currency current) {

        return saved.containsKey(current) ? current.name() + "\uD83D\uDFE2" : current.name();
    }

    /**
     * Util method to mark up a pressed button in the "banks" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    public String getButton(Banks saved, Banks current) {
        return saved == current ? current.getName() + "\uD83D\uDFE2" : current.getName();
    }

    /**
     * Util method to mark up a pressed button in the "number of symbols after comma" keyboard.
     *
     * @param saved   Chat's ID long.
     * @param current Text to send.
     */
    public String getButton(NumberOfSymbolsAfterComma saved, NumberOfSymbolsAfterComma current) {
        return saved == current ? current.getNumber() + "\uD83D\uDFE2" : String.valueOf(current.getNumber());
    }
}
