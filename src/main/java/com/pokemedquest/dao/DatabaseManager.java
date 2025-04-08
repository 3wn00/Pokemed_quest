package com.pokemedquest.dao;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the SQLite database.
 * Provides a static method to obtain a database connection.
 */
public class DatabaseManager {

    // The path to the SQLite database file.
    // IMPORTANT: This path is relative to where the application is run from.
    // Assuming you run from the project root directory 'PokeMed_Quest',
    // it will look for the db file in the 'data' subfolder.
    private static final String DB_URL = "jdbc:sqlite:data/application.db";

    // Private constructor to prevent instantiation of this utility class.
    private DatabaseManager() { }

    /**
     * Establishes and returns a connection to the SQLite database.
     *
     * The calling method is responsible for closing the connection
     * (preferably using a try-with-resources statement).
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs or the url is null.
     */
    public static Connection getConnection() throws SQLException {
        /*
         * Optional: Explicitly load the SQLite JDBC driver.
         * Modern JDBC drivers (Type 4) often register themselves automatically
         * when they are on the classpath, so this step might not be strictly
         * necessary, but it provides an early check that the driver is available.
         */
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Make sure the JAR is in the classpath.");
            // Wrap the ClassNotFoundException in an SQLException or a RuntimeException
            // to avoid forcing callers to catch ClassNotFoundException specifically.
            throw new SQLException("Database driver not found", e);
        }

        // Debugging: Print the absolute path of the database file
        System.out.println("Using database file: " + Paths.get("data/application.db").toAbsolutePath());

        // Attempt to establish the connection using the DB URL
        return DriverManager.getConnection(DB_URL);
    }
}