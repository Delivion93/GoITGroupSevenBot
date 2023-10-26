package com.goitgroupsevenbot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(840,"USD"),
    EUR(978,"EUR");
//    PLZ(985),
//    CAD(124),
//    RUB(643),
//    UAH(980);
    private final int id;
    private final String text;

}
