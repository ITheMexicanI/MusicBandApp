package ru.lab.server;

import ru.lab.common.commands.Command;
import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestProcessor implements Runnable {
    private final DatagramSocket socket;
    private final CommandExecutor executor;
    private final DataBaseHelper database;
    private final ExecutorService readPool = Executors.newCachedThreadPool();

    private final List<NotConfirmedRequest> notConfirmingRequests = new ArrayList<>();

    public RequestProcessor(DatagramSocket socket, MusicBandCollection collection, DataBaseHelper database) {
        this.socket = socket;
        this.database = database;
        this.executor = new CommandExecutor(collection, database);

        // Create new thread to confirming requests
        Resender resender = new Resender(socket);
        Thread resenderThread = new Thread(resender);
        resenderThread.start();

    }

    @Override
    public void run() {
        while (true) {
            try {
                final byte[] clientRequestBuffer = new byte[Serializator.MAX_SIZE];

                // Waiting a request
                socket.setSoTimeout(0);
                DatagramPacket packetFromClient = new DatagramPacket(clientRequestBuffer, clientRequestBuffer.length);
                socket.receive(packetFromClient);

                // Multithreaded request reading
                readPool.execute(() -> {
                    // Serialize request
                    Request clientRequest = (Request) Serializator.deserialize(packetFromClient.getData());
                    Server.logger.info("Message from client: " + clientRequest.toString());

                    if (checkRequest(clientRequest)) {
                        new Thread(() -> {
                            // Creating response
                            Response serverResponse = createResponse(clientRequest);
                            Server.logger.info("Message to client: " + serverResponse.getTitle() + "...");

                            new Thread(() -> {
                                // Sending response
                                sendResponse(serverResponse, packetFromClient);
                            }).start();
                        }).start();
                    }
                });
            } catch (IOException e) {
                Server.logger.info("IOException in setSocketTimeout");
            }
        }
    }

    public void sendResponse(Response serverResponse, DatagramPacket packetFromClient) {
        String identificationString = packetFromClient.getAddress().getCanonicalHostName() + ":" + packetFromClient.getPort(); // + " " + System.currentTimeMillis() + packetFromClient.hashCode();
        serverResponse.setIdentificationString(identificationString);

        ByteArrayOutputStream serializedResponse = Serializator.serialize(serverResponse);
        DatagramPacket packetToClient = new DatagramPacket(serializedResponse.toByteArray(), serializedResponse.toByteArray().length, packetFromClient.getAddress(), packetFromClient.getPort());

        try {
            socket.send(packetToClient);
            notConfirmingRequests.add(new NotConfirmedRequest(identificationString, packetToClient));
            Server.logger.info("Waiting confirm from client with identification string: '" + identificationString + "'");
        } catch (IOException e) {
            Server.logger.info("Client died");
        }
    }

    private Response createResponse(Request clientRequest) {
        Response response;
        Command command = clientRequest.getCommand();
        Object argument = clientRequest.getArgument();

        boolean isAuthorized = checkClient(clientRequest);

        if (isAuthorized || command.equals(Command.HELP) || command.equals(Command.LOG) || command.equals(Command.REG)) {
            response = executor.executeCommand(command, argument, clientRequest.getUser());
        } else {
            response = new Response("Вы не авторизованы, зарегистрируйтесь или войдите в аккаунт.", "Открыть справку: 'help'", Mark.STRING);
        }
        return response;
    }

    private boolean checkRequest(Request clientRequest) {
        if (clientRequest.getCommand() == null) {
            String identificationString = (String) clientRequest.getArgument();
            boolean flag = isContainsIdentificationString(identificationString);
            if (flag) {
                removeNotConfirmedRequestByString(identificationString);
                Server.logger.info("Response to client with identification string: '" + identificationString + "' was confirmed");
            }
            return false;
        }
        return true;
    }

    private boolean checkClient(Request clientRequest) {
        User clientUser = clientRequest.getUser();
        if (clientUser == null) return false;
        if (clientUser.getLogin() == null || clientUser.getPassword() == null) return false;
        return database.checkUser(clientUser);
    }

    private boolean isContainsIdentificationString(String identificationString) {
        try {
            return notConfirmingRequests.stream().anyMatch(request -> request.getIdentificationString().equals(identificationString));
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void removeNotConfirmedRequestByString(String identificationString) {
        NotConfirmedRequest elem = null;
        for (NotConfirmedRequest request : notConfirmingRequests) {
            if (request.getIdentificationString().equals(identificationString)) elem = request;
        }

        if (elem != null) notConfirmingRequests.remove(elem);
    }

    class Resender implements Runnable {
        private final DatagramSocket socket;

        public Resender(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (!notConfirmingRequests.isEmpty()) {
                deleteExpiredRequest();
                reduceRequestCounters();
                resendResponses();
            }
        }

        private void deleteExpiredRequest() {
            List<NotConfirmedRequest> expiredRequests = new ArrayList<>();
            for (NotConfirmedRequest request : notConfirmingRequests) {
                if (request.getCounter() <= 0) expiredRequests.add(request);
            }

            for (NotConfirmedRequest request : expiredRequests) {
                notConfirmingRequests.remove(request);
            }
        }

        private void reduceRequestCounters() {
            for (NotConfirmedRequest request : notConfirmingRequests) {
                request.setCounter(request.getCounter() - 1); // Reducing the counter
            }
        }

        private void resendResponses() {
            for (NotConfirmedRequest request : notConfirmingRequests) {
                try {
                    int counter = request.getCounter();
                    if (counter > 0)
                        Server.logger.info("Response to client with identification string: '" + request.getIdentificationString() + "' wasn't confirmed. Resending...");
                    else
                        Server.logger.info("Response to client with identification string: '" + request.getIdentificationString() + "' wasn't confirmed. Resending stopped");
                    socket.send(request.getPacketToClient());
                } catch (IOException e) {
                    Server.logger.info("Client died");
                }
            }
        }
    }

    static class NotConfirmedRequest {
        private final String identificationString;
        private final DatagramPacket packetToClient;
        private int counter;

        public NotConfirmedRequest(String identificationString, DatagramPacket packetToClient) {
            this.identificationString = identificationString;
            this.packetToClient = packetToClient;
            this.counter = 3;
        }

        public String getIdentificationString() {
            return identificationString;
        }

        public DatagramPacket getPacketToClient() {
            return packetToClient;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NotConfirmedRequest that = (NotConfirmedRequest) o;
            return counter == that.counter && identificationString.equals(that.identificationString) && packetToClient.equals(that.packetToClient);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identificationString, counter, packetToClient);
        }
    }
}

