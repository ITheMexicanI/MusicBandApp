package ru.lab.common.commands;

import ru.lab.common.mainObjects.*;
import ru.lab.common.utils.Mark;
import ru.lab.common.utils.Response;
import ru.lab.common.utils.User;
import ru.lab.server.database.DataBaseHelper;

import java.util.*;
import java.util.stream.Collectors;


/**
 * ЧЕЛ, КОТОРЫЙ УМЕЕТ ИСПОЛНЯТЬ КОМАНДЫ И ДРУЖИТ С КОЛЛЕКЦИЕЙ
 */
public class CommandExecutor {
    private final MusicBandCollection collection;
    private final DataBaseHelper database;

    public CommandExecutor(MusicBandCollection collection, DataBaseHelper database) {
        this.collection = collection;
        this.database = database;
    }

    protected Response help() {
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
                "'shuffle'               : перемешать элементы коллекции в случайном порядке\n" +
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

    protected Response info() {
        return new Response("Информация о коллекиции:", collection.getInfo(), Mark.STRING);
    }

    protected Response show() {
        return new Response("Коллекция:", collection.getCollection(), Mark.STACK);
    }

    protected Response add(MusicBand element) {
        long id = collection.getMusicsCount() + 1;
        while (collection.getIds().contains(id)) id++;

        collection.addMusicBand(element);
        element.setId(id);
        element.setCreationDate(new Date());

        return new Response("", "Элемент добавлен в коллекцию", Mark.STRING);
    }

    protected Response updateId(AbstractMap.SimpleEntry<Long, MusicBand> commandArgs) {
        long id = commandArgs.getKey();
        MusicBand element = collection.getElementByID(id);

        if (element != null) {
            element.updateElement(commandArgs.getValue());
            return new Response("Измененный элемент:", element.toString(), Mark.STRING);
        } else {
            return new Response("Элемента с таким id нет.", "Возрат в главное меню...", Mark.STRING);
        }
    }

    protected Response removeId(long id) {
        MusicBand element = collection.getElementByID(id);
        if (element != null) {
            collection.removeById(id);
            collection.getIds().remove(id);
            return new Response("Элемент, который был удален:", element.toString(), Mark.STRING);
        } else {
            return new Response("Элемента с таким id нет.", "Возрат в главное меню...", Mark.STRING);
        }
    }

    protected Response clear() {
        collection.clearCollection();
        collection.getIds().clear();
        return new Response("Коллекция очищена", "Возрат в главное меню...", Mark.STRING);
    }

    protected Response insertAtIndex(AbstractMap.SimpleEntry<Integer, MusicBand> commandArgs) {
        int pos = commandArgs.getKey();
        MusicBand element = commandArgs.getValue();

        long id = collection.getMusicsCount() + 1;
        while (collection.getIds().contains(id)) id++;

        element.setId(id);
        element.setCreationDate(new Date());

        if (pos < 0 || pos > collection.getCollection().size()) {
            collection.addMusicBand(element);
            return new Response("Введенная позиция находится вне коллекции, поэтому добавление произойдет в конец.", "Возрат в главное меню...", Mark.STRING);
        } else {
            collection.insertElement(element, pos);
            return new Response("Элемент добавлен в коллекцию.", "Возрат в главное меню...", Mark.STRING);
        }
    }

    protected Response shuffle() {
        collection.shuffle();
        return new Response("Коллекция перемешана", "Возрат в главное меню...", Mark.STRING);
    }

    protected Response sort() {
        Collections.sort(collection.getCollection());
        return new Response("Коллекция остртирована по размеру объектов в ней", "Возрат в главное меню...", Mark.STRING);
    }

    protected Response reorder() {
        Collections.reverse(collection.getCollection());
        return new Response("Обратная сортировка произведена", "Возрат в главное меню...", Mark.STRING);
    }

    protected Response showByBestAlbum(String albumName) {
        return new Response("Результат:", collection.getCollection().stream().filter(element -> element.getBestAlbum().getName().equals(albumName)).collect(Collectors.toList()), Mark.LIST);
    }

    protected Response showGreaterThanAlbum(Album album) {
        return new Response("Результат:", collection.getCollection().stream().filter(element -> element.getBestAlbum().compareTo(album) > 0).collect(Collectors.toList()), Mark.LIST);
    }

    protected Response showNumberOfParticipants() {
        return new Response("Общее количество участников:", collection.getCollection().stream().map(MusicBand::getNumberOfParticipants).sorted().collect(Collectors.toList()), Mark.LIST);
    }

    protected Response reg(User user) {
        boolean isContains = database.isContainsUserByLogin(user);

        if (!isContains) {
            database.addUser(user);
            return new Response("Вы успешно зарегестрировались >.<", user, Mark.USER);
        }

        return new Response("Упс, пользователь с таким именем уже существует, возврат в главное меню >.<", null, Mark.USER);
    }

    protected Response log(User user) {
        boolean isContains = database.checkUser(user);

        if (isContains) {
            return new Response("Вы успешно вошли >.<", user, Mark.USER);
        }

        return new Response("Неправильный логин или пароль, возврат в главное меню >.<", null, Mark.USER);
    }
}

