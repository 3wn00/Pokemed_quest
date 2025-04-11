package com.pokemedquest.dao;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:data/application.db";

    private DatabaseManager() { }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Make sure the JAR is in the classpath.");
            throw new SQLException("Database driver not found", e);
        }

        System.out.println("Using database file: " + Paths.get("data/application.db").toAbsolutePath());

        return DriverManager.getConnection(DB_URL);
    }
}
