package ru.lab5.common.network;

import ru.lab5.common.commands.Command;

import java.io.Serializable;

public class Request implements Serializable {
    private Command command;
    private Object argument;

    public Request(Command command, Object argument) {
        this.command = command;
        this.argument = argument;
    }

    public Command getCommand() {
        return command;
    }

    public Object getArgument() {
        return argument;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setArgument(Object argument) {
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "Request {" +
                "command: '" + command +
                "', argument: '" + argument +
                "'}";
    }
}
