package ru.lab.common.commands;

import java.io.Serializable;

/**
 * ВСЕВОЗМОЖНЫЕ КОМАНЫ
 */
public enum Command implements Serializable {
    REG("reg"),
    LOG("log"),

    HELP("help"),
    INFO("info"),
    SHOW("show"),
    ADD("add"),
    UPDATE_ID("updateId"),
    REMOVE_BY_ID("removeId"),
    CLEAR("clear"),
    EXECUTE("execute"),
    EXIT("exit"),
    SHUFFLE("shuffle"),
    REORDER("reorder"),
    SORT("sort"),
    SHOW_BY_ALBUM("showByAlbum"),
    SHOW_GREATER_THAN_ALBUM("showGreaterThanAlbum"),
    SHOW_NUM_OF_PARTICIPANTS("showNumOfParticipants");

    private final String name;

    /**
     * @param name ИМЕЧКО КОМАНДЫ
     */
    Command(String name) {
        this.name = name;
    }


    public static Command getCommandByName(String name) {
        switch (name) {
            case "reg":
                return REG;
            case "log":
                return LOG;

            case "help":
                return HELP;
            case "info":
                return INFO;
            case "show":
                return SHOW;
            case "add":
                return ADD;
            case "updateId":
                return UPDATE_ID;
            case "removeId":
                return REMOVE_BY_ID;
            case "clear":
                return CLEAR;
            case "execute":
                return EXECUTE;
            case "exit":
                return EXIT;
            case "shuffle":
                return SHUFFLE;
            case "sort":
                return SORT;
            case "reorder":
                return REORDER;
            case "showByAlbum":
                return SHOW_BY_ALBUM;
            case "showGreaterThanAlbum":
                return SHOW_GREATER_THAN_ALBUM;
            case "showNumOfParticipants":
                return SHOW_NUM_OF_PARTICIPANTS;

            default:
                return null;
        }
    }

    /**
     * @return ВОЗВРАЩАЕТ ИМЕЧКО КОМАНДЫ
     */
    @Override
    public String toString() {
        return name;
    }
}
