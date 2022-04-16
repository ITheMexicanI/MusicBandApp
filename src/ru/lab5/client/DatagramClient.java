package ru.lab5.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class DatagramClient {
    public static DatagramChannel startClient() throws IOException {
        DatagramChannel client = DatagramChannelBuilder.bindChannel(null);
        client.configureBlocking(false);
        return client;
    }

    public static void sendMessage(DatagramChannel client, String message, SocketAddress serverAddress) throws IOException, InterruptedException {
        ByteBuffer request = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        ByteBuffer serverResponse = ByteBuffer.allocate(256);

        outer:
        while (true) {
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

        String responseFromServer = new String(serverResponse.array(), 0, serverResponse.position());
        System.out.println(responseFromServer);


        ByteBuffer confirmRequest = ByteBuffer.wrap("response confirm".getBytes(StandardCharsets.UTF_8));

        while (true) {
            int numSent = client.send(confirmRequest, serverAddress);
            if (numSent > 0) break;
        }
    }

    private static void resendRequest() {
        System.out.println("Message error");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramChannel client = startClient();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8080);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            sendMessage(client, reader.readLine(), serverAddress);
        }
    }
}
