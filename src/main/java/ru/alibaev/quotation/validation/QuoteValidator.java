package ru.alibaev.quotation.validation;

import ru.alibaev.quotation.constraints.QuoteConstraint ;
import ru.alibaev.quotation.entity.Quote;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация котировки в части цен
 */
public class QuoteValidator implements
        ConstraintValidator<QuoteConstraint, Quote> {

    @Override
    public void initialize(QuoteConstraint quoteConstraint) {
    }

    @Override
    public boolean isValid(Quote quote, ConstraintValidatorContext context) {
        return quote.getBid().doubleValue() < quote.getAsk().doubleValue();
    }
}
