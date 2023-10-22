package com.goitgroupsevenbot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Banks {
    MOMOBANK("Monobank","MOMOBANK"), PRIVAT_BANK("Privat bank","PRIVAT_BANK"), NABU("NABU","NABU");
    private final String name;
    private final String signature;
}
