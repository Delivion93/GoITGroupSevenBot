package com.goitgroupsevenbot.repository;

import com.goitgroupsevenbot.entity.domain.CurrencyBankItem;

import java.util.concurrent.CopyOnWriteArrayList;

public class CurrencyBankRepository {
    private static CopyOnWriteArrayList<CurrencyBankItem> listDomainBanks = new CopyOnWriteArrayList<>();
        public static CopyOnWriteArrayList<CurrencyBankItem> getList(){
            return listDomainBanks;
        }
}
