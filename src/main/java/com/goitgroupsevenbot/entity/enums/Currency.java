package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(840,"USD"),
    EUR(978,"EUR");
    private final int id;
    private final String text;

}
