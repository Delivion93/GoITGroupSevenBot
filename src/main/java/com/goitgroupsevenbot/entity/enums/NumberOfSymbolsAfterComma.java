package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NumberOfSymbolsAfterComma {
    TWO(2,"TWO","%.2f"),
    THREE(3,"THREE","%.3f"),
    FOUR(4,"FOUR","%.4f");
    private final int number;
    private final String signature;
    private final String expression;
}
