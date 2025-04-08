package com.pokemedquest.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn != null) {
                System.out.println("Database connection successful!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}