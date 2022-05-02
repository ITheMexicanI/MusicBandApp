package ru.lab.common.mainObjects;

import java.time.LocalDate;
import java.util.*;

/**
 * КЛАСС, ХРАНЯЩИЙ И УПРАВЛЯЮЩИЙ МУЗЫЧКОЙ
 */
public class MusicBandCollection {
    private final Stack<MusicBand> musicBandStack = new Stack<>();
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

    /**
     * ДОБАВЛЯЕТ МУЗЫЧКУ В СТЭК С МУЗЫЧОЙ
     * @param musicBand САМА МУЗЫЧКА
     */
    public void addMusicBand(MusicBand musicBand) {
        musicBandStack.add(musicBand);
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
     * ПЕРЕМЕШИВАЕТ МУЗЫЧКУ
     */
    public void shuffle() {
        Collections.shuffle(musicBandStack);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return musicBandStack.equals(((MusicBandCollection) o).getCollection());
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicBandStack, initializingDate);
    }
}

