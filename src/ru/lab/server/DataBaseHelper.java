package ru.lab.server;

import ru.lab.common.mainObjects.*;
import ru.lab.common.utils.User;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DataBaseHelper {
    private Connection connection;
    private PreparedStatement prepareStatement;
    private Statement statement;

    public DataBaseHelper(String[] args) {
        initializeConnection(args);
    }

    public boolean checkUser(User user) {
        try {
            String request = "SELECT * FROM users WHERE login='" + user.getLogin() + "' AND password='" + user.getPassword() + "'";
            ResultSet result = statement.executeQuery(request); // Объект, хранящий ответ от БД

            // Проверка на найденных пользователей
            int k = 0;
            while (result.next()) {
                k++;
            }
            if (k == 1) return true;
        } catch (SQLException e) {
            Server.logger.info("Database connection error");
        }
        return false;
    }

    public boolean isContainsUserByLogin(User user) throws SQLException {
        String request = "SELECT * FROM users WHERE login='" + user.getLogin() + "'";
        ResultSet result = statement.executeQuery(request); // Объект, хранящий ответ от БД

        // Проверка на найденных пользователей
        int k = 0;
        while (result.next()) {
            k++;
        }
        return k == 1;
    }

    public void addUser(User user) throws SQLException {
        prepareStatement = connection.prepareStatement("INSERT INTO users (login, password)" +
                "VALUES (?, ?)");

        prepareStatement.setString(1, user.getLogin());
        prepareStatement.setString(2, user.getPassword());

        prepareStatement.execute();
    }

    public void add(MusicBand musicBand, int id, User user) throws SQLException {
        prepareStatement = connection.prepareStatement("INSERT INTO music (id, name, corX, corY, creationDate, numOfParticipants, estabDate, genre, albumName, albumTracks, login)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        prepareStatement.setInt(1, id);
        prepareStatement.setString(2, musicBand.getName());
        prepareStatement.setInt(3, musicBand.getCoordinates().getX());
        prepareStatement.setInt(4, musicBand.getCoordinates().getY());
        prepareStatement.setString(5, new SimpleDateFormat("dd-MM-yyyy").format(musicBand.getCreationDate()));
        prepareStatement.setLong(6, musicBand.getNumberOfParticipants());
        prepareStatement.setString(7, musicBand.getEstablishmentDate().toString());
        prepareStatement.setString(8, musicBand.getGenre().toString());
        prepareStatement.setString(9, musicBand.getBestAlbum().getName());
        prepareStatement.setInt(10, musicBand.getBestAlbum().getTracks());
        prepareStatement.setString(11, user.getLogin());

        prepareStatement.execute();
    }

    public MusicBandCollection load() throws SQLException {
        MusicBandCollection collection = new MusicBandCollection();

        String request = "SELECT * FROM music";
        ResultSet result = statement.executeQuery(request);

        while (result.next()) {
            int id = result.getInt("id");
            String name = result.getString("name");
            int corX = result.getInt("corX");
            int corY = result.getInt("corY");

            Date creationDate = null;
            LocalDate estabDate = null;
            try {
                creationDate = new SimpleDateFormat("dd-MM-yyyy").parse(result.getString("creationDate"));
                estabDate = LocalDate.parse(result.getString("estabDate"));
            } catch (Exception e) {
                Server.logger.info("Error when parsing creation dates");
            }

            int numOfParticipants = result.getInt("numOfParticipants");
            MusicGenre genre = MusicGenre.getGenreByName(result.getString("genre"));
            String albumName = result.getString("albumName");
            int albumTracks = result.getInt("albumTracks");

            Coordinates coordinates = new Coordinates(corX, corY);
            Album album = new Album(albumName, albumTracks);

            MusicBand musicBand = new MusicBand(id, name, coordinates, creationDate, numOfParticipants, estabDate, genre, album);
            collection.addMusicBand(musicBand);
        }

        return collection;
    }

    public int getIdSeq() throws SQLException {
        String request = "SELECT nextval('idSequence')";
        ResultSet result = statement.executeQuery(request);
        result.next();

        return result.getInt(1);
    }

    public void clear(User user) throws SQLException {
        prepareStatement = connection.prepareStatement("DELETE FROM music WHERE login = ?;");
        prepareStatement.setString(1, user.getLogin());
        prepareStatement.execute();
    }

    public MusicBandCollection removeById(int id, User user) throws SQLException {
        prepareStatement = connection.prepareStatement("DELETE FROM music WHERE (id = ?) AND (login = ?)");
        prepareStatement.setInt(1, id);
        prepareStatement.setString(2, user.getLogin());

        prepareStatement.execute();

        return load();
    }

    public MusicBandCollection updateById(int id, MusicBand musicBand, User user) throws SQLException {
        prepareStatement = connection.prepareStatement("UPDATE music SET name = ?, corX = ?, corY = ?, creationDate = ?, " +
                "numOfParticipants = ?, estabDate = ?, genre = ?, albumName = ?, albumTracks = ?" +
                "WHERE (id = ?) AND (login = ?);");

        prepareStatement.setString(1, musicBand.getName());
        prepareStatement.setInt(2, musicBand.getCoordinates().getX());
        prepareStatement.setInt(3, musicBand.getCoordinates().getY());
        prepareStatement.setString(4, new SimpleDateFormat("dd-MM-yyyy").format(musicBand.getCreationDate()));
        prepareStatement.setLong(5, musicBand.getNumberOfParticipants());
        prepareStatement.setString(6, musicBand.getEstablishmentDate().toString());
        prepareStatement.setString(7, musicBand.getGenre().toString());
        prepareStatement.setString(8, musicBand.getBestAlbum().getName());
        prepareStatement.setInt(9, musicBand.getBestAlbum().getTracks());
        prepareStatement.setInt(10, id);
        prepareStatement.setString(11, user.getLogin());

        prepareStatement.execute();

        return load();
    }

    private void initializeConnection(String[] args) {
        try {
            File file;
            if (args.length > 0) file = new File(args[0]);
            else file = new File("properties.txt");

            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);

            String dbUsername = reader.readLine();
            String dbPassword = reader.readLine();
            String dbUrl = reader.readLine();

            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            statement = connection.createStatement();
        } catch (IOException e) {
            System.err.println("File with properties doesn't exists or the data fields are not correct\nAdd path to properties.txt in program arguments or place properties.txt near with the startup file");
            System.exit(-1);
        } catch (SQLException e) {
            Server.logger.info("Database connection error, check file with properties, exit...");
            System.exit(-1);
        }
    }
}
