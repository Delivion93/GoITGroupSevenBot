package com.goitgroupsevenbot.entity.domain;

import com.goitgroupsevenbot.entity.enums.Banks;
import com.goitgroupsevenbot.entity.enums.Currency;
import com.goitgroupsevenbot.entity.enums.NotificationTime;
import com.goitgroupsevenbot.entity.enums.NumberOfSymbolsAfterComma;
import lombok.*;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Object that holds all user's info.
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 19.10.2023
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;
    private NumberOfSymbolsAfterComma symbols;
    private Banks bank;
    private Map<Currency,Currency> currencyTarget;
    private NotificationTime notificationTime;
    //TODO: Add fields to store user choose (Number of decimal, Bank, Currency, Newsletter).
}
