package ru.innopolis.lw02;

import org.apache.log4j.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Сначала запускаем первый поток чтения ресурсов.
 * Потом создаем пул потоков и в этом пуле читаем и обрабатываем ресурсы.
 * Далее запускаем поток который записывает результаты в файл.
 */
class OccurrencesWordsResourceArray {
    private static final Logger LOGGER = Logger.getLogger(OccurrencesWordsResourceArray.class);
    private final BlockingQueue<RepositoryResources> resourcesBlockingQueue;
    private final BlockingQueue<String> stringResultBlockingQueue;

    OccurrencesWordsResourceArray() {
        this.resourcesBlockingQueue = new ArrayBlockingQueue<>(100);
        this.stringResultBlockingQueue = new ArrayBlockingQueue<>(200);
    }

    void getOccurencies(String[] sources, String[] words, String res) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(resourcesBlockingQueue);
            LOGGER.debug(stringResultBlockingQueue);
        }
        new Thread(new ResourceReader(sources, resourcesBlockingQueue)).start();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < sources.length; i++) {
            executorService.submit(new ResourceHandler(resourcesBlockingQueue, stringResultBlockingQueue, words));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(executorService);
        }
        executorService.shutdown();

        new Thread(new ResultWriter(sources, stringResultBlockingQueue, res)).start();
    }
}



