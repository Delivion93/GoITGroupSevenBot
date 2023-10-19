package com.goitgroupsevenbot.config;

import io.github.cdimascio.dotenv.Dotenv;

public class BotConstance {
    private static final Dotenv DOTENV = Dotenv.configure().directory(".env").load();
    public static final String BOT_NAME = DOTENV.get("TELEGRAM_BOT_NAME");
    public static final String BOT_TOKEN = DOTENV.get("TELEGRAM_BOT_TOKEN");
    public static final String URL_MONOBANK = "https://api.monobank.ua/bank/currency";
    public static final String URL_NABU = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    public static final String URL_PRIVAT = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";
    public static final String CONTENT_TYPE = "content-type";
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
}
