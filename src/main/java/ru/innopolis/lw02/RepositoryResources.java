package ru.innopolis.lw02;

import java.net.URL;

/**
 * При создании объект класса, хранит всю информацию о входном ресурсе
 */
public class RepositoryResources {
    private String fileSize;
    private URL urll;

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public URL getUrll() {
        return urll;
    }

    public void setUrll(URL urll) {
        this.urll = urll;
    }

    @Override
    public String toString() {
        return "RepositoryResources{" +
                "fileSize='" + fileSize + '\'' +
                ", urll=" + urll +
                '}';
    }
}
