package ru.alibaev.quotation.enumeration;

/**
 * Перечисление для типовых вариантов цен котировок
 */
public enum QuotesEnum {
    RANDOM_1 (102.0 + Math.round(Math.random()*2), 106.5 + Math.round(Math.random()*2)),
    RANDOM_2 (108.0 + Math.round(Math.random()*3), 112.5 + Math.round(Math.random()*2)),
    RANDOM_3 (114.0 + Math.round(Math.random()*2), 117.5 + Math.round(Math.random()*2)),
    LOW (80.0, 90.0),
    MIDDLE (100.0, 101.0),
    HIGH (130.0, 150.0);

    private Double bid;
    private Double ask;

    public Double getBid() {
        return bid;
    }

    public Double getAsk() {
        return ask;
    }

    QuotesEnum(Double bid, Double ask) {
        this.bid = bid;
        this.ask = ask;
    }
}
