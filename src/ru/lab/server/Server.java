package ru.lab.server;

import ru.lab.common.commands.Command;
import ru.lab.common.commands.CommandExecutor;
import ru.lab.common.commands.CommandReader;
import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.*;
import ru.lab.server.parser.CSVWriter;
import ru.lab.server.parser.excetions.FileException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server implements Runnable {
    public static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private final DatagramSocket socket;

    private CommandReader commandReader;
    private DataBaseHelper database;

    private static String path;

    private final ExecutorService readPool = Executors.newCachedThreadPool();

    public Server() throws SocketException {
        this.socket = new DatagramSocket(CommandReader.PORT);
    }

    public static void main(String[] args) throws SocketException {
        logger.info("Server started");
        path = getFileFromArgs(args);
        new Server().run();
    }

    @Override
    public void run() {
        initializeDataBase();
        MusicBandCollection collection = null;
        try {
            collection = database.load();
        } catch (SQLException e) {
            logger.info("Database error");
            e.printStackTrace();
        }

//        CSVReader fileReader = new CSVReader(collection);
//        fileReader.readCSVFile(path);

//        setupSignalHandler(collection);
//        setupShutDownWork(collection);

        commandReader = new CommandReader();
        commandReader.setExecutor(new CommandExecutor(collection, database));

        while (true) {
            try {
                final byte[] clientRequestBuffer = new byte[CommandReader.MAX_SIZE];

                socket.setSoTimeout(0);
                DatagramPacket packetFromClient = new DatagramPacket(clientRequestBuffer, clientRequestBuffer.length);
                socket.receive(packetFromClient);

                readPool.execute(() -> {
                    Request clientRequest = (Request) Serializator.deserialize(packetFromClient.getData());
                    String messageFromClient = clientRequest.toString();
                    logger.info("Message from client: " + messageFromClient);

                    processRequest(packetFromClient, clientRequest);
                });
            } catch (IOException e) {
                logger.info("IOException in setSocketTimeout");
            }
        }
    }

    private void initializeDataBase() {
        database = new DataBaseHelper();
    }

    private void processRequest(DatagramPacket packetFromClient, Request clientRequest) {
        new Thread(() -> {
            DatagramPacket packetToClient = createResponse(packetFromClient, clientRequest);

            new Thread(() -> {
                try {
                    socket.send(packetToClient);
                } catch (IOException e) {
                    logger.info("Client/Server error");
                }
            }).start();
        }).start();
    }

    private DatagramPacket createResponse(DatagramPacket packetFromClient, Request clientRequest) {
        ByteArrayOutputStream response;
        Command command = clientRequest.getCommand();
        Object argument = clientRequest.getArgument();

        boolean isAuthorized = checkClient(clientRequest);

        if (isAuthorized || command.equals(Command.HELP) || command.equals(Command.LOG) || command.equals(Command.REG)) {
            response = Serializator.serialize(commandReader.executeCommand(command, argument, clientRequest.getUser()));
        } else {
            response = Serializator.serialize(new Response("Вы не авторизованы, зарегестрируйтесь или войдите в аккаунт.", "Открыть справку: 'help'", Mark.STRING));
        }
        return new DatagramPacket(Objects.requireNonNull(response).toByteArray(), response.toByteArray().length, packetFromClient.getAddress(), packetFromClient.getPort());
    }

    private boolean checkClient(Request clientRequest) {
        User clientUser = clientRequest.getUser();
        if (clientUser == null) return false;
        if (clientUser.getLogin() == null || clientUser.getPassword() == null) return false;
        return database.checkUser(clientUser);
    }

    private void saveData(MusicBandCollection database) {
        logger.info("Saving database...");
        try {
            CSVWriter writer = new CSVWriter(database);
            writer.writeCSVFile("database.csv");
            logger.info("Saved");
        } catch (FileException | IOException e) {
            logger.info("Saving error. No such file.");
        }
    }

    private static String getFileFromArgs(String[] args) {
        String fileName = null;

        if (args.length < 1) {
            System.out.println("В аргументе не передан файл");
            System.out.println("Введите файл");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                fileName = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else {
            fileName = args[0];
        }
        return fileName;
    }
}
