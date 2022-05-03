package ru.lab.server;

import ru.lab.common.commands.Command;
import ru.lab.common.mainObjects.Album;
import ru.lab.common.mainObjects.MusicBand;
import ru.lab.common.mainObjects.MusicBandCollection;
import ru.lab.common.utils.Mark;
import ru.lab.common.utils.Response;
import ru.lab.common.utils.User;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * ЧЕЛ, КОТОРЫЙ УМЕЕТ ИСПОЛНЯТЬ КОМАНДЫ И ДРУЖИТ С КОЛЛЕКЦИЕЙ
 */
public class CommandExecutor {
    private final ReentrantLock lock = new ReentrantLock();
    private MusicBandCollection collection;
    private final DataBaseHelper database;

    public CommandExecutor(MusicBandCollection collection, DataBaseHelper database) {
        this.collection = collection;
        this.database = database;
    }

    public Response executeCommand(Command command, Object commandArgs, User user) {
        Response response = null;
        lock.lock();
        try {
            switch (command) {
                case REG:
                    response = reg((User) commandArgs);
                    break;
                case LOG:
                    response = log((User) commandArgs);
                    break;


                case HELP:
                    response = help();
                    break;
                case INFO:
                    response = info();
                    break;
                case SHOW:
                    response = show();
                    break;
                case ADD:
                    response = add((MusicBand) commandArgs, user);
                    break;
                case UPDATE_ID:
                    response = updateId((AbstractMap.SimpleEntry<Integer, MusicBand>) commandArgs, user);
                    break;
                case REMOVE_BY_ID:
                    response = removeId((int) commandArgs, user);
                    break;
                case CLEAR:
                    response = clear(user);
                    break;
                case EXECUTE:
                    response = new Response("", "Скрипт был выполнен", Mark.STRING);
                    break;
                case SHUFFLE:
                    response = shuffle();
                    break;
                case SORT:
                    response = sort();
                    break;
                case REORDER:
                    response = reorder();
                    break;
                case SHOW_BY_ALBUM:
                    response = showByBestAlbum((String) commandArgs);
                    break;
                case SHOW_GREATER_THAN_ALBUM:
                    response = showGreaterThanAlbum((Album) commandArgs);
                    break;
                case SHOW_NUM_OF_PARTICIPANTS:
                    response = showNumberOfParticipants();
                    break;
            }
        } finally {
            lock.unlock();
        }

        return response;
    }

    private Response help() {
        return new Response("Справка:", "Возможные команды (Для авторизованных пользователей): \n" +
                "'help'                  : вывести справку по доступным командам\n" +
                "'info'                  : вывести информацию о коллекции\n" +
                "'show'                  : вывести все элементы коллекции\n" +
                "'add'                   : добавить новый элемент в коллекцию\n" +
                "'updateId X'            : обновить значение элемента коллекции (X - id элемента, которое нужно заменить)\n" +
                "'removeId X'            : удалить элемент из коллекции по его id (X - id элемента, которое нужно заменить)\n" +
                "'clear'                 : очистить коллекцию\n" +
                "'execute PATH'          : считать и исполнить скрипт из указанного файла.(PATH - путь до файла-скрипта)\n" +
                "'exit'                  : завершить программу (без сохранения в файл)\n" +
                "'insert X'              : добавить новый элемент в заданную позицию (X - позиция)\n" +
                "'shuffle'               : перемешать элaddементы коллекции в случайном порядке\n" +
                "'reorder'               : отсортировать коллекцию в порядке\n" +
                "'showByAlbum'           : вывести элементы, значение поля bestAlbum которых равно заданному\n" +
                "'showGreaterThanAlbum'  : вывести элементы, значение поля bestAlbum которых больше заданного\n" +
                "'showNumOfParticipants' : вывести значения поля numberOfParticipants всех элементов в порядке возрастания\n\n" +
                "'reg'                   : выйти из аккаунта и зарегестрировать новый\n" +
                "'log'                   : выйти из аккаунта и войти в другой" + "\n\n" +
                "Возможные команды (Для неавторизованных пользователей): \n" +
                "'help'                  : вывести справку по доступным командам\n" +
                "'reg'                   : зарегестрировать новый аккаунт\n" +
                "'log'                   : войти в аккаунт", Mark.STRING);
    }

