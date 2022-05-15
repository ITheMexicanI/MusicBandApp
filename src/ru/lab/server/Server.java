package ru.lab.server;

import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.Serializator;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    private final DatagramSocket socket = new DatagramSocket(Serializator.PORT);

    private DataBaseHelper database;
    private MusicBandCollection collection;

    public Server() throws SocketException {
    }

    public static void main(String[] args) {
        try {
            logger.info("Server started");
            Server server = new Server();
            server.start(args);
        } catch (SocketException e) {
            logger.info("Socket error/Port is busy");
            e.printStackTrace();
        }
    }

    private void start(String[] args) throws SocketException {
        initializeDataBase(args);
        RequestProcessor requestProcessor = new RequestProcessor(socket, collection, database);
        requestProcessor.run();

    }


    private void initializeDataBase(String[] args) {
        try {
            database = new DataBaseHelper(args);
            collection = database.load();
        } catch (SQLException e) {
            logger.info("DataBase initializing error");
            System.exit(-1);
        }
    }
}
