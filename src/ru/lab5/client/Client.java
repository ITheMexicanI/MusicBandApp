package ru.lab5.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int timeout = 100;

    private byte[] clientBuffer = new byte[256];
    private final byte[] serverBuffer = new byte[256];
    private final byte[] confirmBuffer = "response confirm".getBytes(StandardCharsets.UTF_8);

    public Client(DatagramSocket socket, String ip) throws UnknownHostException {
        this.socket = socket;
        this.address = InetAddress.getByName(ip);
    }

    public void sendMessage(String request) throws IOException {
        try {
            // Отправка запроса на сервер
            clientBuffer = request.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetToServer = new DatagramPacket(clientBuffer, clientBuffer.length, address, 8080);

            // Ожидание ответа от сервера и его обработка
            socket.setSoTimeout(timeout);
            socket.send(packetToServer);

            DatagramPacket serverResponse = new DatagramPacket(serverBuffer, serverBuffer.length);
            socket.receive(serverResponse);
            String messageFromServer = new String(serverResponse.getData(), 0, serverResponse.getLength());
            System.out.println("Server response: " + messageFromServer);

            // Отправка о подтверждение получения пакета
            DatagramPacket confirmResponse = new DatagramPacket(confirmBuffer, confirmBuffer.length, address, 8080);
            socket.send(confirmResponse);
        } catch (SocketTimeoutException e) {
            System.out.println("Ошибка отправки запроса...");
            // TODO: переотправлять сообщения
        }
    }

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        Client client = new Client(socket, "localhost");
        System.out.println("Send some message to a server.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            client.sendMessage(reader.readLine());
        }
    }

}
