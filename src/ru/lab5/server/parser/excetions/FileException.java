package ru.lab5.server.parser.excetions;

/**
 * Возникает если к файлу нет доступа или файл не найден
 */
public class FileException extends RuntimeException {
    @Override
    public String getMessage() {
        return "The file does not exist or there is no access to it";
    }
}
