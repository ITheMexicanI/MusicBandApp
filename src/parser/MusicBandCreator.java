package parser;

import mainObjects.*;
import parser.excetions.InvalidFieldValue;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * ЗАНИМАЕТСЯ СОЗДАНИЕМ И ПОВЕРКОЙ НА ПРАВИЛЬНОСТЬ ЗАПОЛНЕНИЯ ПОЛЕЙ МУЗЫЧКИ
 */
public class MusicBandCreator {
    private MusicBandCollection collection;

    /**
     * @param collection - СТЭК ХРАНЯЩИЙ МУЗЫЧКУ
     */
    public MusicBandCreator(MusicBandCollection collection) {
        this.collection = collection;
    }

    /**
     * ПРАБГАЕТ ПО CSV АБЪЕКТАМ И СТРОИТ ПО НИМ МУЗЫЧКУ
     * @param csvObjects ХРАНИТ СЫРЫЕ CSV АБЪЕКТЫ
     */
    protected void createMusicBandStack(List<CSVObject> csvObjects) {
        Stack<MusicBand> stack = new Stack<>();
        long id;

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
            id = collection.getMusicsCount() + 1;

            name = fields.get("name");
            checkNonNullString(name);

            try {
                xCor = Integer.parseInt(fields.get("cor_x"));
                yCor = Integer.parseInt(fields.get("cor_y"));
                coordinates = new Coordinates(xCor, yCor);
            } catch (NumberFormatException e) {
                throw new InvalidFieldValue("Coords must be numbers");
            }

            creationDate = new Date();

            numberOfParticipants = Long.parseLong(fields.get("num_of_participants"));
            checkMoreThanZero(numberOfParticipants);

            establishmentDate = LocalDate.parse(fields.get("date"));
            checkNonNullString(establishmentDate.toString());

            genre = MusicGenre.getGenreByName(fields.get("genre"));
            checkGenre(genre);

            albumName = fields.get("album_name");
            albumTracks = Integer.parseInt(fields.get("album_tracks"));
            checkNonNullString(albumName);
            checkMoreThanZero((long) albumTracks);
            album = new Album(albumName, albumTracks);

            collection.addMusicBand(new MusicBand(id, name, coordinates, creationDate, numberOfParticipants, establishmentDate, genre, album));
        }
    }

    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАНА НЕ ПУСТАЯ СТРОКА ТК ЭТО null ПО УСЛОВИЮ
     * @param string ПРОСТО СТРОКА
     */
    private void checkNonNullString(String string) {
        if (string.equals("")) throw new InvalidFieldValue("Some field is null");
    }


    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАННОЕ ЧЕСЛО БОЛЬШЕ НОЛЯ
     * @param num  ПРОСТО ЧЕСЛО
     */
    private void checkMoreThanZero(long num) {
        if (num < 0) throw new InvalidFieldValue("Some field less than zero");
    }

    /**
     * ПРОВЕРЯЕТ ЧТО ПЕРЕДАННЫЙ ЖАНР СУЩЕСТВУЕТ
     * @param genre ЕНАМ ЖАНРОВ
     */
    private void checkGenre(MusicGenre genre) {
        if (genre == null) throw new InvalidFieldValue("Genre is incorrect");
    }
}