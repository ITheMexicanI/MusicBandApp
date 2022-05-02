package ru.lab.client;

import ru.lab.common.commands.CommandReader;
import ru.lab.common.commands.CommandValidator;
import ru.lab.common.commands.inputSystem.CommandInput;
import ru.lab.common.commands.inputSystem.ConsoleCommandInput;
import ru.lab.common.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Client {
    private User user;

    public Client() {
        DatagramChannel channel = null;
        user = new User();

        try {
            channel = startClient();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске клиента");
        }

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", CommandReader.PORT);

        CommandInput commandInput = new ConsoleCommandInput();
        CommandReader commandReader = new CommandReader();
        commandReader.setValidator(new CommandValidator(commandInput));

        commandReader.setClient(this);
        commandReader.setChannel(channel);
        commandReader.setServerAddress(serverAddress);

        commandReader.read(commandInput);
    }

    public static DatagramChannel startClient() throws IOException {
        DatagramChannel client = DatagramChannelBuilder.bindChannel(null);
        client.configureBlocking(false);
        return client;
    }

    public void sendMessage(DatagramChannel client, ByteArrayOutputStream message, SocketAddress serverAddress) throws IOException, InterruptedException {
        ByteBuffer request = ByteBuffer.wrap(message.toByteArray());
        ByteBuffer serverResponse = ByteBuffer.allocate(CommandReader.MAX_SIZE);

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

        Response responseFromServer = (Response) Serializator.deserialize(serverResponse.array());
        processResponse(Objects.requireNonNull(responseFromServer));
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

    public User getUser() {
        return user;
    }

    public static void main(String[] args) {
        new Client();
    }
}
