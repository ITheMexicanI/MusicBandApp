package commands;

import commands.exceptions.InvalidExecuteCommand;
import commands.inputSystem.CommandInput;

import java.io.*;
import java.util.*;

/**
 * КЛАСС, ЧИТАЮЩИЙ И ФИЛЬТРУЮЩИЙ КОМАНДЫ
 */
public class CommandReader {
    CommandExecutor executor;

    /**
     * @param executor - ЧЕЛ, КОТОРЫЙ УМЕЕТ ВЫПОЛНЯТЬ КОМАНДЫ
     */
    public CommandReader(CommandExecutor executor) {
        this.executor = executor;
        executor.help();
    }


    /**
     * ЧИТАЕТ КОМАНДЫ
     *
     * @param reader ЧИТАТЕЛЬ, ЧИТАЕТ КОМАНДЫ ЛИБО С КОНСОЛИ, ЛИБО С ФАЙЛА
     */
    public void read(CommandInput reader) {
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) break;
                AbstractMap.SimpleEntry<Command, String> commandEntry = parseCommand(line);
                Command command = commandEntry.getKey();
                String commandArgs = commandEntry.getValue();
                executeCommand(command, commandArgs);
            } catch (InvalidExecuteCommand e) {
                System.out.println("Команда неверна или нет доступа к файлу");
            }
        }
    }

    /**
     * СМОТРИТ НА КОМАНДУ И ДУМАЕТ ВЫПОЛНЯЕТ ЕЕ
     *
     * @param command     КОМАНДА
     * @param commandArgs АРГУМЕНТ КОМАНДЫ
     */
    private void executeCommand(Command command, String commandArgs) {
        switch (command) {
            case HELP:
                executor.help();
                break;
            case INFO:
                executor.info();
                break;
            case SHOW:
                executor.show();
                break;
            case ADD:
                executor.add();
                break;
            case UPDATE_ID:
                executor.updateId(commandArgs);
                break;
            case REMOVE_BY_ID:
                executor.removeId(commandArgs);
                break;
            case CLEAR:
                executor.clear();
                break;
            case SAVE:
                executor.save(commandArgs);
                break;
            case EXECUTE:
                executor.executeScript(commandArgs);
                break;
            case EXIT:
                executor.exit();
                break;
            case INSERT_AT:
                executor.insertAtIndex(commandArgs);
                break;
            case SHUFFLE:
                executor.shuffle();
                break;
            case SORT:
                executor.sort();
                break;
            case REORDER:
                executor.reorder();
                break;
            case SHOW_BY_ALBUM:
                executor.showByBestAlbum();
                break;
            case SHOW_GREATER_THAN_ALBUM:
                executor.showGreaterThanAlbum();
                break;
            case SHOW_NUM_OF_PARTICIPANTS:
                executor.showNumberOfParticipants();
                break;
        }
    }

    /**
     * @param stringCommand СТРОКА КОМАНДЫ ПОЛЬЗОВАТЕЛЯ
     * @return ВОЗВРАЩАЕТ КОМАНДУ РАЗДЕЛЕННУЮ НА КОМАНДУ И АРГУМЕНТ
     */
    private AbstractMap.SimpleEntry<Command, String> parseCommand(String stringCommand) {
        if (stringCommand.equals(" ")) throw new InvalidExecuteCommand();
        else {
            String commandArgs;
            List<String> commandArr = Arrays.asList(stringCommand.split(" "));
            Command command = Command.getCommandByName(commandArr.get(0));
            if (commandArr.size() > 1) commandArgs = commandArr.get(1);
            else commandArgs = "";

            if (command != null) return new AbstractMap.SimpleEntry<>(command, commandArgs);
            throw new InvalidExecuteCommand();
        }
    }
}
