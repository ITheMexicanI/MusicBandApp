package ru.lab.common.utils;

import java.io.Serializable;

public class Response implements Serializable {
    private final String title;
    private final Object message;
    private final Mark mark;

    public Response(String title, Object message, Mark mark) {
        this.title = title;
        this.message = message;
        this.mark = mark;
    }

    public String getTitle() {
        return title;
    }

    public Object getMessage() {
        return message;
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public String toString() {
        return "Response: " +
                "title='" + title + '\'' +
                ", message=" + message +
                '}';
    }
}