    private Response info() {
        return new Response("Информация о коллекиции:", collection.getInfo(), Mark.STRING);
    }

    private Response show() {
        return new Response("Коллекция:", collection.getCollection(), Mark.STACK);
    }

    private Response add(MusicBand element, User user) {
        try {
            int id = database.getIdSeq();

            element.setId(id);
            element.setCreationDate(new Date());
            collection.addMusicBand(element);
            database.add(element, id, user);
        } catch (SQLException e) {
            Server.logger.info("Database work error");
            e.printStackTrace();
        }
        return new Response("", "Элемент добавлен в коллекцию", Mark.STRING);
    }

    private Response updateId(AbstractMap.SimpleEntry<Integer, MusicBand> commandArgs, User user) {
        try {
            int id = commandArgs.getKey();
            MusicBandCollection newCol = database.updateById(id, commandArgs.getValue(), user);

            if (!newCol.equals(collection)) {
                collection = database.load();
                return new Response("Измененный элемент:", collection.getElementByID(id).toString(), Mark.STRING);
            }
        } catch (SQLException e) {
            Server.logger.info("Database work error");
            e.printStackTrace();
        }
        return new Response("Элемента с таким id нет или элемент вам не принадлежит.", "Возрат в главное меню...", Mark.STRING);
    }

    private Response removeId(int id, User user) {
        try {
            MusicBandCollection newCol = database.removeById(id, user);
            if (!newCol.equals(collection)) {
                String msg = collection.getElementByID(id).toString();
                collection = database.load();
                return new Response("Элемент, который был удален:", msg, Mark.STRING);
            }
        } catch (SQLException e) {
            Server.logger.info("Database work error");
            e.printStackTrace();
        }
        return new Response("Элемента с таким id нет или элемент вам не принадлежит.", "Возрат в главное меню...", Mark.STRING);
    }

    private Response clear(User user) {
        try {
            database.clear(user);
            collection = database.load();
        } catch (SQLException e) {
            Server.logger.info("Database work error");
            e.printStackTrace();
        }

        return new Response("Коллекция очищена от элементов, принадлежащих вам.", "Возрат в главное меню...", Mark.STRING);
    }

    private Response shuffle() {
        collection.shuffle();
        return new Response("Коллекция перемешана", "Возрат в главное меню...", Mark.STRING);
    }

    private Response sort() {
        Collections.sort(collection.getCollection());
        return new Response("Коллекция остртирована по размеру объектов в ней", "Возрат в главное меню...", Mark.STRING);
    }

    private Response reorder() {
        Collections.reverse(collection.getCollection());
        return new Response("Обратная сортировка произведена", "Возрат в главное меню...", Mark.STRING);
    }

    private Response showByBestAlbum(String albumName) {
        return new Response("Результат:", collection.getCollection().stream().filter(element -> element.getBestAlbum().getName().equals(albumName)).collect(Collectors.toList()), Mark.LIST);
    }

    private Response showGreaterThanAlbum(Album album) {
        return new Response("Результат:", collection.getCollection().stream().filter(element -> element.getBestAlbum().compareTo(album) > 0).collect(Collectors.toList()), Mark.LIST);
    }

    private Response showNumberOfParticipants() {
        return new Response("Общее количество участников:", collection.getCollection().stream().map(MusicBand::getNumberOfParticipants).sorted().collect(Collectors.toList()), Mark.LIST);
    }

    private Response reg(User user) {
        try {
            boolean isContains = database.isContainsUserByLogin(user);

            if (!isContains) {
                database.addUser(user);
                return new Response("Вы успешно зарегестрировались >.<", user, Mark.USER);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Response("Упс, пользователь с таким именем уже существует, возврат в главное меню >.<", null, Mark.USER);
    }

    private Response log(User user) {
        boolean isContains = database.checkUser(user);

        if (isContains) {
            return new Response("Вы успешно вошли >.<", user, Mark.USER);
        }

        return new Response("Неправильный логин или пароль, возврат в главное меню >.<", null, Mark.USER);
    }
}
