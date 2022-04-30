package ru.lab5.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCHelper {
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "11435211";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lab";


    private Statement statement;

    public JDBCHelper() {
        initializeConnection();
    }

    public void printRequestToDB() {
        try {
            String request = "SELECT * FROM users ORDER BY login";
            ResultSet result = statement.executeQuery(request); // Объект, хранящий ответ от БД

            while (result.next()) {
                System.out.println(result.getInt("id") + " " + result.getString("login"));
            }
        } catch (SQLException e) {
            System.err.println("Database connection error");
        }
    }

    private void initializeConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            statement = connection.createStatement(); // Объект, отправляющий запосы в БД
        } catch (SQLException e) {
            System.err.println("Database connection error");
        }
    }
}
