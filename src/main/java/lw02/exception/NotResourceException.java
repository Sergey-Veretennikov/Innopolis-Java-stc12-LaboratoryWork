package lw02.exception;

public class NotResourceException extends Exception {
    public NotResourceException(String message) {
        super(message + " - Не является: file, http, ftp !!!");
    }
}
