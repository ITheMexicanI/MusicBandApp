package ru.lab.common.utils;

import ru.lab.common.commands.Command;

import java.io.Serializable;

public class Request implements Serializable {
    private Command command;
    private Object argument;
    private User user;

    public Request(Command command, Object argument, User user) {
        this.command = command;
        this.argument = argument;
        this.user = user;
    }

    public Command getCommand() {
        return command;
    }

    public Object getArgument() {
        return argument;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Request {" +
                "command: '" + command +
                "', argument: '" + argument +
                "'}";
    }
}
