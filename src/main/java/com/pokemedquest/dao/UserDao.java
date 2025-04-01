package com.pokemedquest.dao;

import com.pokemedquest.model.User; // Import the User model

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; // If you add a getAllUsers method
import java.util.List;     // If you add a getAllUsers method
import java.util.Optional; // Used for find methods that might not return a result

/**
 * UserDao (Data Access Object) for User entities.
 * Handles all database operations related to Users (CRUD - Create, Read, Update, Delete).
 */
public class UserDao {

    // SQL query strings - defined as constants
    // Assumes a table named 'users' with the specified columns
    private static final String INSERT_USER_SQL = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, username, password_hash, role FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
    // Add UPDATE and DELETE SQL statements later as needed
    // private static final String UPDATE_USER_SQL = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?";
    // private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";
    // private static final String SELECT_ALL_USERS_SQL = "SELECT id, username, password_hash, role FROM users";


    /**
     * Creates a new user record in the database.
     * Also updates the passed User object with the auto-generated ID.
     *
     * @param user The User object to save (ID will be ignored initially, then set).
     * @return true if the user was created successfully, false otherwise.
     */
    public boolean createUser(User user) {
        // Use try-with-resources to ensure Connection and PreparedStatement are closed automatically
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters using placeholders (?) to prevent SQL injection
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getRole());

            int affectedRows = preparedStatement.executeUpdate();

            // Check if the insertion was successful
            if (affectedRows > 0) {
                // Retrieve the auto-generated key (the user ID)
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1)); // Set the ID back on the user object
                        return true; // User created successfully
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            // Handle exception appropriately - maybe log it or throw a custom exception
        }
        return false; // User creation failed
    }

    /**
     * Finds a user by their unique username.
     * Useful for login checks.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserByUsername(String username) {
        User user = null;
        // Use try-with-resources for Connection, PreparedStatement, and ResultSet
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {

            preparedStatement.setString(1, username); // Set the username parameter

            try (ResultSet rs = preparedStatement.executeQuery()) {
                // Check if a user was found
                if (rs.next()) {
                    // Map the ResultSet data to a User object
                    int id = rs.getInt("id");
                    String foundUsername = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    user = new User(id, foundUsername, passwordHash, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            // Handle exception
        }
        // Wrap the result in Optional - handles null gracefully
        return Optional.ofNullable(user);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param userId The ID of the user to find.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserById(int userId) {
        User user = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {

            preparedStatement.setInt(1, userId); // Set the ID parameter

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    user = new User(id, username, passwordHash, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            // Handle exception
        }
        return Optional.ofNullable(user);
    }

    // --- TODO: Implement other methods as needed ---

    /*
    public boolean updateUser(User user) {
        // Implementation would be similar to createUser, using UPDATE_USER_SQL
        // Use PreparedStatement and set parameters for username, hash, role, and id (in WHERE clause)
        return false; // Placeholder
    }

    public boolean deleteUser(int userId) {
        // Implementation using DELETE_USER_SQL
        // Use PreparedStatement and set the id parameter
        return false; // Placeholder
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // Implementation using SELECT_ALL_USERS_SQL
        // Loop through ResultSet, create User objects, add to list
        return users; // Placeholder
    }
    */

}