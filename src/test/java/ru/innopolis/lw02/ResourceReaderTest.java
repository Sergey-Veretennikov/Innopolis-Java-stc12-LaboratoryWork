package ru.innopolis.lw02;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceReaderTest {
    private static final Logger LOGGER = Logger.getLogger(ResourceReaderTest.class);
    private static final BlockingQueue<RepositoryResources> resourcesBlockingQueue = new ArrayBlockingQueue<>(10);
    private final String PATH1 = "file:" + "stevenson_treasure_island_txt.txt";
    private final String PATH2 = "file:" + "stoker_dracula_txt.txt";
    private final String PATH3 = "file:" + "twain_tom_sawyer_txt.txt";
    private ResourceReader resourceReader;

    @BeforeEach
    void setUpClass() {
        LOGGER.warn("\u001B[34m" + "Test starting" + "\u001B[0m");
        resourceReader = new ResourceReader(new String[]{PATH1, PATH2, PATH3}, resourcesBlockingQueue);
    }

    @Test
    void checksSizeOfBlockingQueueTest() {
        resourceReader.run();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }


        assertEquals(3, resourcesBlockingQueue.size());
        LOGGER.warn(resourcesBlockingQueue.toString());
    }

    @Test
    void checksTheElementBlockingQueueTest() throws InterruptedException {
        resourceReader.run();
        String[] path = new String[3];
        for (int i = 0; i < 3; i++) {
            path[i] = resourcesBlockingQueue.take().getUrll().toString();
        }
        assertEquals(PATH1, path[0]);
        assertEquals(PATH2, path[1]);
        assertEquals(PATH3, path[2]);
        LOGGER.warn(resourcesBlockingQueue.toString());


    }

    @Test
    void checksEmptyOfBlockingQueueTest() throws InterruptedException {
        resourceReader.run();
        String[] path = new String[3];
        for (int i = 0; i < 3; i++) {
            path[i] = resourcesBlockingQueue.take().getUrll().toString();
        }
        assertTrue(resourcesBlockingQueue.isEmpty());
        LOGGER.warn(resourcesBlockingQueue.toString());
    }
}