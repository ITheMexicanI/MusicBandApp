package commands;

import commands.inputSystem.CommandInput;
import commands.inputSystem.FileCommandInput;
import mainObjects.*;
import parser.CSVWriter;
import parser.excetions.FileException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


/**
 * ЧЕЛ, КОТОРЫЙ УМЕЕТ ИСПОЛНЯТЬ КОМАНДЫ И ДРУЖИТ С КОЛЛЕКЦИЕЙ
 */
public class CommandExecutor {
    private MusicBandCollection collection;
    private CommandInput reader;

    /**
     * @param collection - МУЗЫЧКА
     * @param reader - ЧИТАТЕЛЬ ЛИБО КОНСОЛЬНЫЙ, ЛИБО ФАЙЛОВЫЙ
     */
    public CommandExecutor(MusicBandCollection collection, CommandInput reader) {
        this.collection = collection;
        this.reader = reader;
    }

    /**
     * СПРАВКА
     */
    protected void help() {
        System.out.println("Справка:");
        System.out.println("Возможные команды: \n" +
                "'help'                  : вывести справку по доступным командам\n" +
                "'info'                  : вывести информацию о коллекции\n" +
                "'show'                  : вывести все элементы коллекции\n" +
                "'add'                   : добавить новый элемент в коллекцию\n" +
                "'updateId X'            : обновить значение элемента коллекции (X - id элемента, которое нужно заменить)\n" +
                "'removeId X'            : удалить элемент из коллекции по его id (X - id элемента, которое нужно заменить)\n" +
                "'clear'                 : очистить коллекцию\n" +
                "'save PATH'             : сохранить коллекцию в файл(PATH - путь до файла, в который нужно сохранить коллекцию)\n" +
                "'execute PATH'          : считать и исполнить скрипт из указанного файла.(PATH - путь до файла-скрипта)\n" +
                "'exit'                  : завершить программу (без сохранения в файл)\n" +
                "'insert X'              : добавить новый элемент в заданную позицию (X - позиция)\n" +
                "'shuffle'               : перемешать элементы коллекции в случайном порядке\n" +
                "'reorder'               : отсортировать коллекцию в порядке\n" +
                "'showByAlbum'           : вывести элементы, значение поля bestAlbum которых равно заданному\n" +
                "'showGreaterThanAlbum'  : вывести элементы, значение поля bestAlbum которых больше заданного\n" +
                "'showNumOfParticipants' : вывести значения поля numberOfParticipants всех элементов в порядке возрастания");
    }

    /**
     * ВЫВОДИТ ИНФУ О КОЛЛЕКЦИИ
     */
    protected void info() {
        collection.getInfo();
    }

    /**
     * ПОКАЗЫВАЕТ КОЛЛЕКЦИЮ
     */
    protected void show() {
        collection.showCollection();
    }


    /**
     * ДОБАВЛЯЕТ НОВЫЙ ЭЛЕМЕНТ В КОЛЛЕКЦИЮ
     */
    protected void add() {
        long id = collection.getMusicsCount() + 1;
        System.out.println("Добавление нового элемента в коллекцию:");
        collection.addMusicBand(createMusicBand(id, new Date()));
        System.out.println("Элемент добавлен в колекцию");
    }

    /**
     * МЕНЯЕТ ДАННЫЙ ЭЛЕМЕНТ
     * @param commandArgs - АЙДИ ЭЛЕМЕНТА
     */
    protected void updateId(String commandArgs) {
        long id = Long.parseLong(commandArgs);
        MusicBand element = collection.getElementByID(id);
        if (element != null) {
            System.out.println("Элемент, который будем менять:");
            System.out.println(element);
            System.out.println("Изменение элемента коллекции:");
            element.updateElement(createMusicBand(id, element.getCreationDate()));
        } else {
            System.out.println("Элемента с таким id нет.");
            System.out.println("Возврат в главное меню...");
        }
    }

    /**
     * УДАЛЯЕТ ЭЛЕМЕНТ ПО ДАННОМУ АЙДИ
     * @param commandArgs АЙДИ ЭЛЕМЕНТА
     */
    protected void removeId(String commandArgs) {
        long id = Long.parseLong(commandArgs);
        MusicBand element = collection.getElementByID(id);
        if (element != null) {
            System.out.println("Данный элемент был удален:");
            System.out.println(element);
            collection.removeById(id);
        } else {
            System.out.println("Элемента с таким id нет.");
            System.out.println("Возврат в главное меню...");
        }
    }

    /**
     * ОЧИЩАЕТ КОЛЛЕКЦИЮ
     */
    protected void clear() {
        collection.clearCollection();
    }

