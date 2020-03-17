package ru.alibaev.quotation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteDto {
    private String isin;
    private BigDecimal bid;
    private BigDecimal ask;
}
