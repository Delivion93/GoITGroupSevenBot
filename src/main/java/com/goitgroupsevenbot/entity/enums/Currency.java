package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for all currency.
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 26.10.2023
 */
@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(840, "USD"),
    EUR(978, "EUR");
    private final int id;
    private final String text;

}
