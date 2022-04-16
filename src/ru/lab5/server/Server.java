package ru.lab5.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class Server {
    private final DatagramSocket socket;
    private final int timeout = 1000; // 1 сек

    private final byte[] clientRequestBuffer = new byte[256];
    private final byte[] clientConfirmBuffer = new byte[256];

    public Server(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() throws IOException {
        while (true) {
            // Ожидание запроса
            socket.setSoTimeout(0);
            DatagramPacket packetFromClient = new DatagramPacket(clientRequestBuffer, clientRequestBuffer.length);
            socket.receive(packetFromClient);
            String messageFromClient = new String(packetFromClient.getData(), 0, packetFromClient.getLength());
            System.out.println("Message from client: " + messageFromClient);

            processRequest(packetFromClient);
        }
    }


    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(8080);
        Server server = new Server(datagramSocket);
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server error");
        }
    }

    private void processRequest(DatagramPacket packetFromClient) {
        byte[] response = "Got your message".getBytes(StandardCharsets.UTF_8);
        DatagramPacket packetToClient = new DatagramPacket(response, response.length, packetFromClient.getAddress(), packetFromClient.getPort());

        while (true) { // TODO: заменить на фор 3 раза
            try {
                // Отправка ответа клиенту
                socket.send(packetToClient);

                // Ожидание подтверждения получения
                DatagramPacket packetWithClientConfirm = new DatagramPacket(clientConfirmBuffer, clientConfirmBuffer.length);
                socket.setSoTimeout(timeout);
                socket.receive(packetWithClientConfirm);
                String messageWithClientConfirm = new String(packetWithClientConfirm.getData(), 0, packetWithClientConfirm.getLength());
                System.out.println("Response was received: " + messageWithClientConfirm.equals("response confirm"));
                break;
            } catch (SocketTimeoutException e) {
                System.out.println("Confirmation of response was not received. Resending...");
            } catch (IOException e) {
                System.out.println("Server/Client error");
            }
        }
    }
}
