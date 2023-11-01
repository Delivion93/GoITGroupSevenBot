package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for all banks.
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 21.10.2023
 */
@Getter
@RequiredArgsConstructor
public enum Banks {
    MOMOBANK("Монобанк", "MOMOBANK"), PRIVAT_BANK("ПриватБанк", "PRIVAT_BANK"), NABU("НАБУ", "NABU");
    private final String name;
    private final String signature;
}
