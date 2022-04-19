package ru.lab5.server;

import ru.lab5.common.commands.Command;
import ru.lab5.common.commands.CommandExecutor;
import ru.lab5.common.commands.CommandReader;
import ru.lab5.common.mainObjects.MusicBandCollection;
import ru.lab5.common.network.Request;
import ru.lab5.common.network.Serializator;
import ru.lab5.server.parser.CSVReader;
import ru.lab5.server.parser.CSVWriter;
import ru.lab5.server.parser.excetions.FileException;
import sun.misc.Signal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private final DatagramSocket socket;

    private final byte[] clientRequestBuffer = new byte[CommandReader.MAX_SIZE];
    private final byte[] clientConfirmBuffer = new byte[CommandReader.MAX_SIZE];

    private DatagramPacket prevUser;
    private DatagramPacket packetFromClient;
    private Request clientRequest;

    private CommandReader commandReader;

    public Server(DatagramSocket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(CommandReader.PORT);
        Server server = new Server(datagramSocket);
        logger.info("Server starts...");

        try {
            server.run(args[0]);
        } catch (IOException e) {
            logger.info("Server error");
        }
    }

    public void run(String path) throws IOException {
        MusicBandCollection collection = new MusicBandCollection();

        CSVReader fileReader = new CSVReader(collection);
        fileReader.readCSVFile(path);
        setupSignalHandler(collection);  // TODO: доделать

        commandReader = new CommandReader();
        commandReader.setExecutor(new CommandExecutor(collection));

        while (true) {
            socket.setSoTimeout(0);
            packetFromClient = new DatagramPacket(clientRequestBuffer, clientRequestBuffer.length);
            socket.receive(packetFromClient);

            if (prevUser == null || !prevUser.getSocketAddress().equals(packetFromClient.getSocketAddress())) {
                prevUser = packetFromClient;
                logger.info("Client connected:\n\tIP: " + packetFromClient.getAddress().getCanonicalHostName() + "\n\tPort: " + packetFromClient.getPort());
            }

            clientRequest = (Request) Serializator.deserialize(packetFromClient.getData());
            String messageFromClient = clientRequest.toString();
            logger.info("Message from client: " + messageFromClient);

            processRequest();
        }
    }

    private void processRequest() {
        DatagramPacket packetToClient = createResponse();

        for (int i = 0; i < 3; i++) {
            try {
                socket.send(packetToClient);
                DatagramPacket packetWithClientConfirm = new DatagramPacket(clientConfirmBuffer, clientConfirmBuffer.length);
                int timeout = 1000; // 1 сек
                socket.setSoTimeout(timeout);
                socket.receive(packetWithClientConfirm);

                Request request = (Request) Serializator.deserialize(packetWithClientConfirm.getData());
                logger.info("Response was received: " + request.getArgument().equals("response confirm"));
                break;
            } catch (SocketTimeoutException e) {
                logger.info("Confirmation of response was not received. Resending...");
            } catch (IOException e) {
                logger.info("Server/Client error");
            }
        }
    }

    private DatagramPacket createResponse() {
        Command command = clientRequest.getCommand();
        Object argument = clientRequest.getArgument();

        ByteArrayOutputStream response = Serializator.serialize(commandReader.executeCommand(command, argument));
        return new DatagramPacket(response.toByteArray(), response.toByteArray().length, packetFromClient.getAddress(), packetFromClient.getPort());
    }

    private static void setupSignalHandler(MusicBandCollection database) {
        Signal.handle(new Signal("TSTP"), signal -> {
            logger.info("Saving database...");
            try {
                CSVWriter writer = new CSVWriter(database);
                writer.writeCSVFile("database.csv");
            } catch (FileException | IOException e) {
                logger.info("Saving error. No such file.");
            }
        });
    }
}
