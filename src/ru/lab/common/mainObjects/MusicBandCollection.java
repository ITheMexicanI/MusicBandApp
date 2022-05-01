package ru.lab.common.mainObjects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * КЛАСС, ХРАНЯЩИЙ И УПРАВЛЯЮЩИЙ МУЗЫЧКОЙ
 */
public class MusicBandCollection {
    private Stack<MusicBand> musicBandStack = new Stack<>();
    private final List<Long> ids = new ArrayList<>();
    private final java.time.LocalDate initializingDate;

    /**
     * КОНСТРУКТОР МУЗЫЧКИ ИНИЦИАЛИЗИРУЕТ ДАТУ ЗАГРУЗКИ
     */
    public MusicBandCollection() {
        initializingDate = LocalDate.now();
    }


    /**
     * @return ВОЗВРАЩАЕТ КОЛИЧЕСТВО МУЗЫЧКИ
     */
    public int getMusicsCount() {
        return musicBandStack.size();
    }

    public List<Long> getIds() {
        return ids;
    }

    /**
     * ДОБАВЛЯЕТ МУЗЫЧКУ В СТЭК С МУЗЫЧОЙ
     * @param musicBand САМА МУЗЫЧКА
     */
    public void addMusicBand(MusicBand musicBand) {
        musicBandStack.add(musicBand);
        ids.add(musicBand.getId());
    }


    /**
     * ВЫВОДИТ ИНФУ О МУЗЫЧКЕ
     */
    public String getInfo() {
        return "Тип: " + this.getCollection().getClass().getSimpleName() +
                "\nДата инициализации: " + initializingDate.toString() +
                "\nКоличество элементов: " + this.getMusicsCount();
    }

    /**
     * @return ВОЗВРАЩАЕТ ВСЮ МУЗЫЧКУ
     */
    public Stack<MusicBand> getCollection() {
        return musicBandStack;
    }

    /**
     * УДАЛЯЕТ ВСЮ МУЗЫЧКУ
     */
    public void clearCollection() {
        musicBandStack = new Stack<>();
    }


    /**
     * УДАЛЯЕТ МУЗЫЧКУ ПО ДАННОМУ ID
     * @param id ID ЭЛЕМЕНТА
     */
    public void removeById(long id) {
        int size = getCollection().size();
        musicBandStack.removeIf(element -> element.getId() == id);
        if (size > getCollection().size()) getIds().remove(id);
    }

    /**
     * ПЕРЕМЕШИВАЕТ МУЗЫЧКУ
     */
    public void shuffle() {
        Collections.shuffle(musicBandStack);
    }

    /**
     * @param element - МУЗЫЧКА
     * @param position - ПОЗИЦИЯ НА КОТОРУЮ ПЫТАЕМСЯ ДОБАВИТЬ МУЗЫЧКУ
     */
    public void insertElement(MusicBand element, int position) {
        musicBandStack.insertElementAt(element, position);
    }

    /**
     * @param id ID ЭЛЕМЕНТА
     * @return ВОЗВРАЩАЕТ МУЗЫЧКУ ПО ДАННОМУ АЙДИ
     */
    public MusicBand getElementByID(long id) {
        for (MusicBand element : musicBandStack) {
            if (element.getId() == id) return element;
        }
        return null;
    }

    public long getMinId() {
        Collections.sort(getIds());
        return getIds().get(0);
    }
}

