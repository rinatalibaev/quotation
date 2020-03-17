package ru.alibaev.quotation.constraints;

import ru.alibaev.quotation.validation.QuoteValidator ;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Ограничение для связки валидатора сущностей Quote
 */
@Documented
@Constraint(validatedBy = QuoteValidator.class)
@Target( { ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface QuoteConstraint {
    String message() default "Введенные Вами данные неверны";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
