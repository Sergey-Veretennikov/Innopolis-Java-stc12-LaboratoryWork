package ru.innopolis.lw02;

import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Поток, который получает результат ArrayBlockingQueue и записывает в файл.
 */
class ResultWriter implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ResultWriter.class);
    private final String filePath;
    private final String[] sources;
    private final BlockingQueue<String> stringResultBlockingQueue;

    ResultWriter(String[] sources, BlockingQueue<String> stringResultBlockingQueue, String filePath) {
        this.sources = sources;
        this.stringResultBlockingQueue = stringResultBlockingQueue;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        String msg;
        int count = 0;
        try (FileWriter fileWriter = new FileWriter(filePath, false)) {
            while (count != sources.length) {
                msg = stringResultBlockingQueue.take();
                if (msg.equals("EXIT")) {
                    count++;
                } else {
                    fileWriter.write(msg + "\n");
                    fileWriter.flush();
                }
            }
            long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - Main.t0);
            LOGGER.info("Время обработки ресурсов: " + seconds + " секунд");
            LOGGER.info(stringResultBlockingQueue);

        } catch (InterruptedException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
