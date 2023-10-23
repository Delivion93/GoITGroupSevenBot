package com.goitgroupsevenbot.service.impl;

import com.goitgroupsevenbot.config.BotConstance;
import com.goitgroupsevenbot.service.CurrencyService;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 23.10.2023
 */
public class PrivatBankCurrencyService implements CurrencyService {
    @Override
    public String getCurrenciesInfo() {
        String url = BotConstance.URL_PRIVAT;
        String json;
        try {
            json = Jsoup.connect(url)
                    .header(BotConstance.CONTENT_TYPE, BotConstance.APPLICATION_JSON_CHARSET_UTF_8)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Can't connect to Monobank API");
        }
        return json;
    }
}
