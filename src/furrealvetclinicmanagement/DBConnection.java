/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package furrealvetclinicmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Asus
 */
public class DBConnection {
    
    private static Connection connection = null;
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/furrealvetclinicmanagement"
            + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String URL = config("FURREAL_DB_URL", "db.url", DEFAULT_URL);
    private static final String USER = config("FURREAL_DB_USER", "db.user", "root");
    private static final String PASSWORD = config("FURREAL_DB_PASSWORD", "db.password", "");

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return connection;

    }

    private static String config(String environmentName, String propertyName, String fallback) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return fallback;
    }
}
