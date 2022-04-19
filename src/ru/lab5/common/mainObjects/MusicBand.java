package ru.lab5.common.mainObjects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * КЛАСС, ХРАНЯЩИЙ В СЕБЕ БАЗОВЫЙ ЭЛЕМЕНТ МУЗЫЧКИ
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long numberOfParticipants; //Значение поля должно быть больше 0
    private java.time.LocalDate establishmentDate; //Поле не может быть null
    private MusicGenre genre; //Поле может быть null
    private Album bestAlbum; //Поле не может быть null

    /**
     * @param id - АЙДИ МУЗЫЧКИ
     * @param name - НАЗВАНИЕ МУЗЫЧКИ
     * @param coordinates - КООРДИНАТЫ МУЗЫЧКИ
     * @param creationDate - ДАТА ДОБАВЛЕНИЯ МУЗЫЧКИ
     * @param numberOfParticipants = КЛОЛИЧЕСТВО УЧАСТНИКОВ
     * @param establishmentDate -ДАТА СОЗДАНИЯ
     * @param genre - ЖАНР МУЗЫЧКИ
     * @param bestAlbum - ЛУЧШИЙ АЛЬБОМ
     */
    public MusicBand(long id, String name, Coordinates coordinates, java.util.Date creationDate, long numberOfParticipants, java.time.LocalDate establishmentDate, MusicGenre genre, Album bestAlbum) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
    }

    /**
     * ОБНОВЛЯЕТ ПОЛНОСТЬЮ МУЗЫЧКУ
     * @param o ДРУГАЯ МУЗЫЧКА
     */
    public void updateElement(MusicBand o) {
        this.name = o.getName();
        this.coordinates = o.getCoordinates();
        this.numberOfParticipants = o.getNumberOfParticipants();
        this.establishmentDate = o.getEstablishmentDate();
        this.genre = o.getGenre();
        this.bestAlbum = o.getBestAlbum();
    }

    /**
     * @return ВОЗВРАЩАЕТ ИМЯ МУЗЫЧКИ
     */
    public String getName() {
        return name;
    }

    /**
     * @return ВОЗВРАЩАЕТ ID
     */
    public long getId() {
        return id;
    }

    /**
     * @return ВОЗВРАЩАЕТ ДАТУ ДОБАВЛЕНИЯ МУЗЫЧКИ
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @return ВОЗВРАЩАЕТ ЛУЧШИЙ АЛЬБОМ
     */
    public Album getBestAlbum() {
        return bestAlbum;
    }

    /**
     * @return ВОЗВРАЩАЕТ КОЛИЧЕСТВО УЧАСТНИКОВ ГРУППЫ
     */
    public long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    /**
     * @return ВОЗВРАЩАЕТ КООРДИНАТЫ МУЗЫЧКИ
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return ВОЗВРАЩАЕТ ДАТУ СОЗДАНИЕ ГРУППЫ
     */
    public LocalDate getEstablishmentDate() {
        return establishmentDate;
    }

    /**
     * @return ВОЗВРАЩАЕТ ЖАНР МУЗЫЧКИ
     */
    public MusicGenre getGenre() {
        return genre;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * СРАВНИВАЕТ МУЗЫЧКИ ПО ИМЕНИ, СОРТИРОВКА ПО УМОЛЧАНИЮ
     * @param o - ДРУГАЯ МУЗЫЧКА
     * @return ВЗВРАЩАЕТ КАКОЕ ИМЕЧКО КРУЧЕ
     */
    @Override
    public int compareTo(MusicBand o) {
        return this.getName().compareTo(o.getName()); // Сортировка по умолчанию
    }

    /**
     * @return КРАСИВО ПЕЧАТАЕТ МУЗЫЧКУ В КОНСОЛЬ
     */
    @Override
    public String toString() {
        return  "id: " + id +
                ", имя: " + name +
                ", координаты: " + coordinates +
                ", дата добавления: " + new SimpleDateFormat("dd-MM-yyyy").format(creationDate) +
                ", участники: " + numberOfParticipants +
                ", дата создания группы: " + establishmentDate.toString() +
                ", жанр: " + genre.toString() +
                ", " + bestAlbum;
    }

    /**
     * @return ВОЗВРАЩАЕТ СТРОКУ ВИДА CSV ФАЙЛА
     */
    public String getCSVString() {
        return  id + "," +
                name + "," +
                coordinates.getX() + "," +
                coordinates.getY() + "," +
                new SimpleDateFormat("dd-MM-yyyy").format(creationDate) + "," +
                numberOfParticipants + "," +
                establishmentDate.toString() + "," +
                genre.toString() + "," +
                bestAlbum.getName() + "," + bestAlbum.getTracks();
    }
}