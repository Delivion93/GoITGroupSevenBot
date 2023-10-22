package com.goitgroupsevenbot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(840),
    EUR(978),
    PLZ(985),
    CAD(124),
    RUB(643),
    UAH(980);
    private final int id;

}
