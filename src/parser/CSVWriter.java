package parser;

import mainObjects.MusicBand;
import mainObjects.MusicBandCollection;

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
     */
    public void writeCSVFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        write(fileName);
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