package ru.innopolis.lw02;

import lw02.RepositoryResources;
import lw02.ResourceReader;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceReaderTest {
    private static final Logger LOGGER = Logger.getLogger(ResourceReaderTest.class);
    private static BlockingQueue<RepositoryResources> resourcesBlockingQueue = new ArrayBlockingQueue<>(10);
    private final String PATH1 = "file:C://ResourceLab/stevenson_treasure_island_txt.txt";
    private final String PATH2 = "file:C://ResourceLab/stoker_dracula_txt.txt";
    private final String PATH3 = "file:C://ResourceLab/twain_tom_sawyer_txt.txt";
    private ResourceReader resourceReader;


    @BeforeEach
    void setUpClass() {
        LOGGER.warn("\u001B[34m" + "Test starting" + "\u001B[0m");
        resourceReader = new ResourceReader(new String[]{PATH1, PATH2, PATH3}, resourcesBlockingQueue);
        new Thread(resourceReader).start();
    }

    @Test
    void checksSizeOfBlockingQueueTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }


        assertEquals(3, resourcesBlockingQueue.size());
        LOGGER.warn(resourcesBlockingQueue.toString());
    }
}