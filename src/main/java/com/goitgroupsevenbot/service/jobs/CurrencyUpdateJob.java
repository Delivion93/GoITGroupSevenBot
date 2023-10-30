package com.goitgroupsevenbot.service.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goitgroupsevenbot.entity.dto.MonobankCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.NabuCurrencyItemDto;
import com.goitgroupsevenbot.entity.dto.PrivatCurrencyItemDto;
import com.goitgroupsevenbot.entity.mapper.CurrencyBankItemMapper;
import com.goitgroupsevenbot.repository.CurrencyBankRepository;
import com.goitgroupsevenbot.service.CurrencyService;
import com.goitgroupsevenbot.service.impl.MonobankCurrencyService;
import com.goitgroupsevenbot.service.impl.NabuCurrencyService;
import com.goitgroupsevenbot.service.impl.PrivatBankCurrencyService;
import org.quartz.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class CurrencyUpdateJob implements Job {
//    private CurrencyService currencyServiceMonobank;
//    private CurrencyService currencyServiceNabu;
//    private CurrencyService currencyServicePrivat;
//    private CopyOnWriteArrayList<MonobankCurrencyItemDto> currencyListMonobank;
//    private CopyOnWriteArrayList<NabuCurrencyItemDto> currencyListNabu;
//    private CopyOnWriteArrayList<PrivatCurrencyItemDto> currencyListPrivat;
//    CurrencyBankItemMapper currencyBankItemMapper;
//
//    public CurrencyUpdateJob() {
//        currencyServiceMonobank = new MonobankCurrencyService();
//        currencyServiceNabu = new NabuCurrencyService();
//        currencyServicePrivat = new PrivatBankCurrencyService();
//        currencyListMonobank = new CopyOnWriteArrayList<>();
//        currencyListPrivat = new CopyOnWriteArrayList<>();
//        currencyListNabu = new CopyOnWriteArrayList<>();
//        currencyBankItemMapper = new CurrencyBankItemMapper();
//    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        CurrencyService currencyServiceMonobank= new MonobankCurrencyService();
        CurrencyService currencyServiceNabu= new NabuCurrencyService();
        CurrencyService currencyServicePrivat= new PrivatBankCurrencyService();
        CopyOnWriteArrayList<MonobankCurrencyItemDto> currencyListMonobank = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<NabuCurrencyItemDto> currencyListNabu = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<PrivatCurrencyItemDto> currencyListPrivat = new CopyOnWriteArrayList<>();
        CurrencyBankItemMapper currencyBankItemMapper= new CurrencyBankItemMapper();
        ObjectMapper objectMapper = new ObjectMapper();
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
        CurrencyBankRepository.getList().addAll(currencyBankItemMapper.privatDtoToDomain(currencyListPrivat));
        CurrencyBankRepository.getList().addAll(currencyBankItemMapper.nabuDtoToDomain(currencyListNabu));
        CurrencyBankRepository.getList().addAll(currencyBankItemMapper.monobankDtoToDomain(currencyListMonobank));
        System.out.println("CurrencyBankRepository.listDomainBanks = " + CurrencyBankRepository.getList());

        // Mapping

        //DTO -> Domain


        // CurrencyBankRepository

//        CurrencyBankRepositoryDto.setCurrencyList();
//        CurrencyBankRepository.setCurrencyListDomainBanks();
 //       System.out.println("CurrencyBankRepositoryDomain.listDomainBanks = " + CurrencyBankRepository.listDomainBanks);
    }
}
