package ru.innopolis.lw02;

import org.apache.log4j.Logger;
import ru.innopolis.lw02.exception.NotResourceException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * - Создаем поток, который бежит по ссылкам считывает информацию каждой по ссылке создает объект RepositoryResources
 * и складывает туда информацию, полученную по ссылке. Далее эти объекты складываем в ArrayBlockingQueue.
 * - В зависимости от размера файла присваиваем ему характеристику Small file или Large file
 */
class ResourceReader implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ResourceReader.class);
    private final String[] sources;
    private final BlockingQueue<RepositoryResources> resourcesBlockingQueue;
    private URL url;

    public ResourceReader(String[] sources, BlockingQueue<RepositoryResources> resourcesBlockingQueue) {
        this.sources = sources;
        this.resourcesBlockingQueue = resourcesBlockingQueue;
    }

    @Override
    public void run() {
        for (String source : sources) {
            url = checksResources(source);
            LOGGER.info(url);
            try {
                switch (url.getProtocol()) {
                    case "file":
                        resourcesBlockingQueue.put(createRepositoryResourcesFile(url));
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(createRepositoryResourcesFile(url).toString());
                        }
                        break;
                    case "http":
                        resourcesBlockingQueue.put(createRepositoryResourcesUrl(url));
                        break;
                    case "ftp:":
                        resourcesBlockingQueue.put(createRepositoryResourcesUrl(url));
                        break;
                    default:
                        throw new NotResourceException(url.getHost());
                }
            } catch (NotResourceException | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
                System.exit(0);
            }
        }
        LOGGER.info(resourcesBlockingQueue.toString());
    }

    private RepositoryResources createRepositoryResourcesFile(URL url) {
        RepositoryResources repositoryResources = new RepositoryResources();
        File file = new File(url.getFile());
        if (file.exists()) {
            if (file.length() > 1_048_576) {
                repositoryResources.setFileSize("Large file"); //Large
                repositoryResources.setUrll(url);
                return repositoryResources;
            } else {
                repositoryResources.setFileSize("Small file");
                repositoryResources.setUrll(url);
                return repositoryResources;
            }
        } else {
            LOGGER.info("Error File: " + file);
            return repositoryResources;
        }
    }

    private RepositoryResources createRepositoryResourcesUrl(URL url) {
        RepositoryResources repositoryResources = new RepositoryResources();
        repositoryResources.setUrll(url);
        repositoryResources.setFileSize("Small file");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(repositoryResources.toString());
        }
        return repositoryResources;
    }

    private URL checksResources(String sources) {
        try {
            if (sources.substring(0, 4).equals("file") || sources.substring(0, 4).equals("http")
                    || sources.substring(0, 4).equals("ftp:")) {
                url = new URL(sources);
            } else {
                throw new NotResourceException(sources);
            }
        } catch (MalformedURLException | NotResourceException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(0);
        }
        return url;
    }
}



