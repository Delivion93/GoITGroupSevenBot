package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Banks {
    MOMOBANK("Монобанк","MOMOBANK"), PRIVAT_BANK("ПриватБанк","PRIVAT_BANK"), NABU("НАБУ","NABU");
    private final String name;
    private final String signature;
}
