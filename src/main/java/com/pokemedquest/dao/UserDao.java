package com.pokemedquest.dao;

import com.pokemedquest.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDao (Data Access Object) for User entities.
 * Handles all database operations related to Users (CRUD - Create, Read, Update, Delete).
 */
public class UserDao {

    // SQL query strings - Adjust table/column names as needed
    private static final String INSERT_USER_SQL = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, username, password_hash, role FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
    private static final String UPDATE_USER_SQL = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?"; // Added
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?"; // Added
    private static final String SELECT_ALL_USERS_SQL = "SELECT id, username, password_hash, role FROM users"; // Added


    /**
     * Creates a new user record in the database.
     * Also updates the passed User object with the auto-generated ID.
     *
     * @param user The User object to save (ID will be ignored initially, then set).
     * @return true if the user was created successfully, false otherwise.
     */
    public boolean createUser(User user) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getRole());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return false;
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
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {

            preparedStatement.setString(1, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String foundUsername = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    user = new User(id, foundUsername, passwordHash, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
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

            preparedStatement.setInt(1, userId);

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
        }
        return Optional.ofNullable(user);
    }

    /**
     * Updates an existing user's details in the database.
     *
     * @param user The User object containing the updated data (must have a valid ID).
     * @return true if the update was successful (at least one row affected), false otherwise.
     */
    public boolean updateUser(User user) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setInt(4, user.getId()); // ID for the WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Returns true if a record was actually updated

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a user from the database based on their ID.
     *
     * @param userId The ID of the user to delete.
     * @return true if the deletion was successful (at least one row affected), false otherwise.
     */
    public boolean deleteUser(int userId) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {

            preparedStatement.setInt(1, userId); // ID for the WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Returns true if a record was actually deleted

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all users from the database.
     * Useful for admin functionalities.
     *
     * @return A List of all User objects; maybe empty if no users exist.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String role = rs.getString("role");
                users.add(new User(id, username, passwordHash, role));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            // Handle exception - returning empty list might be acceptable depending on use case
        }
        return users;
    }
}