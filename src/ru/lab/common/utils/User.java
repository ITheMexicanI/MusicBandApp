package ru.lab.common.utils;

import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return
                "login: '" + login + '\'' +
                ", password: '" + password + '\'';
    }
}
