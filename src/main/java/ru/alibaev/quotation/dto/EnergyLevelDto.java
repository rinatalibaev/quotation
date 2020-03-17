package ru.alibaev.quotation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyLevelDto {
    private String isin;
    private BigDecimal elvl;
}
