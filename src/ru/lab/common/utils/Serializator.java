package ru.lab.common.utils;

import ru.lab.common.commands.CommandReader;

import java.io.*;

public class Serializator {
    public static ByteArrayOutputStream serialize(Object request) {
        ObjectOutputStream oos;
        ByteArrayOutputStream bos;
        try {
            bos = new ByteArrayOutputStream(CommandReader.MAX_SIZE);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(request);
            oos.close();
            return bos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object deserialize(byte[] request) {
        ObjectInputStream ois;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(request, 0, request.length);
            ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            ois.close();
            return object;
        } catch (EOFException e) {
            System.out.println("Не достаточно памяти для передачи объекта");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка десериализации, класс не найден или сервер недоступен");
            System.exit(0);
            return null;
        }
    }
}
