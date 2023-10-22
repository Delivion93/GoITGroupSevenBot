package com.goitgroupsevenbot.entity;

import lombok.*;

import java.sql.Timestamp;

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
    private Currency currencyOriginal;
    private Currency currencyTarget;
    //TODO: Add fields to store user choose (Number of decimal, Bank, Currency, Newsletter).
}
