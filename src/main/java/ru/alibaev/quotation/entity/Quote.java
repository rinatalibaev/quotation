package ru.alibaev.quotation.entity;

import lombok.Data;
import ru.alibaev.quotation.constraints.QuoteConstraint;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Сущность котировки по ценной бумаге
 */
@Entity
@Data
@QuoteConstraint
public class Quote {

    @Id
    @GeneratedValue
    private long id;

    /**
     * международный идентификационный код ценной бумаги
     */
    @Size(min = 12, max = 12)
    private String isin;

    /**
     * максимальная цена, которую покупатель готов заплатить за ценную бумагу
     */
    private BigDecimal bid;

    /**
     * минимальная цена, по которой продавец готов продать ценную бумагу
     */
    private BigDecimal ask;



}
