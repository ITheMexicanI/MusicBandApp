package ru.lab5.parser;

import ru.lab5.mainObjects.MusicBand;
import ru.lab5.mainObjects.MusicBandCollection;
import ru.lab5.parser.excetions.FileException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

/**
 * ЗАНИМАЕТСЯ ЗАПИСЬЮ В CSV ФАЙЛ
 */
public class CSVWriter {
    private MusicBandCollection collection;

    /**
     * @param collection - СТЭК ХРАНЯЩИЙ МУЗЫЧКУ
     */
    public CSVWriter(MusicBandCollection collection) {
        this.collection = collection;
    }

    /**
     * МЕТОД, КОТОРЫЙ СОЗДАЕТ ФАЙЛ ДЛЯ МУЗЫЧКИ
     * @param fileName ИМЕЧКО ФАЙЛА
     * @throws IOException МОЖЕТ ВЫВАЛИЦА ПРИ ОШИБКАХ ВВОДА
     * @throws FileException МОЖЕТ ВЫВАЛИЦА ПРИ ОШИБАХ ПРИ ЧТЕНИИ ФАЙЛА
     */
    public void writeCSVFile(String fileName) throws IOException {
        File file = new File(fileName);
        try {
            file.createNewFile();
            write(fileName);
        } catch (Exception e) {
            throw new FileException();
        }
    }

    /**
     * ЗАПИСЫВАЕТ МУЗЫЧКУ В CSV ФАЙЛ
     * @param fileName ИМЕЧКО ФАЙЛА
     * @throws IOException МОЖЕТ ВЫВАЛИЦА ПРИ ОШИБКАХ ВВОДА
     */
    private void write(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        Stack<MusicBand> musicBandStack = collection.getCollection();
        String headers = CSVReader.getPrimeCSVHeaders();

        writer.write(headers + "\n");

        for (MusicBand element : musicBandStack) {
            String string = element.getCSVString();
            writer.write(string + "\n");
        }

        writer.close();
    }
}
