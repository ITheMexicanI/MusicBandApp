package commands.inputSystem;

import java.io.*;

/**
 * ЧИТАТЕЛЬ ИЗ ФАЙЛА
 */
public class FileCommandInput implements CommandInput {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataInputStream dis = null;

    /**
     * ИНИЦИАЛИЗИРУЕТ ВСЕ, ЧТОБЫ ПРОЧИТАТЬ ИЗ ФАЙЛА
     * @param file - ПУТЬ ДО ФАЙЛА
     */
    public FileCommandInput(File file) {
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println("Файл не найден");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
