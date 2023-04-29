package com.Model;

import javafx.scene.image.Image;

import java.io.*;
import java.sql.*;
import java.util.*;

public class ConnectSQL {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3307/notes";
    private static final String DATABASE_USER = "pes1ug20cs050";
    private static final String DATABASE_PASSWORD = "8453067238";

    private static final String INSERT_USER = "INSERT INTO credentials (username, password) VALUES (?, ?)";
    private static final String SEARCH_USER = "SELECT username FROM credentials";
    private static final String GET_PASSWORD = "SELECT password FROM credentials where username=?";

    private static final String CREATE_TABLE = "CREATE TABLE $table LIKE template";

    private static final String LIST_NOTES = "SELECT title FROM $table";

    private static final String LIST_NOTES_BY_TITLE = "SELECT title FROM $table WHERE title LIKE ?";
    private static final String LIST_NOTES_BY_TAG = "SELECT title FROM $table WHERE tags LIKE ?";

    private static final String GET_NOTE = "SELECT * FROM $table WHERE title=?";

    private static final String INSERT_NOTE = "INSERT INTO $table (title, tags, about, image1, image2, image3) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_NOTE = "UPDATE $table SET title=?, tags=?, about=?, image1=?, image2=?, image3=? WHERE title=?";

    private static final String DELETE_NOTE = "DELETE FROM $table WHERE title=?";
    // Singleton
    private static ConnectSQL instance = null;
    private Connection connection = null;

    private ConnectSQL() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ConnectSQL getInstance() {
        if (instance == null)
            instance = new ConnectSQL();
        return instance;
    }
    public Connection getSQLConnection() {
        return connection;
    }

    public static boolean insertCredentials(String username, String password) throws SQLException {
        Connection connection = getInstance().getSQLConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        System.out.println(preparedStatement);

        preparedStatement = connection.prepareStatement(CREATE_TABLE.replace("$table", username));
        preparedStatement.executeUpdate();
        System.out.println(preparedStatement);

        preparedStatement.close();
        return true;
    }

    public static boolean searchUsername(String username) throws SQLException{
        boolean found = false;
        try {
            Connection connection = getInstance().getSQLConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SEARCH_USER);
            System.out.println(statement);
            // Retrieve column values
            while (resultSet.next()) {
                String value = resultSet.getString("username");
                if (value.equals(username)) {
                    found = true;
                    break;
                }
            }
            statement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error searching username in database");
        }
        return found;
    }

    public static String getPassword(String username) throws SQLException {
        String value = null;
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_PASSWORD);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(preparedStatement);
        if (resultSet.next())
           value = resultSet.getString("password");
        preparedStatement.close();
        resultSet.close();
        return value;
    }

    public List<String> searchNotes() throws SQLException {
        List<String> noteNames = new ArrayList<>();
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(LIST_NOTES.replace("$table", SharedData.username));
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(preparedStatement);

        while (resultSet.next()) {
            String value = resultSet.getString("title");
            noteNames.add(value);
        }
        preparedStatement.close();
        resultSet.close();
        return noteNames;
    }

    public List<String> searchNotesByTitle(String title) throws SQLException {
        List<String> noteNames = new ArrayList<>();
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(LIST_NOTES_BY_TITLE.replace("$table", SharedData.username));
        preparedStatement.setString(1, "%" + title + "%");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(preparedStatement);
        while (resultSet.next()) {
            String value = resultSet.getString("title");
            noteNames.add(value);
        }
        preparedStatement.close();
        resultSet.close();
        return noteNames;
    }

    public List<String> searchNotesByTag(String tag) throws SQLException {
        List<String> noteNames = new ArrayList<>();
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(LIST_NOTES_BY_TAG.replace("$table", SharedData.username));
        preparedStatement.setString(1, "%" + tag + ",%");
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(preparedStatement);
        while (resultSet.next()) {
            String value = resultSet.getString("title");
            noteNames.add(value);
        }
        preparedStatement.close();
        resultSet.close();
        return noteNames;
    }

    public Note getNote(String title) throws Exception {
        Note note = null;
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(GET_NOTE.replace("$table", SharedData.username));
        preparedStatement.setString(1, title);
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println(preparedStatement);
        if (resultSet.next()) {
            String tags = resultSet.getString("tags");
            String about = resultSet.getString("about");

            InputStream inputStream;
            byte[] imageData;
            List<Image> images = new ArrayList<>();
            Image image;

            for(int i=1; i<=3; i++) {
                inputStream = resultSet.getBinaryStream("image" + i);
                if (inputStream != null) {
                    imageData = new byte[inputStream.available()];
                    inputStream.read(imageData);
                    image = new Image(new ByteArrayInputStream(imageData));
                    images.add(image);
                }
                else
                    images.add(null);
            }
            note = new Note(title, tags, about, images, null);
        }
        preparedStatement.close();
        resultSet.close();
        return note;
    }

    public static void insertNote(Note note) throws SQLException, IOException {
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NOTE.replace("$table", SharedData.username));
        preparedStatement.setString(1, note.getTitle());
        preparedStatement.setString(2, note.getTags());
        preparedStatement.setString(3, note.getAbout());

        int i = 1;
        for (File imageFile : note.getFiles()) {
                System.out.println(imageFile);
                FileInputStream fileInputStream = new FileInputStream(imageFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1)
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                preparedStatement.setBytes(3+ i++, byteArrayOutputStream.toByteArray());
        }
        while(i <= 3) {
            preparedStatement.setNull(3 + i++, Types.BLOB);
        }
        preparedStatement.executeUpdate();
        System.out.println(preparedStatement);
        preparedStatement.close();
    }

    public static void updateNote(String title, Note note) throws SQLException, IOException {
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_NOTE.replace("$table", SharedData.username));
        preparedStatement.setString(1, note.getTitle());
        preparedStatement.setString(2, note.getTags());
        preparedStatement.setString(3, note.getAbout());

        int i = 1;
        for (File imageFile : note.getFiles()) {
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1)
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            preparedStatement.setBytes(3+ i++, byteArrayOutputStream.toByteArray());
        }
        while(i <= 3) {
            preparedStatement.setNull(3 + i++, Types.BLOB);
        }
        preparedStatement.setString(7, title);
        preparedStatement.executeUpdate();
        System.out.println(preparedStatement);
        preparedStatement.close();
    }

    public static void deleteNote(List<String> deleteList) throws SQLException {
        Connection connection = getInstance().getSQLConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_NOTE.replace("$table", SharedData.username));
        for(String title : deleteList) {
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
            System.out.println(preparedStatement);
        }
        preparedStatement.close();
    }

}
