package ru.alibaev.quotation.concurrent;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Component
public class CustomMutex {

    // отображение для хранения семафоров по идентификационным кодам инструментов
    private Map<String, Semaphore> map = Collections.synchronizedMap(new HashMap<>());

    // предоставляет доступ к изменению лучшей цены (elvl) по идентификационному коду (isin)
    public void acquire (String isin) {
        createSemaphore(isin);
        try {
            map.get(isin).acquire();
//            System.out.println("Поток получил семафор: " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // синхронно создает семафоры по идентификационным кодам (isin)
    // синхронизация необходима, чтобы потоки одновременно не создавали семафоры для доступа к одному инструменту
    private synchronized void createSemaphore(String isin) {
        if (!map.containsKey(isin)) {
            map.put(isin, new Semaphore(1));
        }
    }

    // освобождает семафор по идентификационному коду (isin)
    public void release (String isin) {
        map.get(isin).release();
//        System.out.println("Поток отдал семафор: " + Thread.currentThread().getName());
    }
}
