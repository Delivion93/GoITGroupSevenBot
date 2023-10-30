package com.goitgroupsevenbot.repository;

import com.goitgroupsevenbot.entity.Banks;
import com.goitgroupsevenbot.entity.Currency;
import com.goitgroupsevenbot.entity.CurrencyBankItemDomain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CurrencyBankRepositoryDomain {
    public static CopyOnWriteArrayList<CurrencyBankItemDomain> listDomainBanks;

    public static void setCurrencyListDomainBanks(){
        List<CurrencyBankItemDomain> listPrivatBank = CurrencyBankRepositoryDto.currencyListPrivat.stream()
                .filter(it -> it.getCcy().equals(Currency.EUR.getText()) ||
                        it.getCcy().equals(Currency.USD.getText()))
                .map(it -> CurrencyBankItemDomain.builder()
                        .banks(Banks.PRIVAT_BANK)
                        .currency(Currency.valueOf(it.getCcy()))
                        .rateBuy(it.getBuy())
                        .rateSell(it.getSale())
                        .build())
                .toList();
        List<CurrencyBankItemDomain> listNabu = CurrencyBankRepositoryDto.currencyListNabu.stream()
                .filter(it -> it.getCc().equals(Currency.USD.getText()) || it.getCc().equals(Currency.EUR.getText()))
                .map(it -> CurrencyBankItemDomain.builder()
                        .banks(Banks.NABU)
                        .currency(Currency.valueOf(it.getCc()))
                        .rateBuy(getCurrencyRateBuy(it.getRate()))
                        .rateSell(getCurrencyRateSell(it.getRate()))
                        .build())
                .toList();
        List<CurrencyBankItemDomain> listMonobank = CurrencyBankRepositoryDto.currencyListMonobank.stream()
                .filter(it -> it.getCurrencyCodeB() == 980 &&(it.getCurrencyCodeA() == 840 || it.getCurrencyCodeA() == 978))
                .map(it -> CurrencyBankItemDomain.builder()
                        .banks(Banks.MOMOBANK)
                        .currency(getCurrency(it.getCurrencyCodeA()))
                        .rateBuy(it.getRateBuy())
                        .rateSell(it.getRateSell())
                        .build())
                .toList();
        listDomainBanks = new CopyOnWriteArrayList<>();
        listDomainBanks.addAll(listPrivatBank);
        listDomainBanks.addAll(listNabu);
        listDomainBanks.addAll(listMonobank);
    }

    public static void main(String[] args) {
        CurrencyBankRepositoryDto.setCurrencyList();
        CurrencyBankRepositoryDomain.setCurrencyListDomainBanks();
        listDomainBanks.forEach(System.out::println);

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
    private static double getCurrencyRateBuy(double currencyRate){

        return currencyRate - (0.989/100)*currencyRate;
    }
    /**
     * Util method to return rate sell based on rate cross.
     *
     * @param currencyRate Currency rate cross.
     * @return double rate sell.
     */
    private static double getCurrencyRateSell(double currencyRate){

        return currencyRate + (0.989/100)*currencyRate;
    }
}
