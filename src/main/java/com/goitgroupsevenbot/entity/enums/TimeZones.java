package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
/**
 * Enum for all time zones.
 *
 * @author Abramov Artem
 * @version 1.0.0 28.10.2023
 */
@Getter
@RequiredArgsConstructor
public enum TimeZones {
    LONDON(DateTimeZone.forID("Europe/London"),"Лондон GMT+0","LONDON"),
    MADRID(DateTimeZone.forID("Europe/Madrid"),"Мадрид GMT+1","MADRID"),
    KYIV(DateTimeZone.forID("Europe/Kiev"),"Київ GMT+2","KYIV");
    private final DateTimeZone timeZone;
    private final String name;
    private final String signature;

}
