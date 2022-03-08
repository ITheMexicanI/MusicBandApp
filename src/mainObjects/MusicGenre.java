package mainObjects;

import java.util.Arrays;
import java.util.List;

/**
 * ЕНАМ ТИПОВ МУЗЫЧКИ
 */
public enum MusicGenre {
    HIP_HOP("Hip hop"),
    PSYCHEDELIC_CLOUD_RAP("Rap"),
    POST_ROCK("Post rock"),
    PUNK_ROCK("Punk rock"),
    BRIT_POP("Brit pop");
    private final String name;

    /**
     * @param name ИМЯ ТИПА МУЗЫЧКИ
     */
    MusicGenre(String name) {
        this.name = name;
    }

    public static MusicGenre getGenreByName(String name) {
        switch (name) {
            case "Hip hop":
                return HIP_HOP;
            case "Rap":
                return PSYCHEDELIC_CLOUD_RAP;
            case "Post rock":
                return POST_ROCK;
            case "Punk rock":
                return PUNK_ROCK;
            case "Brit pop":
                return BRIT_POP;
            default:
                return null;
        }
    }

    /**
     * @return ВОЗВРАЩАЕТ ВСЕВОЗМОЖНЫЕ ТИПЫ МУЗЫЧКИ
     */
    public static List<MusicGenre> getAllGenres() {
        return Arrays.asList(HIP_HOP, PSYCHEDELIC_CLOUD_RAP, POST_ROCK, PUNK_ROCK, BRIT_POP);
    }

    /**
     * @return ВОЗВРАЩАЕТ ИМЯ МУЗЫЧКИ
     */
    @Override
    public String toString() {
        return name;
    }
}