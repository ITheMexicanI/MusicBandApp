package ru.lab.server;

import ru.lab.common.commands.Command;
import ru.lab.common.commands.CommandExecutor;
import ru.lab.common.commands.CommandReader;
import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.*;
import ru.lab.server.database.DataBaseHelper;
import ru.lab.server.parser.CSVReader;
import ru.lab.server.parser.CSVWriter;
import ru.lab.server.parser.excetions.FileException;
import sun.misc.Signal;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.logging.Logger;

public class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private final DatagramSocket socket;

    private final byte[] clientRequestBuffer = new byte[CommandReader.MAX_SIZE];
    private final byte[] clientConfirmBuffer = new byte[CommandReader.MAX_SIZE];

    private DatagramPacket packetFromClient;
    private Request clientRequest;

    private CommandReader commandReader;
    private DataBaseHelper database;

    public Server(DatagramSocket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(CommandReader.PORT);
        Server server = new Server(datagramSocket);

        try {
            logger.info("Server started");
            server.run(server.getFileFromArgs(args));
        } catch (IOException e) {
            logger.info("Server error");
        }
    }

    public void run(String path) throws IOException {
        MusicBandCollection collection = new MusicBandCollection();
        initializeDataBase();

        CSVReader fileReader = new CSVReader(collection);
        fileReader.readCSVFile(path);
//        setupSignalHandler(collection);
//        setupShutDownWork(collection);

        commandReader = new CommandReader();
        commandReader.setExecutor(new CommandExecutor(collection, database));

        while (true) {
            socket.setSoTimeout(0);
            packetFromClient = new DatagramPacket(clientRequestBuffer, clientRequestBuffer.length);
            socket.receive(packetFromClient);

            clientRequest = (Request) Serializator.deserialize(packetFromClient.getData());
            String messageFromClient = clientRequest.toString();
            logger.info("Message from client: " + messageFromClient);

            processRequest();
        }
    }

    private void initializeDataBase() {
        database = new DataBaseHelper();
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
                if (i < 3) logger.info("Confirmation of response was not received. Resending...");
                else logger.info("Client has died");
            } catch (IOException e) {
                logger.info("Server/Client error");
            }
        }
    }

    private DatagramPacket createResponse() {
        ByteArrayOutputStream response;
        Command command = clientRequest.getCommand();
        Object argument = clientRequest.getArgument();

        boolean isAuthorized = checkClient();

        if (isAuthorized || command.equals(Command.HELP) || command.equals(Command.LOG) || command.equals(Command.REG)) {
            response = Serializator.serialize(commandReader.executeCommand(command, argument));
        } else {
            response = Serializator.serialize(new Response("Вы не авторизованы, зарегестрируйтесь или войдите в аккаунт.", "Открыть справку: 'help'", Mark.STRING));
        }
        return new DatagramPacket(Objects.requireNonNull(response).toByteArray(), response.toByteArray().length, packetFromClient.getAddress(), packetFromClient.getPort());
    }

    private boolean checkClient() {
        User clientUser = clientRequest.getUser();
        if (clientUser == null) return false;
        if (clientUser.getLogin() == null || clientUser.getPassword() == null) return false;
        return database.checkUser(clientUser);
    }

//    private void setupSignalHandler(MusicBandCollection database) {
//        Signal.handle(new Signal("TSTP"), signal -> {
//            saveData(database);
//            socket.close();
//        });
//    }
//
//    private void setupShutDownWork(MusicBandCollection database) {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            saveData(database);
//            socket.close();
//        }));
//    }

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

    private String getFileFromArgs(String[] args) {
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
