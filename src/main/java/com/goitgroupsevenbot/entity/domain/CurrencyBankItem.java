package com.goitgroupsevenbot.entity.domain;

import com.goitgroupsevenbot.entity.enums.Banks;
import com.goitgroupsevenbot.entity.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyBankItem {
    private Banks banks;
    private Currency currency;
    private double rateBuy;
    private double rateSell;
}
