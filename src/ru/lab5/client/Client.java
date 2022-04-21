package ru.lab5.client;

import ru.lab5.common.commands.CommandReader;
import ru.lab5.common.commands.CommandValidator;
import ru.lab5.common.commands.inputSystem.CommandInput;
import ru.lab5.common.commands.inputSystem.ConsoleCommandInput;
import ru.lab5.common.utils.Mark;
import ru.lab5.common.utils.Request;
import ru.lab5.common.utils.Response;
import ru.lab5.common.utils.Serializator;

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
    public static DatagramChannel startClient() throws IOException {
        DatagramChannel client = DatagramChannelBuilder.bindChannel(null);
        client.configureBlocking(false);
        return client;
    }

    public static void sendMessage(DatagramChannel client, ByteArrayOutputStream message, SocketAddress serverAddress) throws IOException, InterruptedException {
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

        ByteBuffer confirmRequest = ByteBuffer.wrap(Objects.requireNonNull(Serializator.serialize(new Request(null, "response confirm"))).toByteArray());

        while (true) {
            int numSent = client.send(confirmRequest, serverAddress);
            if (numSent > 0) break;
        }
    }

    private static void processResponse(Response responseFromServer) {
        String title = responseFromServer.getTitle();
        Object message = responseFromServer.getMessage();
        Mark mark = responseFromServer.getMark();

        if (!title.isEmpty()) System.out.println(title);

        if (mark.equals(Mark.STRING)) System.out.println((String) message);
        else if (mark.equals(Mark.STACK)) ((Stack) message).forEach(System.out::println);
        else if (mark.equals(Mark.LIST)) ((List) message).forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException {
        DatagramChannel channel = startClient();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", CommandReader.PORT);

        CommandInput commandInput = new ConsoleCommandInput();
        CommandReader commandReader = new CommandReader();
        commandReader.setValidator(new CommandValidator(commandInput));
        commandReader.setClient(new Client());
        commandReader.setChannel(channel);
        commandReader.setServerAddress(serverAddress);
        commandReader.read(commandInput);
    }
}
