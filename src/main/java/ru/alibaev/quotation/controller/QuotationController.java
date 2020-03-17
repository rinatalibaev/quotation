package ru.alibaev.quotation.controller;

import org.springframework.web.bind.annotation.*;
import ru.alibaev.quotation.dto.QuoteDto;
import ru.alibaev.quotation.service.QuotationService;

@RestController
@RequestMapping("/quotation")
public class QuotationController {

    private QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    /**
     * Получение лучшей цены по данному инструменту
     * @param quoteDto объект дата-трансфер для котировки
     * @return вычисленное значение energy level (elvl) по данному инструменту
     */
    @PostMapping
    public @ResponseBody String quotation (@RequestBody QuoteDto quoteDto) {
        return quotationService.saveQuote(quoteDto);
    }

}
