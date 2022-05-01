package ru.lab.server.parser;

import ru.lab.common.mainObjects.*;
import ru.lab.server.parser.excetions.InvalidFieldValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ЗАНИМАЕТСЯ СОЗДАНИЕМ И ПОВЕРКОЙ НА ПРАВИЛЬНОСТЬ ЗАПОЛНЕНИЯ ПОЛЕЙ МУЗЫЧКИ
 */
public class MusicBandCreator {
    private final MusicBandCollection collection;

    /**
     * @param collection - СТЭК ХРАНЯЩИЙ МУЗЫЧКУ
     */
    public MusicBandCreator(MusicBandCollection collection) {
        this.collection = collection;
    }

    /**
     * ПРАБГАЕТ ПО CSV АБЪЕКТАМ И СТРОИТ ПО НИМ МУЗЫЧКУ
     *
     * @param csvObjects ХРАНИТ СЫРЫЕ CSV АБЪЕКТЫ
     */
    protected void createMusicBandStack(List<CSVObject> csvObjects) {
        long id;
        String psevId;

        String name;

        int xCor;
        int yCor;
        Coordinates coordinates;

        Date creationDate;

        long numberOfParticipants;

        LocalDate establishmentDate;

        MusicGenre genre;

        String albumName;
        int albumTracks;
        Album album;

        for (CSVObject obj : csvObjects) {
            Map<String, String> fields = obj.getFields();

            psevId = fields.get("id");
            try {
                id = Long.parseLong(psevId);
                if (id <= 0) id *= (-1);
            } catch (NumberFormatException e) {
                id = collection.getMinId() + 1;
            }
            while (collection.getIds().contains(id)) id++;

            name = fields.get("name");
            checkNonNullString(name);

            try {
                xCor = Integer.parseInt(fields.get("cor_x"));
                yCor = Integer.parseInt(fields.get("cor_y"));
                coordinates = new Coordinates(xCor, yCor);
            } catch (NumberFormatException e) {
                throw new InvalidFieldValue("Coords must be numbers");
            }

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            try {
                creationDate = format.parse(fields.get("creation_date"));
            } catch (ParseException e) {
                String dateString = format.format(new Date());
                try {
                    creationDate = format.parse(dateString);
                } catch (ParseException ex) {
                    creationDate = null;
                    ex.printStackTrace();
                }
            }

            numberOfParticipants = Long.parseLong(fields.get("num_of_participants"));
            checkMoreThanZero(numberOfParticipants);

            establishmentDate = LocalDate.parse(fields.get("date"));
            checkNonNullString(establishmentDate.toString());

            genre = MusicGenre.getGenreByName(fields.get("genre"));
            checkGenre(genre);

            try {
                albumName = fields.get("album_name");
                albumTracks = Integer.parseInt(fields.get("album_tracks"));
                checkNonNullString(albumName);
                checkMoreThanZero(albumTracks);
                album = new Album(albumName, albumTracks);
            } catch (NumberFormatException e) {
                throw new InvalidFieldValue("Number of album tracks must be number: " + fields.get("album_tracks"));
            }

            collection.addMusicBand(new MusicBand(id, name, coordinates, creationDate, numberOfParticipants, establishmentDate, genre, album));
        }
    }

    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАНА НЕ ПУСТАЯ СТРОКА ТК ЭТО null ПО УСЛОВИЮ
     *
     * @param string ПРОСТО СТРОКА
     */
    private void checkNonNullString(String string) {
        if (string.equals("")) throw new InvalidFieldValue("Some field is null");
    }


    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАННОЕ ЧЕСЛО БОЛЬШЕ НОЛЯ
     *
     * @param num ПРОСТО ЧЕСЛО
     */
    private void checkMoreThanZero(long num) {
        if (num < 0) throw new InvalidFieldValue("Some field less than zero");
    }

    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАННЫЙ ЖАНР СУЩЕСТВУЕТ
     *
     * @param genre ЕНАМ ЖАНРОВ
     */
    private void checkGenre(MusicGenre genre) {
        if (genre == null) throw new InvalidFieldValue("Genre is incorrect");
    }
}
