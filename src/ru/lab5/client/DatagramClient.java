package ru.lab5.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class DatagramClient {
    public static DatagramChannel startClient() throws IOException {
        DatagramChannel client = DatagramChannelBuilder.bindChannel(null);
        client.configureBlocking(false);
        return client;
    }

    public static void sendMessage(DatagramChannel client, String message, SocketAddress serverAddress) throws IOException, InterruptedException {
        ByteBuffer request = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        int isSending = client.send(request, serverAddress);

        ByteBuffer serverResponse = ByteBuffer.allocate(256);
        SocketAddress isReceived = client.receive(serverResponse);

        if (isSending != message.length() || isReceived == null) resendRequest();

        String responseFromServer = new String(serverResponse.array(), 0, serverResponse.array().length);
        responseFromServer = responseFromServer.replace("\u0000", "");
        System.out.println(responseFromServer);

        if (responseFromServer.equals("")) {

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
