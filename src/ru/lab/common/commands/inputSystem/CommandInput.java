package ru.lab.common.commands.inputSystem;

/**
 * ПРОСТО ГОВОРИТ ЧТО ПАРНИ, РЕАЛИЗОВЫВАЮЩИЕ ЭТОТ ИНТЕРФЕЙС ДОЛЖНЫ УМЕТЬ ЧИТАТЬ
 */
public interface CommandInput {
    /**
     * @return ДОЛЖЕН ВЕРНУТЬ ПРОЧТЕННУЮ СТРОКУ
     */
    String readLine();
}
