package ru.alibaev.quotation.service;

import org.springframework.stereotype.Service;
import ru.alibaev.quotation.concurrent.CustomMutex;
import ru.alibaev.quotation.dto.QuoteDto;
import ru.alibaev.quotation.entity.EnergyLevel;
import ru.alibaev.quotation.entity.Quote;
import ru.alibaev.quotation.repository.EnergyLevelRepository;
import ru.alibaev.quotation.repository.QuotationRepository;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Service
public class QuotationService {

    private QuotationRepository quotationRepository;
    private EnergyLevelRepository energyLevelRepository;
    private CustomMutex customMutex;
    private ForkJoinPool forkJoinPool;

    public QuotationService(QuotationRepository quotationRepository, EnergyLevelRepository energyLevelRepository, CustomMutex customMutex) {
        this.quotationRepository = quotationRepository;
        this.energyLevelRepository = energyLevelRepository;
        this.customMutex = customMutex;
        this.forkJoinPool = ForkJoinPool.commonPool();
    }

    /**
     * Сохранение котировки и возврат лучшей цены по данному инструменту
     * @param quoteDto объект дата-трансфер для котировки
     * @return вычисленное значение energy level (elvl) по данному инструменту
     */
    public String saveQuote(QuoteDto quoteDto) {
        return forkJoinPool.invoke(new CalculateElvl(quoteDto));
    }

    // задача для сохранения котировки и вычисления лучшей цены по инструменту, указанному в котировке
    class CalculateElvl extends RecursiveTask<String> {
        private QuoteDto quoteDto;
        CalculateElvl(QuoteDto quoteDto) {
            this.quoteDto = quoteDto;
        }

        @Override
        protected String compute() {
            // Сохраняем котировку в базе данных
            Quote quote = new Quote();
            quote.setIsin(quoteDto.getIsin());
            quote.setBid(quoteDto.getBid());
            quote.setAsk(quoteDto.getAsk());
            quotationRepository.save(quote);
            // запрашиваем семафор на доступ к изменению elvl инструмента, указанного в котировке
            // если семафором владеет другой поток, то данный поток будет ждать освобождения семафора
            customMutex.acquire(quoteDto.getIsin());
            // получаем данные о лучшей цене по данному инструменту из базы
            EnergyLevel energyLevel = energyLevelRepository.findByIsin(quoteDto.getIsin());

            // если в базе данных еще нет данных по лучшей цене, то вносим их
            if (energyLevel == null) {
                energyLevel = new EnergyLevel();
                energyLevel.setIsin(quoteDto.getIsin());
                energyLevel.setQuote(quote);
                energyLevel.setElvl(quoteDto.getBid() != null ? quoteDto.getBid() : quoteDto.getAsk());
            } else {
                // если в базе есть данные о лучшей цене, то сравниваем ее с данными нашей котировки и определяем новую лучшую цену
                if (quoteDto.getBid().doubleValue() > energyLevel.getElvl().doubleValue()) {
                    energyLevel.setElvl(quoteDto.getBid());
                } else if (quoteDto.getAsk().doubleValue() < energyLevel.getElvl().doubleValue()) {
                    energyLevel.setElvl(quoteDto.getAsk());
                }
            }

//            System.out.println("Поток: " + Thread.currentThread().getName() + ", elvl: " + energyLevel.getElvl());
//            System.out.println("Количество потоков в пуле: " + forkJoinPool.getActiveThreadCount());
//            System.out.println("Количество задач в очереди: " + forkJoinPool.getQueuedSubmissionCount());

            // сохраняем лучшую цену в базу данных
            energyLevelRepository.saveAndFlush(energyLevel);
            // освобождаем семафор
            customMutex.release(quoteDto.getIsin());
            // возвращаем текстовое представление лучшей цены по инструменту, указанному в котировке
            return energyLevel.getElvl().toString();
        }
    }
}
