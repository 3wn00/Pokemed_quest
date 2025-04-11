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

public class UserDao {

    private static final String INSERT_USER_SQL = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT id, username, password_hash, role FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
    private static final String DELETE_USER_BY_USERNAME_SQL = "DELETE FROM users WHERE username = ?";
    private static final String SELECT_ALL_USERS_SQL = "SELECT id, username, password_hash, role FROM users";

    public boolean deleteUserByUsername(String username) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_BY_USERNAME_SQL)) {
            preparedStatement.setString(1, username);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user by username: " + e.getMessage());
            return false;
        }
    }

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
                User user = new User(id, username, passwordHash, role);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }
        return users;
    }

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
}
