package com.goitgroupsevenbot.entity.domain;

import com.goitgroupsevenbot.entity.enums.*;
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
    private TimeZones timeZone;
    private int currentTime;

    public String currencyToString(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Currency, Currency> currencyCurrencyEntry : currencyTarget.entrySet()) {
            sb.append(currencyCurrencyEntry.getValue().getText()).append(" ");
        }
        return sb.toString();
    }
}
