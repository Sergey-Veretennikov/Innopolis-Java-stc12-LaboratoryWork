package ru.innopolis.lw02;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceHandlerTest {
    private static final Logger LOGGER = Logger.getLogger(ResourceReaderTest.class);
    private static final BlockingQueue<RepositoryResources> resourcesBlockingQueue = new ArrayBlockingQueue<>(10);
    private static final BlockingQueue<String> stringResultBlockingQueue = new ArrayBlockingQueue<>(10);
    private static final String[] words = {"little", "Xhmkuiapy", "DISAPPEARS"};
    private static final String PATH1 = "file:stevenson_treasure_island_txt.txt";
    private static final String PATH2 = "file:stoker_dracula_txt.txt";
    private static final String PATH3 = "file:twain_tom_sawyer_txt.txt";
    private ResourceReader resourceReader;

    @BeforeEach
    void setUpClass() {
        LOGGER.warn("\u001B[34m" + "Test starting" + "\u001B[0m");
        resourceReader = new ResourceReader(new String[]{PATH1, PATH2, PATH3}, resourcesBlockingQueue);
    }


    @Test
    void checksSizeOfResourcesBlockingQueueTest() {
        resourceReader.run();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 3; i++) {
            executorService.submit(new ResourceHandler(resourcesBlockingQueue, stringResultBlockingQueue, words));
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        assertEquals(0, resourcesBlockingQueue.size());
        LOGGER.warn(resourcesBlockingQueue.toString());
        LOGGER.warn(stringResultBlockingQueue.toString());
    }

/*    @Test
    void checksNoEmptyOfBlockingQueueTest() {
        resourceReader.run();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 3; i++) {
            executorService.submit(new ResourceHandler(resourcesBlockingQueue, stringResultBlockingQueue, words));
        }

        Mockito.times(2000);
        assertTrue(!stringResultBlockingQueue.isEmpty());
        LOGGER.warn(resourcesBlockingQueue.toString());
        LOGGER.warn(stringResultBlockingQueue.toString());
    }*/
}
