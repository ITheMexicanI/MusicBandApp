package ru.lab5.common.network;

import java.io.Serializable;

public class Response implements Serializable {
    private String title;
    private Object message;
    private Mark mark;

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
