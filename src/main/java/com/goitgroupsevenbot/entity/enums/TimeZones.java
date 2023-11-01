package com.goitgroupsevenbot.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
@Getter
@RequiredArgsConstructor
public enum TimeZones {
    LONDON(DateTimeZone.forID("Europe/London"),"Лондон GMT+0"),MADRID(DateTimeZone.forID("Europe/Madrid"),"Мадрид GMT+1"),KYIV(DateTimeZone.forID("Europe/Kiev"),"Київ GMT+2");
    private final DateTimeZone timeZone;
    private final String name;

}
