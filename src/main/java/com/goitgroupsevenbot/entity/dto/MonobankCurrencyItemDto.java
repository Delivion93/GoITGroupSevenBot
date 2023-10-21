package com.goitgroupsevenbot.entity.dto;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *DTO object.
 * @author Shalaiev Ivan
 * @version 1.0.0 19.10.2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeName("MonobankCurrencyItem")
public class MonobankCurrencyItemDto {
    private int currencyCodeA;
    private int currencyCodeB;
    private long date;
    private double rateBuy;
    private double rateCross;
    private double rateSell;
}
