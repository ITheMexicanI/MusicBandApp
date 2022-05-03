package ru.lab.client;

import ru.lab.common.commands.inputSystem.ConsoleCommandInput;
import ru.lab.common.utils.Serializator;
import ru.lab.common.utils.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class    Client {
    public static void main(String[] args) {
        try {
            // Connect to channel
            DatagramChannel channel = DatagramChannelBuilder.bindChannel(null);
            channel.configureBlocking(false);

            // Connect to server
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", Serializator.PORT);
            CommandValidator validator = new CommandValidator(new ConsoleCommandInput());
            RequestSender sender = new RequestSender(validator, serverAddress, channel, new User());
            sender.sendHelp();

            sender.read();
        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу");
        }
    }
}
