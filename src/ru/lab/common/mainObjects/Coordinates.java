package ru.lab.common.mainObjects;

import java.io.Serializable;

/**
 * КЛАСС КООРДИНАТ МУЗЫЧКИ
 */
public class Coordinates implements Serializable {
    private final int x;
    private final int y;

    /**
     * @param x X КООРДИНАТА
     * @param y Y КООРДИНАТА
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return ГЕТТЕР ДЛЯ X
     */
    public int getX() {
        return x;
    }

    /**
     * @return ГЕТТЕР ДЛЯ Y
     */
    public int getY() {
        return y;
    }

    /**
     * @return ВОЗВРАЩАЕТ КООДИНАТЫ
     */
    @Override
    public String toString() {
        return  "x: " + x +
                ", y: " + y;
    }
}
