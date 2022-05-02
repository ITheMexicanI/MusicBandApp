package ru.lab.common.commands;

import ru.lab.client.Client;
import ru.lab.common.commands.inputSystem.CommandInput;
import ru.lab.common.commands.inputSystem.FileCommandInput;
import ru.lab.common.mainObjects.*;
import ru.lab.common.utils.Request;
import ru.lab.common.utils.User;
import sun.security.provider.SHA;

import java.io.*;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;


/**
 * ЧЕЛ, КОТОРЫЙ УМЕЕТ ИСПОЛНЯТЬ КОМАНДЫ И ДРУЖИТ С КОЛЛЕКЦИЕЙ
 */
public class CommandValidator {
    private final CommandInput reader;

    /**
     * @param reader ЧИТАТЕЛЬ ЛИБО КОНСОЛЬНЫЙ, ЛИБО ФАЙЛОВЫЙ
     */
    public CommandValidator(CommandInput reader) {
        this.reader = reader;
    }


    protected Request log(User user) {
        System.out.println("Вход в аккаунт...");

        System.out.println("Введите логин:");
        String login = inputNonNullString();
        System.out.println("Введите пароль:");
        String password = null;
        try {
            password = encryptPassword(inputNonNullString());
        } catch (NoSuchAlgorithmException ignored) {
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        return new Request(Command.LOG, newUser, user);
    }

    protected Request reg(User user) {
        System.out.println("Регистрация нового аккаунта");

        System.out.println("Введите логин:");
        String login = inputNonNullString();
        String password;

        while (true) {
            System.out.println("Введите пароль:");
            password = inputNonNullString();
            System.out.println("Повторите пароль:");

            if (!password.equals(inputNonNullString())) System.out.println("Пароли не совпадают, повторите снова");
            else break;
        }

        try {
            password = encryptPassword(password);
        } catch (NoSuchAlgorithmException ignored) {
        }

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        return new Request(Command.REG, newUser, user);
    }

    private String encryptPassword(final String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(password.getBytes());
        byte byteBuffer[] = messageDigest.digest();
        StringBuffer strHexString = new StringBuffer();

        for (int i = 0; i < byteBuffer.length; i++) {
            String hex = Integer.toHexString(0xff & byteBuffer[i]);
            if (hex.length() == 1) {
                strHexString.append('0');
            }
            strHexString.append(hex);
        }
        return strHexString.toString();
    }

    /**
     * СПРАВКА
     */
    protected Request help(User user) {
        return new Request(Command.HELP, null, user);
    }

    /**
     * ВЫВОДИТ ИНФУ О КОЛЛЕКЦИИ
     *
     * @param user
     */
    protected Request info(User user) {
        return new Request(Command.INFO, null, user);
    }

    /**
     * ПОКАЗЫВАЕТ КОЛЛЕКЦИЮ
     *
     * @param user
     */
    protected Request show(User user) {
        return new Request(Command.SHOW, null, user);
    }


    /**
     * ДОБАВЛЯЕТ НОВЫЙ ЭЛЕМЕНТ В КОЛЛЕКЦИЮ
     *
     * @param user
     */
    protected Request add(User user) {
        System.out.println("Добавление нового элемента в коллекцию:");
        return new Request(Command.ADD, createMusicBand(), user);
    }

    /**
     * МЕНЯЕТ ДАННЫЙ ЭЛЕМЕНТ
     *
     * @param commandArgs - АЙДИ ЭЛЕМЕНТА
     * @param user
     */
    protected Request updateId(String commandArgs, User user) {
        try {
            int id = Integer.parseInt(commandArgs);
            System.out.println("Изменение элемента коллекции:");
            return new Request(Command.UPDATE_ID, new AbstractMap.SimpleEntry<>(id, createMusicBand()), user);
        } catch (NumberFormatException e) {
            System.out.println("Аргумент должен быть числом, возврат в главное меню...");
            return null;
        }
    }

    /**
     * УДАЛЯЕТ ЭЛЕМЕНТ ПО ДАННОМУ АЙДИ
     *
     * @param commandArgs АЙДИ ЭЛЕМЕНТА
     * @param user
     */
    protected Request removeId(String commandArgs, User user) {
        try {
            int id = Integer.parseInt(commandArgs);
            return new Request(Command.REMOVE_BY_ID, id, user);
        } catch (NumberFormatException e) {
            System.out.println("Аргумент должен быть числом, возврат в главное меню...");
            return null;
        }
    }

    /**
     * ОЧИЩАЕТ КОЛЛЕКЦИЮ
     *
     * @param user
     */
    protected Request clear(User user) {
        return new Request(Command.CLEAR, null, user);
    }

    /**
     * ИСПОЛНЯЕТ СКРИПТ
     *
     * @param commandArgs ПУТЬ ДО СКРИПТА
     * @param user
     */
    protected Request executeScript(String commandArgs, DatagramChannel channel, Client client, SocketAddress serverAddress, User user) {
        File file = new File(commandArgs);

        if (commandArgs.isEmpty()) {
            System.out.println("Путь должен быть корректным, попробуйте снова");
            commandArgs = inputNonNullString();
            file = new File(commandArgs);
        }

        try {
            CommandInput commandInput = new FileCommandInput(file);
            CommandReader commandReader = new CommandReader();
            commandReader.setValidator(new CommandValidator(commandInput));
            commandReader.setChannel(channel);
            commandReader.setClient(client);
            commandReader.setServerAddress(serverAddress);
            commandReader.read(commandInput);
            return new Request(Command.EXECUTE, null, user);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден или к нему нет доступа");
        }
        return null;
    }

    /**
     * ВЫХОДИТ ИЗ ПОРОГРАММЫ
     */
    protected void exit() {
        System.out.println("Закрытие программы...");
        System.exit(0);
    }

    /**
     * МЕШАЕТ КОЛЛЕКЦИЮ
     *
     * @param user
     */
    protected Request shuffle(User user) {
        return new Request(Command.SHUFFLE, null, user);
    }

    /**
     * СОРТИРУЕТ КОЛЛЕКЦИЮ ПО УМОЛЧАНИЮ
     *
     * @param user
     */
    protected Request sort(User user) {
        return new Request(Command.SORT, null, user);
    }

    /**
     * РАЗВОРАЧИВАЕТ КОЛЛЕКЦИЮ
     *
     * @param user
     */
    protected Request reorder(User user) {
        return new Request(Command.REORDER, null, user);
    }

    /**
     * ПОКАЗЫВАЕТ СОВПАДЕНИЯ С ДАННЫМ АЛЬБОМОМ
     *
     * @param user
     */
    protected Request showByBestAlbum(User user) {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        return new Request(Command.SHOW_BY_ALBUM, albumName, user);
    }

    /**
     * ПОКАЗЫВАЕТ САМЫЕ КРУТЫЕ АЛЬБОМЫ
     *
     * @param user
     */
    protected Request showGreaterThanAlbum(User user) {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        System.out.println("Введите количество треков в албоме");
        int tracks = (int) inputPositiveLong();
        Album album = new Album(albumName, tracks);
        System.out.println("Результат:");
        return new Request(Command.SHOW_GREATER_THAN_ALBUM, album, user);
    }

    /**
     * КОМАНДА, ПО ВЫВОДУ ОТСОРТИРОВАННЫХ УЧАСТНИКОВ ГРУППЫ
     *
     * @param user
     */
    protected Request showNumberOfParticipants(User user) {
        return new Request(Command.SHOW_NUM_OF_PARTICIPANTS, null, user);
    }


    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ СТРОКИ
     *
     * @return ВОЗВРАЩАЕТ СТРОКУ
     */
    private String inputNonNullString() {
        while (true) {
            String input = reader.readLine();
            if (!input.equals("")) return input;
            System.out.println("Строка не может быть пустой, попробуйте снова");
        }
    }

    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ ЧИСЛА
     *
     * @return ВОЗВРАЩАЕТ ЧИСЛО
     */
    private int inputInt() {
        while (true) {
            try {
                return Integer.parseInt(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число:");
            }
        }
    }

    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ ЧИСЛА
     *
     * @return ВОЗВРАЩАЕТ ЧИСЛО
     */
    private long inputPositiveLong() {
        while (true) {
            try {
                long input = Long.parseLong(reader.readLine());
                if (input > 0) return input;
                System.out.println("Число должно быть больше 0, попробуйте снова:");
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число:");
            }
        }
    }

    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ ДАТЫ
     *
     * @return ВОЗВРАЩАЕТ ДАТУ
     */
    private String inputDate() {
        while (true) {
            String input = reader.readLine();
            if (Pattern.matches("^-?\\+?(\\d{1,4})[-](\\+?0[1-9]|\\+?1[0-2])[-](\\+?[0-2]?[1-9]|\\+?[1-3][0-1])$", input)) {
                try {
                    LocalDate.parse(input);
                    return input;
                } catch (Exception ignored) {

                }
            }
            System.out.println("Неправильный формат даты, введите дату в формате yyyy-mm-dd:");
        }
    }

    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ ЖАНРА
     *
     * @return ВОЗВРАЩАЕТ ЖАНР
     */
    private MusicGenre inputGenre() {
        while (true) {
            MusicGenre input = MusicGenre.getGenreByName(reader.readLine());
            if (input != null) return input;
            System.out.println("Такого жанра нет, вы можете выбрать один и следующих жанров:");
            MusicGenre.getAllGenres().forEach(System.out::println);
            System.out.println("Введите нужный жанр:");
        }
    }

    /**
     * СОЗДАЕТ НОВУЮ МУЗЫЧКУ
     *
     * @return ВОЗВРАЩАЕТ НОВУЮ МУЗЫЧКУ
     */
    private MusicBand createMusicBand() {
        // name of album
        System.out.println("Введите имя альбома");
        String name = inputNonNullString();

        // coordinates
        System.out.println("Введите значение координаты по oX:");
        int corX = inputInt();
        System.out.println("Введите значение координаты по oY:");
        int corY = inputInt();
        Coordinates coordinates = new Coordinates(corX, corY);

        // number of participants
        System.out.println("Введите колчество участников:");
        long numberOfParticipants = inputPositiveLong();

        // date
        System.out.println("Введите дату создания группы в формате yyyy-mm-dd:");
        LocalDate date = LocalDate.parse(inputDate());

        // music genre
        System.out.println("Выберите жанр: ");
        System.out.println("Жанры на выбор:");
        MusicGenre.getAllGenres().forEach(System.out::println);
        System.out.println("Введите нужный жанр:");
        MusicGenre genre = inputGenre();

        // best album
        System.out.println("Введите название лучшего альбома группы:");
        String albumName = inputNonNullString();
        System.out.println("Введите количество треков в альбоме:");
        int tracks = (int) inputPositiveLong();
        Album album = new Album(albumName, tracks);

        return new MusicBand(-1, name, coordinates, new Date(), numberOfParticipants, date, genre, album);
    }
}

