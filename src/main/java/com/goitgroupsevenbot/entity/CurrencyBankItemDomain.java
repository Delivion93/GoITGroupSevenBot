package com.goitgroupsevenbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyBankItemDomain {
    private Banks banks;
    private Currency currency;
    private double rateBuy;
    private double rateSell;
}
