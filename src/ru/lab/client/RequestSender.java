package ru.lab.client;

import ru.lab.common.commands.Command;
import ru.lab.common.commands.exceptions.InvalidExecuteCommand;
import ru.lab.common.commands.inputSystem.CommandInput;
import ru.lab.common.utils.*;

import java.io.ByteArrayOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class RequestSender {
    private final CommandValidator validator;
    private final DatagramChannel channel;
    private final SocketAddress serverAddress;

    private User user;


    public RequestSender(CommandValidator validator, SocketAddress socketAddress, DatagramChannel channel, User user) {
        this.validator = validator;
        this.serverAddress = socketAddress;
        this.channel = channel;
        this.user = user;
    }

    public void read() {
        CommandInput reader = validator.getReader();
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) return;

                AbstractMap.SimpleEntry<Command, String> commandEntry = parseCommand(line);
                Command command = commandEntry.getKey();
                String commandArgs = commandEntry.getValue();

                Request request = validateCommand(command, commandArgs);
                if (request == null) continue;

                ByteArrayOutputStream serializedRequest = Serializator.serialize(request);
                Response responseFormServer = sendRequestAndGetResponse(channel, serializedRequest, serverAddress);
                processResponse(responseFormServer);
            } catch (Exception e) {
                System.err.println("Команда неверна >.<");
            }
        }
    }

    protected void sendHelp() {
        Request request = validateCommand(Command.HELP, null);
        ByteArrayOutputStream serializedRequest = Serializator.serialize(request);
        Response responseFormServer = sendRequestAndGetResponse(channel, serializedRequest, serverAddress);
        processResponse(responseFormServer);
    }

    private Response sendRequestAndGetResponse(DatagramChannel client, ByteArrayOutputStream message, SocketAddress serverAddress) {
        Response responseFromServer = null;
        try {
            // Create buffer with request
            ByteBuffer request = ByteBuffer.wrap(message.toByteArray());
            ByteBuffer serverResponse = ByteBuffer.allocate(Serializator.MAX_SIZE);

            // Try to send request and get response
            outer:
            for (int i = 0; i < 5; i++) {
                System.out.println("Sending...");
                Instant deadline = Instant.now().plusSeconds(1);
                while (Instant.now().isBefore(deadline)) {
                    request.rewind();
                    int numSent = client.send(request, serverAddress);
                    if (numSent > 0) break;
                    Thread.sleep(100);
                }

                deadline = Instant.now().plusSeconds(1);
                while (Instant.now().isBefore(deadline)) {
                    SocketAddress address = client.receive(serverResponse);
                    if (serverAddress.equals(address)) break outer;
                    Thread.sleep(100);
                }
            }

            // Serialize response
            responseFromServer = (Response) Serializator.deserialize(serverResponse.array());

            // Confirm response (sending confirm request)
            ByteBuffer requestWithConfirm = ByteBuffer.wrap(Serializator.serialize(new Request(null, responseFromServer.getIdentificationString(), user)).toByteArray());
            client.send(requestWithConfirm, serverAddress);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке пакета на сервер :(");
        }
        return responseFromServer;
    }

    private void processResponse(Response responseFromServer) {
        String title = responseFromServer.getTitle();
        Object message = responseFromServer.getMessage();
        Mark mark = responseFromServer.getMark();

        if (!title.isEmpty()) System.out.println(title);

        switch (mark) {
            case STRING:
                System.out.println((String) message);
                break;
            case LIST:
                ((List) message).forEach(System.out::println);
                break;
            case STACK:
                ((Stack) message).forEach(System.out::println);
                break;
            case USER:
                User newUser = (User) message;
                user = newUser;
        }
    }

    private Request validateCommand(Command command, String commandArgs) {
        Request request = null;

        switch (command) {
            case REG:
                request = validator.reg(user);
                break;
            case LOG:
                request = validator.log(user);
                break;


            case HELP:
                request = validator.help(user);
                break;
            case INFO:
                request = validator.info(user);
                break;
            case SHOW:
                request = validator.show(user);
                break;
            case ADD:
                request = validator.add(user);
                break;
            case UPDATE_ID:
                request = validator.updateId(commandArgs, user);
                break;
            case REMOVE_BY_ID:
                request = validator.removeId(commandArgs, user);
                break;
            case CLEAR:
                request = validator.clear(user);
                break;
            case EXECUTE:
                request = validator.executeScript(commandArgs, channel, serverAddress, user);
                break;
            case EXIT:
                validator.exit();
                break;
            case SHUFFLE:
                request = validator.shuffle(user);
                break;
            case SORT:
                request = validator.sort(user);
                break;
            case REORDER:
                request = validator.reorder(user);
                break;
            case SHOW_BY_ALBUM:
                request = validator.showByBestAlbum(user);
                break;
            case SHOW_GREATER_THAN_ALBUM:
                request = validator.showGreaterThanAlbum(user);
                break;
            case SHOW_NUM_OF_PARTICIPANTS:
                request = validator.showNumberOfParticipants(user);
                break;
        }
        return request;
    }

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
}
