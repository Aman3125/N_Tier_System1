package daoexample.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/// <summary>
/// Centralizes JDBC connection details for the application.
/// </summary>
public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/car_garage_db";

//    private static final String URL =
//            "jdbc:mysql://localhost:3306/taskhub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
//

    private static final String USER = "root"; //check this user is setup under Users in PHPMyAdmin
    private static final String PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}