package com.goitgroupsevenbot.entity.mapper;

import com.goitgroupsevenbot.entity.enums.Banks;
import com.goitgroupsevenbot.entity.enums.Currency;
import com.goitgroupsevenbot.entity.domain.CurrencyBankItem;
import com.goitgroupsevenbot.entity.dto.MonobankCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.NabuCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.PrivatCurrencyItemDto;

import java.util.List;

public class CurrencyBankItemMapper {

    public List<CurrencyBankItem> privatDtoToDomain(List<PrivatCurrencyItemDto> currencyItemDtos) {
        return currencyItemDtos.stream()
                                .filter(it -> it.getCcy().equals(Currency.EUR.getText())
                                        || it.getCcy().equals(Currency.USD.getText()))
                                .map(it -> CurrencyBankItem.builder()
                                        .banks(Banks.PRIVAT_BANK)
                        .currency(Currency.valueOf(it.getCcy()))
                        .rateBuy(it.getBuy())
                        .rateSell(it.getSale())
                        .build())
                .toList();
    }
    public List<CurrencyBankItem> nabuDtoToDomain(List<NabuCurrencyItemDto> currencyItemDtos){
        return currencyItemDtos.stream()
                .filter(it -> it.getCc().equals(Currency.USD.getText()) || it.getCc().equals(Currency.EUR.getText()))
                .map(it -> CurrencyBankItem.builder()
                        .banks(Banks.NABU)
                        .currency(Currency.valueOf(it.getCc()))
                        .rateBuy(getCurrencyRateBuy(it.getRate()))
                        .rateSell(getCurrencyRateSell(it.getRate()))
                        .build())
                .toList();
    }
    public List<CurrencyBankItem> monobankDtoToDomain(List<MonobankCurrencyItemDto>currencyItemDtos){
        return currencyItemDtos.stream()
                .filter(it -> it.getCurrencyCodeB() == 980 &&(it.getCurrencyCodeA() == 840 || it.getCurrencyCodeA() == 978))
                .map(it -> CurrencyBankItem.builder()
                        .banks(Banks.MOMOBANK)
                        .currency(getCurrency(it.getCurrencyCodeA()))
                        .rateBuy(it.getRateBuy())
                        .rateSell(it.getRateSell())
                        .build())
                .toList();
    }

    /**
     * Util method to return enum Currency based on currency id.
     *
     * @param currencyCode Currency code based on ISO table.
     * @return Currency enum.
     */
    private static Currency getCurrency(int currencyCode){
        if (currencyCode == 840){
            return Currency.USD;
        }
        return Currency.EUR;
    }
    /**
     * Util method to return rate buy based on rate cross.
     *
     * @param currencyRate Currency rate cross.
     * @return double rate buy.
     */
    private double getCurrencyRateBuy(double currencyRate){

        return currencyRate - (0.989/100)*currencyRate;
    }
    /**
     * Util method to return rate sell based on rate cross.
     *
     * @param currencyRate Currency rate cross.
     * @return double rate sell.
     */
    private double getCurrencyRateSell(double currencyRate){

        return currencyRate + (0.989/100)*currencyRate;
    }
}
