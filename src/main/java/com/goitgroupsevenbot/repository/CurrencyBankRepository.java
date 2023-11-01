package com.goitgroupsevenbot.repository;

import com.goitgroupsevenbot.entity.domain.CurrencyBankItem;

import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Class for mapping from dto object to domain object.
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 28.10.2023
 */
public class CurrencyBankRepository {
    private static CopyOnWriteArrayList<CurrencyBankItem> listDomainBanks = new CopyOnWriteArrayList<>();
        public static CopyOnWriteArrayList<CurrencyBankItem> getList(){
            return listDomainBanks;
        }
        public static void clearList(){
            listDomainBanks = new CopyOnWriteArrayList<>();
        }
}
