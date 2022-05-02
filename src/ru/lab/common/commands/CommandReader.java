package ru.lab.common.commands;

import ru.lab.client.Client;
import ru.lab.common.commands.exceptions.InvalidExecuteCommand;
import ru.lab.common.commands.inputSystem.CommandInput;
import ru.lab.common.mainObjects.Album;
import ru.lab.common.mainObjects.MusicBand;
import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * КЛАСС, ЧИТАЮЩИЙ И ФИЛЬТРУЮЩИЙ КОМАНДЫ
 */
public class CommandReader {
    public static final int MAX_SIZE = 32 * 1024;
    public static final int PORT = 8080;

    private CommandExecutor executor;
    private CommandValidator validator;

    private Client client;
    private DatagramChannel channel;
    private SocketAddress serverAddress;

    /**
     * ЧИТАЕТ КОМАНДЫ
     *
     * @param reader ЧИТАТЕЛЬ, ЧИТАЕТ КОМАНДЫ ЛИБО С КОНСОЛИ, ЛИБО С ФАЙЛА
     */
    public void read(CommandInput reader) {
        while (true) {
            Request request = validateCommand(Command.HELP, null);
            try {
                sendRequest(request);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) return;

                    AbstractMap.SimpleEntry<Command, String> commandEntry = parseCommand(line);
                    Command command = commandEntry.getKey();
                    String commandArgs = commandEntry.getValue();

                    request = validateCommand(command, commandArgs);
                    if (request != null) sendRequest(request);
                } catch (InvalidExecuteCommand e) {
                    System.out.println("Команда неверна");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendRequest(Request request) throws IOException, InterruptedException {
        if (request != null) {
            ByteArrayOutputStream serializedObj;
            serializedObj = Serializator.serialize(request);

            if (serializedObj == null) {
                System.out.println("Ошибка сериализиции");
            } else {
                client.sendMessage(channel, serializedObj, serverAddress);
            }
        }
    }

    /**
     * СМОТРИТ НА КОМАНДУ И ДУМАЕТ ВЫПОЛНЯЕТ ЕЕ
     *
     * @param command     КОМАНДА
     * @param commandArgs АРГУМЕНТ КОМАНДЫ
     */
    private Request validateCommand(Command command, String commandArgs) {
        Request request = null;
        switch (command) {
            case REG:
                request = validator.reg(client.getUser());
                break;
            case LOG:
                request = validator.log(client.getUser());
                break;


            case HELP:
                request = validator.help(client.getUser());
                break;
            case INFO:
                request = validator.info(client.getUser());
                break;
            case SHOW:
                request = validator.show(client.getUser());
                break;
            case ADD:
                request = validator.add(client.getUser());
                break;
            case UPDATE_ID:
                request = validator.updateId(commandArgs, client.getUser());
                break;
            case REMOVE_BY_ID:
                request = validator.removeId(commandArgs, client.getUser());
                break;
            case CLEAR:
                request = validator.clear(client.getUser());
                break;
            case EXECUTE:
                request = validator.executeScript(commandArgs, channel, client, serverAddress, client.getUser());
                break;
            case EXIT:
                validator.exit();
                break;
            case SHUFFLE:
                request = validator.shuffle(client.getUser());
                break;
            case SORT:
                request = validator.sort(client.getUser());
                break;
            case REORDER:
                request = validator.reorder(client.getUser());
                break;
            case SHOW_BY_ALBUM:
                request = validator.showByBestAlbum(client.getUser());
                break;
            case SHOW_GREATER_THAN_ALBUM:
                request = validator.showGreaterThanAlbum(client.getUser());
                break;
            case SHOW_NUM_OF_PARTICIPANTS:
                request = validator.showNumberOfParticipants(client.getUser());
                break;
        }
        return request;
    }

    /**
     * ИСПОЛНЯЕТ КОМНДЫ
     *
     * @param command     ТИП КОМАНДОЧКИ
     * @param commandArgs ВОЗОЖНЫЙ АРГУМЕТ КОМАНДОЧКИ
     */
    public Response executeCommand(Command command, Object commandArgs, User user) {
        Response response = null;
        ReentrantLock lock = executor.getLock();
        lock.lock();
        try {
            switch (command) {
                case REG:
                    response = executor.reg((User) commandArgs);
                    break;
                case LOG:
                    response = executor.log((User) commandArgs);
                    break;


                case HELP:
                    response = executor.help();
                    break;
                case INFO:
                    response = executor.info();
                    break;
                case SHOW:
                    response = executor.show();
                    break;
                case ADD:
                    response = executor.add((MusicBand) commandArgs, user);
                    break;
                case UPDATE_ID:
                    response = executor.updateId((AbstractMap.SimpleEntry<Integer, MusicBand>) commandArgs, user);
                    break;
                case REMOVE_BY_ID:
                    response = executor.removeId((int) commandArgs, user);
                    break;
                case CLEAR:
                    response = executor.clear(user);
                    break;
                case EXECUTE:
                    response = new Response("", "Скрипт был выполнен", Mark.STRING);
                    break;
                case SHUFFLE:
                    response = executor.shuffle();
                    break;
                case SORT:
                    response = executor.sort();
                    break;
                case REORDER:
                    response = executor.reorder();
                    break;
                case SHOW_BY_ALBUM:
                    response = executor.showByBestAlbum((String) commandArgs);
                    break;
                case SHOW_GREATER_THAN_ALBUM:
                    response = executor.showGreaterThanAlbum((Album) commandArgs);
                    break;
                case SHOW_NUM_OF_PARTICIPANTS:
                    response = executor.showNumberOfParticipants();
                    break;
            }
        } finally {
            lock.unlock();
        }

        return response;
    }

    /**
     * @param stringCommand СТРОКА КОМАНДЫ ПОЛЬЗОВАТЕЛЯ
     * @return ВОЗВРАЩАЕТ КОМАНДУ РАЗДЕЛЕННУЮ НА КОМАНДУ И АРГУМЕНТ
     */
    private AbstractMap.SimpleEntry<Command, String> parseCommand(String stringCommand) {
        if (stringCommand.split(" ").length > 0) {
            String commandArgs;
            List<String> commandArr = Arrays.asList(stringCommand.split(" "));
            Command command = Command.getCommandByName(commandArr.get(0));
            if (commandArr.size() > 1) commandArgs = commandArr.get(1);
            else commandArgs = "";

            if (command != null) return new AbstractMap.SimpleEntry<>(command, commandArgs);
        }

        throw new InvalidExecuteCommand();
    }

    /**
     * @param validator ВАЛИДАТОР КОМАНДОЧЕК
     */
    public void setValidator(CommandValidator validator) {
        this.validator = validator;
    }

    /**
     * @param executor ИСПОЛНИТЕЛЬ КОМАНДОЧЕК
     */
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setChannel(DatagramChannel channel) {
        this.channel = channel;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setServerAddress(SocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }
}
