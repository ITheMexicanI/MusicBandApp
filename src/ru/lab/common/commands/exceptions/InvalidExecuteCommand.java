package ru.lab.common.commands.exceptions;

/**
 * ВОЗНИКАЕТ ПРИ НЕПРАВИЛЬНЫХ КОМАНДАХ ПОЛЬЗОВАТЕЛЯ
 */
public class InvalidExecuteCommand extends RuntimeException {
    @Override
    public String getMessage() {
        return "Команды в исполняемом файле неверны";
    }
}
