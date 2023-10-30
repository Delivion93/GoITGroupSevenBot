package com.goitgroupsevenbot.entity.domain;

import com.goitgroupsevenbot.entity.enums.Banks;
import com.goitgroupsevenbot.entity.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified class for DTO objects.
 *
 * @author Shalaiev Ivan
 * @version 1.0.0 30.10.2023
 */
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
