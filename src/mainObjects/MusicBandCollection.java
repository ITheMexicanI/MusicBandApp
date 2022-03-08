package mainObjects;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Stack;

/**
 * КЛАСС, ХРАНЯЩИЙ И УПРАВЛЯЮЩИЙ МУЗЫЧКОЙ
 */
public class MusicBandCollection {
    private Stack<MusicBand> musicBandStack = new Stack<>();
    private java.time.LocalDate initializingDate;

    /**
     * КОНСТРУКТОР МУЗЫЧКИ ИНИЦИАЛИЗИРУЕТ ДАТУ ЗАГРУЗКИ
     */
    public MusicBandCollection() {
        initializingDate = LocalDate.now();
    }


    /**
     * @return ВОЗВРАЩАЕТ КОЛИЧЕСТВО МУЗЫЧКИ
     */
    public int getMusicsCount() {
        return musicBandStack.size();
    }

    /**
     * ДОБАВЛЯЕТ МУЗЫЧКУ В СТЭК С МУЗЫЧОЙ
     * @param musicBand САМА МУЗЫЧКА
     */
    public void addMusicBand(MusicBand musicBand) {
        musicBandStack.add(musicBand);
    }


    /**
     * ВЫВОДИТ ИНФУ О МУЗЫЧКЕ
     */
    public void getInfo() {
        System.out.println("Тип: " + this.getCollection().getClass().getSimpleName() +
                "\nДата инициализации: " + initializingDate.toString() +
                "\nКоличество элементов: " + this.getMusicsCount());
    }

    /**
     * @return ВОЗВРАЩАЕТ ВСЮ МУЗЫЧКУ
     */
    public Stack<MusicBand> getCollection() {
        return musicBandStack;
    }

    /**
     * УДАЛЯЕТ ВСЮ МУЗЫЧКУ
     */
    public void clearCollection() {
        musicBandStack = new Stack<>();
    }

    /**
     * ВЫВОДИТ НА ЭКРАН ВСЮ МУЗЫЧКУ
     */
    public void showCollection() {
        musicBandStack.forEach(System.out::println);
    }

    /**
     * УДАЛЯЕТ МУЗЫЧКУ ПО ДАННОМУ ID
     * @param id ID ЭЛЕМЕНТА
     */
    public void removeById(long id) {
        musicBandStack.removeIf(element -> element.getId() == id);
    }

    /**
     * ПЕРЕМЕШИВАЕТ МУЗЫЧКУ
     */
    public void shuffle() {
        Collections.shuffle(musicBandStack);
    }

    /**
     * @param element - МУЗЫЧКА
     * @param position - ПОЗИЦИЯ НА КОТОРУЮ ПЫТАЕМСЯ ДОБАВИТЬ МУЗЫЧКУ
     */
    public void insertElement(MusicBand element, int position) {
        musicBandStack.insertElementAt(element, position);
    }

    /**
     * @param id ID ЭЛЕМЕНТА
     * @return ВОЗВРАЩАЕТ МУЗЫЧКУ ПО ДАННОМУ АЙДИ
     */
    public MusicBand getElementByID(long id) {
        for (MusicBand element : musicBandStack) {
            if (element.getId() == id) return element;
        }
        return null;
    }
}

