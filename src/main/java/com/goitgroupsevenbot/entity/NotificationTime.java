package com.goitgroupsevenbot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTime {
    NINE(9,"9","NINE"),
    TEN(10,"10","TEN"),
    ELEVEN(11,"11","ELEVEN"),
    TWELVE(12,"12","TWELVE"),
    THIRTEEN(13,"13","THIRTEEN"),
    FOURTEEN(14,"14","FOURTEEN"),
    FIFTEEN(15,"15","FIFTEEN"),
    SIXTEEN(16,"16","SIXTEEN"),
    SEVENTEEN(17,"17","SEVENTEEN"),
    EIGHTEEN(18,"18","EIGHTEEN"),
    TURN_OF_NOTIFICATION(0,"Turn of notifications","TURN_OF_NOTIFICATION");
    private final int time;
    private final String text;
    private final String signature;
}
