package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DBAdapter {

    private static final String URL = "jdbc:mysql://localhost:3306/neverbotscores";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "toor";
    private static final String GET_ALL_USERS = "SELECT * FROM user";
    private static final String INSERT_CHAT = "INSERT INTO chat(id, nv_name) VALUES ( ? , ? )";
    private static final String INSERT_USER = "INSERT INTO user(id, nv_name) VALUES ( ? , ? )";
    private static final String REPLACE_CHAT = "REPLACE INTO chat(id, nv_name) VALUES ( ? , ? )";
    private static final String REPLACE_USER = "REPLACE INTO user(id, nv_name) VALUES ( ? , ? )";
    private static final String UPDATE_CHAT = "UPDATE chat SET nv_name = ? WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE user SET nv_name = ? WHERE id = ?";

    private static final String INSERT_USER_IN_CHAT = "INSERT INTO userinchat(idchat, iduser) VALUES ( ? , ? )";

    private static final String GET_CHAT_SCORES =   "select u.id, u.nv_name, s.score from score s\n" +
                                                    "inner join userinchat cu on cu.id = s.iduserinchat\n" +
                                                    "inner join user u on u.id = cu.iduser\n" +
                                                    "where cu.idchat = ?";
    private static final String DELETE_CHAT_SCORES =    "delete s from score s\n" +
                                                        "inner join userinchat uc on s.iduserinchat = uc.id\n" +
                                                        "inner join chat c on c.id = uc.idchat\n" +
                                                        "where c.id = ?";
    private static final String INSERT_CHAT_SCORES =    "insert into score(iduserinchat, score) \n" +
                                                        "select uc.id, ? from userinchat uc\n" +
                                                        "inner join user u on u.id = uc.iduser\n" +
                                                        "inner join chat c on c.id = uc.idchat\n" +
                                                        "where c.id = ? and u.id = ?;\n";
    private static Connection connection;

    public DBAdapter(){
        try {
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt"))));
            System.setErr(new PrintStream(new BufferedOutputStream(new FileOutputStream("err.txt"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(connection.getClass().getName());
    }

    // region chat CRUD
    public void insertChat(Chat chat){
        try(PreparedStatement statement = connection.prepareStatement(INSERT_CHAT)) {
            statement.setLong(1, chat.getId());
            statement.setString(2, chat.getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceChat(Chat chat){
        try(PreparedStatement statement = connection.prepareStatement(REPLACE_CHAT)) {
            statement.setLong(1, chat.getId());
            statement.setString(2, chat.getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateChat(Chat chat){
        try(PreparedStatement statement = connection.prepareStatement(UPDATE_CHAT)) {
            statement.setString(1, chat.getName());
            statement.setLong(2, chat.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // endregion

    // region user CRUD
    public void insertUser(User user){
        try(PreparedStatement statement = connection.prepareStatement(INSERT_USER)) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void replaceUser(User user){
        try(PreparedStatement statement = connection.prepareStatement(REPLACE_USER)) {
            statement.setLong(1, user.getId());
            statement.setString(2, user.getName());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user){
        try(PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setString(1, user.getName());
            statement.setLong(2, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //endregion

    public Map<User, Integer> getChatScores(Chat chat){
        Map<User, Integer> chatScores = new HashMap<>();
        try(PreparedStatement statement = connection.prepareStatement(GET_CHAT_SCORES)) {
            statement.setLong(1, chat.getId());

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                chatScores.put(new User(rs.getLong("u.id"), rs.getString("u.nv_name")), rs.getInt("s.score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatScores;
    }

    public List<User> getUsers() throws SQLException {
        List<User> result = new ArrayList<>();
        Statement getAllUsers = connection.createStatement();
        ResultSet resultSet = getAllUsers.executeQuery(GET_ALL_USERS);
        while (resultSet.next()) {
            result.add(new User(resultSet.getLong("id"), resultSet.getString("nv_name")));
        }
        return result;
    }

    //@SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            DBAdapter dba = new DBAdapter();

            for (User user :
                    dba.getUsers()) {
                System.out.printf("(%d) %s\n", user.getId(), user.getName());
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUserInChat(Chat chat, User user){
        try(PreparedStatement statement = connection.prepareStatement(INSERT_USER_IN_CHAT)) {
            statement.setLong(1, chat.getId());
            statement.setLong(2, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChatScores(Chat chat){
        try(PreparedStatement statement = connection.prepareStatement(DELETE_CHAT_SCORES)) {

            statement.setLong(1, chat.getId() );

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertScores(Chat chat, Entry<User, Integer> chatScore){
        try(PreparedStatement statement = connection.prepareStatement(INSERT_CHAT_SCORES)) {

            statement.setLong(1, chatScore.getValue() );
            statement.setLong(2, chat.getId());
            statement.setLong(3, chatScore.getKey().getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateScores(Chat chat, Map<User, Integer> chatScores) {
        replaceChat(chat);
        deleteChatScores(chat);

        for (Entry<User, Integer> pair : chatScores.entrySet()) {
            User user = pair.getKey();

            replaceUser(user);

            insertUserInChat(chat, user);

            insertScores(chat, pair);
        }



    }
}
