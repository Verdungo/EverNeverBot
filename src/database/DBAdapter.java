package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.mysql.jdbc.JDBC4Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBAdapter {

    private static final String URL = "jdbc:mysql://localhost:3306/neverbotscores";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "toor";
    private static final String GET_ALL_USERS = "SELECT * FROM user";
    private static final String INSERT_USER = "INSERT INTO user(id, nv_name) VALUES ( ? , ? )";

    private static Driver driver;
    private static Connection connection;

    public DBAdapter() throws SQLException {
        Driver driver = new FabricMySQLDriver();
        DriverManager.registerDriver(driver);
        connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);

        System.out.println(connection.getClass().getName());
    }

    //TODO: возвращать вставленный id ?
    public void insertUser(User user){
        try(PreparedStatement insertUser = connection.prepareStatement(INSERT_USER)) {
            insertUser.setString(1, String.valueOf(user.getId()));
            insertUser.setString(2, user.getName());

            insertUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List getUsers() throws SQLException {
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

            dba.insertUser(new User(14881488,"NaziUser"));
            dba.insertUser(new User(42424242,"SomeOtherUser"));

            for (User user :
                    (List<User>)dba.getUsers()) {
                System.out.printf("(%d) %s\n", user.getId(), user.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
