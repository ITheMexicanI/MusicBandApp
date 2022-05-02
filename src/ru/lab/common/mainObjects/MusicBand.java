package ru.lab.common.mainObjects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * КЛАСС, ХРАНЯЩИЙ В СЕБЕ БАЗОВЫЙ ЭЛЕМЕНТ МУЗЫЧКИ
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final long numberOfParticipants; //Значение поля должно быть больше 0
    private final java.time.LocalDate establishmentDate; //Поле не может быть null
    private final MusicGenre genre; //Поле может быть null
    private final Album bestAlbum; //Поле не может быть null

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

    public Date getCreationDate() {
        return creationDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBand musicBand = (MusicBand) o;
        return id == musicBand.id && numberOfParticipants == musicBand.numberOfParticipants && Objects.equals(name, musicBand.name) && Objects.equals(coordinates, musicBand.coordinates) && Objects.equals(creationDate, musicBand.creationDate) && Objects.equals(establishmentDate, musicBand.establishmentDate) && genre == musicBand.genre && Objects.equals(bestAlbum, musicBand.bestAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, numberOfParticipants, establishmentDate, genre, bestAlbum);
    }
}