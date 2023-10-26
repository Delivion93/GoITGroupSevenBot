package com.goitgroupsevenbot.service.Jobs;

import com.goitgroupsevenbot.repository.CurrencyBankRepositoryDomain;
import com.goitgroupsevenbot.repository.CurrencyBankRepositoryDto;
import org.quartz.*;

public class CurrencyUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext){

        CurrencyBankRepositoryDto.setCurrencyList();
        CurrencyBankRepositoryDomain.setCurrencyListDomainBanks();
        System.out.println("CurrencyBankRepositoryDomain.listDomainBanks = " + CurrencyBankRepositoryDomain.listDomainBanks);
    }
}
