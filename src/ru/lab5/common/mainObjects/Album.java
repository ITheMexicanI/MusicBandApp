package ru.lab5.common.mainObjects;

import java.io.Serializable;

/**
 * КЛАСС, ХРАНЯЩИЙ В СЕБЕ ИНФОРМАЦИЮ ОБ АЛЬБОМЕ МКУЗЫЧКИ
 */
public class Album implements Comparable<Album>, Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private int tracks; //Значение поля должно быть больше 0

    /**
     * @param name ИМЕЧКО АЛЬБОМА
     * @param tracks КОЛИЧЕСТВО ТРЕКОВ В АЛЬБОМЕ
     */
    public Album(String name, int tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    /**
     * @return ГЕТТЕР ИМЕНИ
     */
    public String getName() {
        return name;
    }

    /**
     * @return ГЕТТЕР КОЛИЧЕСТВА ТРЭКОВ
     */
    public int getTracks() {
        return tracks;
    }

    /**
     * @return ВОЗВРАЩАЕТ КРАСИВУЮ СТРОЧКУ
     */
    @Override
    public String toString() {
        return  "назавание альбома: " + name +
                ", количество треков: " + tracks;
    }

    /**
     * @param o ДРУГОЙ АЛЬБОМ
     * @return ВОЗВРАЩАЕТ САМЫЙ КРУТОЙ АЛЬБОМ
     */
    @Override
    public int compareTo(Album o) {
        if (name.equals(o.getName())) return tracks - o.getTracks();
        return name.compareTo(o.getName());
    }
}