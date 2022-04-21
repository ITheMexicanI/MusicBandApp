package ru.lab5.common.commands;

import ru.lab5.client.Client;
import ru.lab5.common.commands.inputSystem.CommandInput;
import ru.lab5.common.commands.inputSystem.FileCommandInput;
import ru.lab5.common.mainObjects.*;
import ru.lab5.common.utils.Request;

import java.io.*;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
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

    /**
     * СПРАВКА
     */
    protected Request help() {
        return new Request(Command.HELP, null);
    }

    /**
     * ВЫВОДИТ ИНФУ О КОЛЛЕКЦИИ
     */
    protected Request info() {
        return new Request(Command.INFO, null);
    }

    /**
     * ПОКАЗЫВАЕТ КОЛЛЕКЦИЮ
     */
    protected Request show() {
        return new Request(Command.SHOW, null);
    }


    /**
     * ДОБАВЛЯЕТ НОВЫЙ ЭЛЕМЕНТ В КОЛЛЕКЦИЮ
     */
    protected Request add() {
        System.out.println("Добавление нового элемента в коллекцию:");
        return new Request(Command.ADD, createMusicBand());
    }

    /**
     * МЕНЯЕТ ДАННЫЙ ЭЛЕМЕНТ
     *
     * @param commandArgs - АЙДИ ЭЛЕМЕНТА
     */
    protected Request updateId(String commandArgs) {
        try {
            long id = Long.parseLong(commandArgs);
            System.out.println("Изменение элемента коллекции:");
            return new Request(Command.UPDATE_ID, new AbstractMap.SimpleEntry<>(id, createMusicBand()));
        } catch (NumberFormatException e) {
            System.out.println("Аргумент должен быть числом, возврат в главное меню...");
            return null;
        }
    }

    /**
     * УДАЛЯЕТ ЭЛЕМЕНТ ПО ДАННОМУ АЙДИ
     *
     * @param commandArgs АЙДИ ЭЛЕМЕНТА
     */
    protected Request removeId(String commandArgs) {
        try {
            long id = Long.parseLong(commandArgs);
            return new Request(Command.REMOVE_BY_ID, id);
        } catch (NumberFormatException e) {
            System.out.println("Аргумент должен быть числом, возврат в главное меню...");
            return null;
        }
    }

    /**
     * ОЧИЩАЕТ КОЛЛЕКЦИЮ
     */
    protected Request clear() {
        return new Request(Command.CLEAR, null);
    }

    /**
     * ИСПОЛНЯЕТ СКРИПТ
     *
     * @param commandArgs ПУТЬ ДО СКРИПТА
     */
    protected Request executeScript(String commandArgs, DatagramChannel channel, Client client, SocketAddress serverAddress) {
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
     * ДОБАВЛЯЕТ ЭЛЕМЕНТ НА ДАННУЮ ПОЗИЦИЮ
     *
     * @param commandArgs АРГУМЕНТ КОМАНДЫ, В ДАННОМ СЛУЧАЕ ПОЗИЦИЯ
     */
    protected Request insertAtIndex(String commandArgs) {
        try {
            int pos = Integer.parseInt(commandArgs);
            System.out.println("Добавление нового элемента в коллекцию:");
            return new Request(Command.INSERT_AT, new AbstractMap.SimpleEntry<>(pos, createMusicBand()));
        } catch (NumberFormatException e) {
            System.out.println("Аргумент должен быть числом, возврат в главное меню...");
            return null;
        }

    }

    /**
     * МЕШАЕТ КОЛЛЕКЦИЮ
     */
    protected Request shuffle() {
        return new Request(Command.SHUFFLE, null);
    }

    /**
     * СОРТИРУЕТ КОЛЛЕКЦИЮ ПО УМОЛЧАНИЮ
     */
    protected Request sort() {
        return new Request(Command.SORT, null);
    }

    /**
     * РАЗВОРАЧИВАЕТ КОЛЛЕКЦИЮ
     */
    protected Request reorder() {
        return new Request(Command.REORDER, null);
    }

    /**
     * ПОКАЗЫВАЕТ СОВПАДЕНИЯ С ДАННЫМ АЛЬБОМОМ
     */
    protected Request showByBestAlbum() {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        return new Request(Command.SHOW_BY_ALBUM, albumName);
    }

    /**
     * ПОКАЗЫВАЕТ САМЫЕ КРУТЫЕ АЛЬБОМЫ
     */
    protected Request showGreaterThanAlbum() {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        System.out.println("Введите количество треков в албоме");
        int tracks = (int) inputPositiveLong();
        Album album = new Album(albumName, tracks);
        System.out.println("Результат:");
        return new Request(Command.SHOW_GREATER_THAN_ALBUM, album);
    }

    /**
     * КОМАНДА, ПО ВЫВОДУ ОТСОРТИРОВАННЫХ УЧАСТНИКОВ ГРУППЫ
     */
    protected Request showNumberOfParticipants() {
        return new Request(Command.SHOW_NUM_OF_PARTICIPANTS, null);
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
                } catch (Exception e) {

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

