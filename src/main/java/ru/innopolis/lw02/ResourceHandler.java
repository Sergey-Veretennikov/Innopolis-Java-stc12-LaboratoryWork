package ru.innopolis.lw02;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Создаем поток, который берет объект RepositoryResources из ArrayBlockingQueue, из объекта получает ссылку на ресурс,
 * скачивает и в зависимости от характеристики Small file или Large file обрабатывает его в одном или нескольких
 * потоках, результат складывает в другую ArrayBlockingQueue.
 */
class ResourceHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ResourceHandler.class);
    private final BlockingQueue<RepositoryResources> resourcesBlockingQueue;
    private final BlockingQueue<String> stringResultBlockingQueue;
    private final String[] words;
    private Scanner scanner;

    ResourceHandler(BlockingQueue<RepositoryResources> resourcesBlockingQueue,
                    BlockingQueue<String> stringResultBlockingQueue, String[] words) {
        this.resourcesBlockingQueue = resourcesBlockingQueue;
        this.stringResultBlockingQueue = stringResultBlockingQueue;
        this.words = words;
    }

    @Override
    public void run() {
        try {
            RepositoryResources repositoryResources = resourcesBlockingQueue.take();
            if (repositoryResources.getFileSize().equals("Large file")) {
                splinterLarge(repositoryResources);
            } else {
                splinter(repositoryResources);
            }
        } catch (InterruptedException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(resourcesBlockingQueue.toString());
        }
    }

    private void splinterLarge(RepositoryResources repositoryResources) throws IOException {
        scanner = new Scanner(repositoryResources.getUrll().openStream());
        List<String> stringList = new CopyOnWriteArrayList<>();
        int count = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        while (scanner.hasNext()) {
            if (count > 500) {
                executorService.submit(analyzes(stringList, repositoryResources));
                stringList.clear();
                count = 0;
            }
            stringList.add(scanner.nextLine());
            count++;
        }
        executorService.submit(analyzes(stringList, repositoryResources));
        executorService.submit(exit());
        executorService.shutdown();
    }

    private void splinter(RepositoryResources repositoryResources) throws IOException, InterruptedException {
        scanner = new Scanner(repositoryResources.getUrll().openStream());
        while (scanner.hasNext()) {
            analyzesParse((scanner.nextLine()).split("[.!?;]\\\\s|\"|<|>|\\\\n|\\\\r"), repositoryResources);
        }
        stringResultBlockingQueue.put("EXIT");
    }

    private Runnable exit() {
        return new Thread(() -> {
            try {
                stringResultBlockingQueue.put("EXIT");
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private Runnable analyzes(List<String> list, RepositoryResources repositoryResources) {
        return new Thread(() -> {
            for (String stringLine : new ArrayList<>(list)) {
                String[] strings = stringLine.split("([.!?;]\\\\s|\"|<|>|\\\\n|\\\\r)");//([A-Z|А-Я|Ё]{1}[а-я|ё|\w\s,-;:\(\)]+[\.\?\!]) [.!?;]\\s|\"|<|>|\\n|\\r
                analyzesParse(strings, repositoryResources);
            }
        });
    }

    private void analyzesParse(String[] strings, RepositoryResources repositoryResources) {
        for (String st : strings) {
            for (String word : words) {
                Pattern pattern = Pattern.compile(word);
                Matcher matcher = pattern.matcher(st);
                if (matcher.find()) {
                    try {
                        stringResultBlockingQueue.put(repositoryResources.getUrll().getFile() + "-" + st);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                    break;
                }
            }
        }
    }
}
