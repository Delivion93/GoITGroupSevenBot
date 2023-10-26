package com.goitgroupsevenbot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
public enum NumberOfSymbolsAfterComma {
    TWO(2,"TWO"),
    THREE(3,"THREE"),
    FOUR(4,"FOUR");
    private final int number;
    private final String signature;
}
