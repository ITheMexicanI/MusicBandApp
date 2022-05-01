package ru.lab.common.commands.inputSystem;

import java.io.*;

/**
 * ЧИТАТЕЛЬ ИЗ ФАЙЛА
 */
public class FileCommandInput implements CommandInput {
    FileInputStream fis;
    BufferedInputStream bis;
    DataInputStream dis;

    /**
     * ИНИЦИАЛИЗИРУЕТ ВСЕ, ЧТОБЫ ПРОЧИТАТЬ ИЗ ФАЙЛА
     * @param file - ПУТЬ ДО ФАЙЛА
     */
    public FileCommandInput(File file) throws FileNotFoundException {
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * ЧИТАЕТ ИЗ ФАЙЛА 1 СТРОКУ
     * @return ВОЗВРАЩАЕТ ПРОЧТЕННУЮ СТРОКУ
     */
    @Override
    public String readLine() {
        try {
            if (dis.available() != 0) return dis.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
