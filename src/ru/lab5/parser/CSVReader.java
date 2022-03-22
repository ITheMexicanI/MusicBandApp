package ru.lab5.parser;

import ru.lab5.mainObjects.MusicBandCollection;
import ru.lab5.parser.excetions.FileException;
import ru.lab5.parser.excetions.InvalidHeaderException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ЗАНИМАЕТСЯ ЧТЕНИЕМ CSV ФАЙЛА
 */
public class CSVReader {
    private static final List<String> primeHeaders = Arrays.asList("name,cor_x,cor_y,num_of_participants,date,genre,album_name,album_tracks".split(","));
    private DataInputStream dis;
    private List<String> headers;
    private List<CSVObject> objects = new ArrayList<>();
    private MusicBandCollection collection;

    public CSVReader(MusicBandCollection collection) {
        this.collection = collection;
    }

    /**
     * АТКРЫВАЕТ ФАЙЛ И ИНИЦИАЛИЗИРУЕТ НУЖНЫЕ ДЛЯ ЕГО ПРОЧТЕНИЯ ШТУКИ
     * @param fileName ИМЕЧКО ФАЙЛА
     */
    public void readCSVFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists() || !file.canRead()) {
            throw new FileException();
        }

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        dis = null;

        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            readHeaders();
            readArguments();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(fis).close();
                Objects.requireNonNull(bis).close();
                Objects.requireNonNull(dis).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        serializeMusicBand(objects);
    }

    /**
     * ПЫТАЕТСЯ СОЗДАТЬ ИЗ РАНДОМНЫХ ПОЛЕЙ МУЗЫЧКУ
     * @param objects CSV АБЪЕКТЫ С РАНДОМНЫМИ ПОЛЯМИ
     */
    private void serializeMusicBand(List<CSVObject> objects) {
        MusicBandCreator creator = new MusicBandCreator(collection);
        creator.createMusicBandStack(objects);
    }

    /**
     * ЧИТАЕТ ЗАГОЛОВКИ CSV ФАЙЛА И СВЕРЯЕТ ИХ  ДАННЫМИ
     * @throws IOException МОЖЕТ ВЫВАЛИЦА ПРИ ОШИБКАХ ВВОДА
     */
    private void readHeaders() throws IOException {
        headers = Arrays.asList(dis.readLine().split(","));
        for (String header: headers) {
            if (!primeHeaders.contains(header)) {
                throw new InvalidHeaderException();
            }
        }
    }

    /**
     * ЧИТАЕТ АРГУМЕНТЫ И ЗАПОМИНАЕТ ИХ В ЛИСТ
     * @throws IOException МОЖЕТ ВЫВАЛИЦА ПРИ ОШИБКАХ ВВОДА
     */
    private void readArguments() throws IOException {
        while (dis.available() != 0) {
            List<String> arguments = Arrays.asList(dis.readLine().split(",", -1));
            CSVObject object = createCSVObject(arguments);
            objects.add(object);
        }

    }

    /**
     * @param arguments ПОЛЯ CSV файла
     * @return ВОЗВРАЗАЕТ CSV АБЪЕКТ, ХРАНЯЩИЙ ПОЛЯ ИЗ ФАЙЛА
     */
    private CSVObject createCSVObject(List<String> arguments) {
        return new CSVObject(headers, arguments);
    }

    /**
     * @return ВОЗВРАЩАЕТ ЗАГОЛОВКИ CSV ФАЙЛА
     */
    public static String getPrimeCSVHeaders() {
        return String.join(",", primeHeaders);
    }
}
