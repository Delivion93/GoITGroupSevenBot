package com.goitgroupsevenbot.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goitgroupsevenbot.entity.dto.MonobankCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.NabuCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.PrivatCurrencyItemDto;
import com.goitgroupsevenbot.service.CurrencyService;
import com.goitgroupsevenbot.service.impl.MonobankCurrencyService;
import com.goitgroupsevenbot.service.impl.NabuCurrencyService;
import com.goitgroupsevenbot.service.impl.PrivatBankCurrencyService;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 23.10.2023
 */
@Getter
public class CurrencyBankRepository {
    private static CopyOnWriteArrayList<MonobankCurrencyItemDto> currencyListMonobank;
    private static CopyOnWriteArrayList<NabuCurrencyItemDto> currencyListNabu;
    private static CopyOnWriteArrayList<PrivatCurrencyItemDto> currencyListPrivat;
    public static void setCurrencyList() {
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyService currencyServiceMonobank = new MonobankCurrencyService();
        CurrencyService currencyServiceNabu = new NabuCurrencyService();
        CurrencyService currencyServicePrivat = new PrivatBankCurrencyService();
        String jsonMonobank = currencyServiceMonobank.getCurrenciesInfo();
        String jsonNabu = currencyServiceNabu.getCurrenciesInfo();
        String jsonPrivat = currencyServicePrivat.getCurrenciesInfo();
        try {
            currencyListMonobank = objectMapper.readValue(jsonMonobank, new TypeReference<CopyOnWriteArrayList<MonobankCurrencyItemDto>>() {
            });
            currencyListNabu = objectMapper.readValue(jsonNabu, new TypeReference<CopyOnWriteArrayList<NabuCurrencyItemDto>>() {
            });
            currencyListPrivat = objectMapper.readValue(jsonPrivat, new TypeReference<CopyOnWriteArrayList<PrivatCurrencyItemDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