    /**
     * СОХРАНЯЕТ КОЛЛЕКЦИЮ В CSV ФАЙЛ
     * @param commandArgs - ПУТЬ ДО ФАЙЛА
     */
    protected void save(String commandArgs) {
        try {
            CSVWriter writer = new CSVWriter(collection);
            writer.writeCSVFile(commandArgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ИСПОЛЬНЯЕТ СКРИПТ
     * @param commandArgs ПУТЬ ДО СКРИПТА
     */
    protected void executeScript(String commandArgs) {
        File file = new File(commandArgs);

        if (file.exists() && file.canRead()) {
            CommandInput reader = new FileCommandInput(new File(commandArgs));
            CommandReader commandReader = new CommandReader(new CommandExecutor(collection, reader));
            commandReader.read(reader);
        } else {
            throw new FileException();
        }
    }

    /**
     * ВЫХОДИТ ИЗ ПОРОГРАММЫ
     */
    protected void exit() {
        System.out.println("Зыкрытие программы...");
        System.exit(0);
    }

    /**
     * ДОБАВЛЯЕТ ЭЛЕМЕНТ НА ДАННУЮ ПОЗИЦИЮ
     * @param commandArgs АРГУМЕНТ КОМАНДЫ, В ДАННОМ СЛУЧАЕ ПОЗИЦИЯ
     */
    protected void insertAtIndex(String commandArgs) {
        int pos = Integer.parseInt(commandArgs);
        if (pos < 0 || pos > collection.getCollection().size()) {
            System.out.println("Введенная позиция находится вне коллекции, поэтому добавление произойдет в конец.");
            add();
        } else {
            MusicBand element = createMusicBand(collection.getMusicsCount() + 1, new Date());
            collection.insertElement(element, pos);
        }
        Collections.sort(collection.getCollection());

    }

    /**
     * МЕШАЕТ КОЛЛЕКЦИЮ
     */
    protected void shuffle() {
        collection.shuffle();
    }

    /**
     * СОРТИРУЕТ КОЛЛЕКЦИЮ ПО УМОЛЧАНИЮ
     */
    protected void sort() {
        Collections.sort(collection.getCollection());
    }

    /**
     * РАЗВОРАЧИВАЕТ КОЛЛЕКЦИЮ
     */
    protected void reorder() {
        Collections.reverse(collection.getCollection());
    }

    /**
     * ПОКАЗЫВАЕТ СОВПАДЕНИЯ С ДАННЫМ АЛЬБОМОМ
     */
    protected void showByBestAlbum() {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        System.out.println("Результат:");
        for (MusicBand element : collection.getCollection()) {
            if (element.getBestAlbum().getName().equals(albumName)) System.out.println(element);
        }
    }

    /**
     * ПОКАЗЫВАЕТ САМЫЕ КРУТЫЕ АЛЬБОМЫ
     */
    protected void showGreaterThanAlbum() {
        System.out.println("Введите название альбома, по которому будет произведен фильтр:");
        String albumName = inputNonNullString();
        System.out.println("Введите количество треков в албоме");
        int tracks = (int) inputPositiveLong();
        Album album = new Album(albumName, tracks);
        System.out.println("Результат:");
        for (MusicBand element : collection.getCollection()) {
            if (element.getBestAlbum().compareTo(album) > 0) System.out.println(element);
        }
    }

    /**
     * КОМАНДА, ПО ВЫВОДУ ОТСОРТИРОВАННЫХ УЧАСТНИКОВ ГРУППЫ
     */
    protected void showNumberOfParticipants() {
        List<Long> participants = new ArrayList<>();
        for (MusicBand element : collection.getCollection()) {
            participants.add(element.getNumberOfParticipants());
        }

        Collections.sort(participants);
        participants.forEach(System.out::println);
    }


    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ СТРОКИ
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
     * @return ВОЗВРАЩАЕТ ДАТУ
     */
    private String inputDate() {
        while (true) {
            String input = reader.readLine();
            if (Pattern.matches("^-?\\+?(\\d{1,4})[-](\\+?0[1-9]|\\+?1[0-2])[-](\\+?[0-2]?[1-9]|\\+?[1-3][0-1])$", input))
                return input;
            System.out.println("Неправильный формат даты, введите дату в формате yyyy-mm-dd:");
        }
    }

    /**
     * ПРОВЕРЯЕТ КОРРЕКТНОСТЬ ЖАНРА
     * @return ВОЗВРАЩАЕТ ЖАНР
     */
    private MusicGenre inputGenre() {
        while (true) {
            MusicGenre input = MusicGenre.getGenreByName(reader.readLine());
            if (input != null) return input;
            System.out.println("Такого жанра нет, вы можете выбрать один и следующих жанров:");
            MusicGenre.getAllGenres().forEach(System.out::println);
            System.out.println("Введие нужный жанр:");
        }
    }

    /**
     * СОЗДАЕТ НОВУЮ МУЗЫЧКУ
     * @param id АЙДИ МУЗЫЧКИ
     * @param creationDate ДАТА ИНИЦИАЛИЗАЦИИ МУЗЫЧКИ
     * @return ВОЗВРАЩАЕТ НОВУЮ МУЗЫЧКУ
     */
    private MusicBand createMusicBand(long id, Date creationDate) {
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
        System.out.println("Введие нужный жанр:");
        MusicGenre genre = inputGenre();

        // best album
        System.out.println("Введите название лучшего альбома группы:");
        String albumName = inputNonNullString();
        System.out.println("Введите количество треков в альбоме:");
        int tracks = (int) inputPositiveLong();
        Album album = new Album(albumName, tracks);

        return new MusicBand(id, name, coordinates, creationDate, numberOfParticipants, date, genre, album);
    }
}

