package ru.innopolis.lw02;

import java.io.File;
import java.util.Objects;

public class Main {
    static long t0;

    public static void main(String[] args) {

        final File folder = new File("C://ResourceLab/");//C://ResourceLab/  D://temp/
        final String[] words = {"little", "Xhmkuiapy", "DISAPPEARS"};
        final String res = "C://ResourceLabRezultat/Rezultat.txt";
        t0 = System.nanoTime();
        new OccurrencesWordsResourceArray().getOccurencies(listFilesForFolder(folder), words, res);
    }

    private static String[] listFilesForFolder(final File folder) {
        String[] messages = new String[Objects.requireNonNull(folder.listFiles()).length];
        for (int i = 0; i < Objects.requireNonNull(folder.listFiles()).length; i++) {
            if (Objects.requireNonNull(folder.listFiles())[i].isFile()) {
                messages[i] = "file:" + Objects.requireNonNull(folder.listFiles())[i].getPath();
            }
        }
        return messages;
    }
}
