package com.pokemedquest.dao;

import com.pokemedquest.model.Avatar; // Import the Avatar model

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * AvatarDao (Data Access Object) for Avatar entities.
 * Handles database operations related to Avatars.
 * Assuming one Avatar per User for simplicity in find/update methods for now.
 */
public class AvatarDao {

    // SQL query strings - Adjust table/column names as needed
    private static final String INSERT_AVATAR_SQL = "INSERT INTO avatars (user_id, avatar_name, color, accessory, level) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_AVATAR_BY_USER_SQL = "SELECT avatar_id, user_id, avatar_name, color, accessory, level FROM avatars WHERE user_id = ?";
    private static final String UPDATE_AVATAR_BY_USER_SQL = "UPDATE avatars SET avatar_name = ?, color = ?, accessory = ?, level = ? WHERE user_id = ?";
    // Add DELETE statement if needed
    // private static final String DELETE_AVATAR_BY_USER_SQL = "DELETE FROM avatars WHERE user_id = ?";


    /**
     * Creates a new avatar record in the database.
     * Updates the passed Avatar object with the auto-generated ID.
     *
     * @param avatar The Avatar object to save.
     * @return true if the avatar was created successfully, false otherwise.
     */
    public boolean createAvatar(Avatar avatar) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AVATAR_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, avatar.getUserId());
            preparedStatement.setString(2, avatar.getAvatarName());
            preparedStatement.setString(3, avatar.getColor());
            preparedStatement.setString(4, avatar.getAccessory());
            preparedStatement.setInt(5, avatar.getLevel());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avatar.setAvatarId(generatedKeys.getInt(1)); // Set the generated ID
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating avatar: " + e.getMessage());
        }
        return false;
    }

    /**
     * Finds the avatar associated with a specific user ID.
     * Assumes a user has at most one avatar.
     *
     * @param userId The ID of the user whose avatar to find.
     * @return An Optional containing the Avatar if found, otherwise an empty Optional.
     */
    public Optional<Avatar> findAvatarByUserId(int userId) {
        Avatar avatar = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVATAR_BY_USER_SQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int avatarId = rs.getInt("avatar_id");
                    // userId is already known
                    String avatarName = rs.getString("avatar_name");
                    String color = rs.getString("color");
                    String accessory = rs.getString("accessory");
                    int level = rs.getInt("level");
                    avatar = new Avatar(avatarId, userId, avatarName, color, accessory, level);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding avatar by user ID: " + e.getMessage());
        }
        return Optional.ofNullable(avatar);
    }

     /**
     * Updates the details of an existing avatar based on the user ID.
     * Assumes a user has at most one avatar.
     *
     * @param avatar The Avatar object containing the updated information (userId must be set).
     * @return true if the update was successful (at least one row affected), false otherwise.
     */
    public boolean updateAvatarByUserId(Avatar avatar) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AVATAR_BY_USER_SQL)) {

            preparedStatement.setString(1, avatar.getAvatarName());
            preparedStatement.setString(2, avatar.getColor());
            preparedStatement.setString(3, avatar.getAccessory());
            preparedStatement.setInt(4, avatar.getLevel());
            preparedStatement.setInt(5, avatar.getUserId()); // Use userId in WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if at least one row was updated

        } catch (SQLException e) {
            System.err.println("Error updating avatar by user ID: " + e.getMessage());
        }
        return false;
    }

    // --- TODO: Implement delete method if required ---
    /*
    public boolean deleteAvatarByUserId(int userId) {
        // Implementation using DELETE_AVATAR_BY_USER_SQL
        return false; // Placeholder
    }
    */
}