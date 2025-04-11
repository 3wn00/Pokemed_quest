package com.pokemedquest.dao;

import com.pokemedquest.model.Avatar; // Import the Avatar model
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Keep this import

/**
 * AvatarDao (Data Access Object) for Avatar entities.
 * Handles database operations related to Avatars.
 * Assuming one Avatar per User for simplicity in find/update methods for now.
 */
public class AvatarDao {

    // --- Updated SQL query strings ---
    // Added total_experience to INSERT
    private static final String INSERT_AVATAR_SQL = "INSERT INTO avatars (user_id, avatar_name, color, accessory, level, total_experience) VALUES (?, ?, ?, ?, ?, ?)";
    // Added total_experience to SELECT
    private static final String SELECT_AVATAR_BY_USER_SQL = "SELECT avatar_id, user_id, avatar_name, color, accessory, level, total_experience FROM avatars WHERE user_id = ?";
    // Added total_experience to UPDATE, removed ascii_art_path
    private static final String UPDATE_AVATAR_BY_USER_SQL = "UPDATE avatars SET avatar_name = ?, color = ?, accessory = ?, level = ?, total_experience = ? WHERE user_id = ?";
    // Added total_experience to SELECT, removed ascii_art_path
    private static final String SELECT_ALL_AVATARS_SQL = "SELECT avatar_id, user_id, avatar_name, color, accessory, level, total_experience FROM avatars";
    // Add DELETE statement if needed
    // private static final String DELETE_AVATAR_BY_USER_SQL = "DELETE FROM avatars WHERE user_id = ?";


    /**
     * Retrieves all avatars from the database.
     * @return List of all avatars including their total experience.
     */
    public List<Avatar> getAllAvatars() {
        List<Avatar> avatars = new ArrayList<>();
        // Use the updated constant SELECT_ALL_AVATARS_SQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_AVATARS_SQL); // Use the constant
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Use the constructor that accepts totalExperience
                Avatar avatar = new Avatar(
                        resultSet.getInt("avatar_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("avatar_name"),
                        resultSet.getString("color"),
                        resultSet.getString("accessory"),
                        resultSet.getInt("level"),
                        resultSet.getInt("total_experience") // Get total_experience
                );
                // Removed avatar.setAsciiArtPath - constructor handles it
                avatars.add(avatar);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving avatars from database: " + e.getMessage());
            e.printStackTrace();
        }
        return avatars;
    }


    /**
     * Creates a new avatar record in the database including total_experience.
     * Updates the passed Avatar object with the auto-generated ID.
     *
     * @param avatar The Avatar object to save (should have initial level and experience set).
     * @return true if the avatar was created successfully, false otherwise.
     */
    public boolean createAvatar(Avatar avatar) {
        // Use the updated INSERT_AVATAR_SQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AVATAR_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, avatar.getUserId());
            preparedStatement.setString(2, avatar.getAvatarName());
            preparedStatement.setString(3, avatar.getColor());
            preparedStatement.setString(4, avatar.getAccessory());
            preparedStatement.setInt(5, avatar.getLevel());
            preparedStatement.setInt(6, avatar.getTotalExperience()); // Add total_experience parameter

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avatar.setAvatarId(generatedKeys.getInt(1)); // Set the generated ID
                        return true;
                    } else {
                         System.err.println("Creating avatar succeeded but failed to retrieve the generated ID.");
                         return true; // Technically created, but ID might be missing on object
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating avatar: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed error
        }
        return false;
    }

    /**
     * Finds the avatar associated with a specific user ID, including total experience.
     * Assumes a user has at most one avatar.
     *
     * @param userId The ID of the user whose avatar to find.
     * @return An Optional containing the Avatar if found, otherwise an empty Optional.
     */
    public Optional<Avatar> findAvatarByUserId(int userId) {
        Avatar avatar = null;
        // Use the updated SELECT_AVATAR_BY_USER_SQL
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVATAR_BY_USER_SQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int avatarId = rs.getInt("avatar_id");
                    // userId is known
                    String avatarName = rs.getString("avatar_name");
                    String color = rs.getString("color");
                    String accessory = rs.getString("accessory");
                    int level = rs.getInt("level");
                    int totalExperience = rs.getInt("total_experience"); // Get total_experience

                    // Use the constructor that accepts totalExperience
                    avatar = new Avatar(avatarId, userId, avatarName, color, accessory, level, totalExperience);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding avatar by user ID: " + e.getMessage());
             e.printStackTrace(); // Print stack trace for detailed error
        }
        return Optional.ofNullable(avatar);
    }

    /**
     * Updates the details (including level and experience) of an existing avatar based on the user ID.
     * Assumes a user has at most one avatar. Does NOT update ascii_art_path.
     *
     * @param avatar The Avatar object containing the updated information (userId must be set).
     * @return true if the update was successful (at least one row affected), false otherwise.
     */
    public boolean updateAvatarByUserId(Avatar avatar) {
        // Use the updated UPDATE_AVATAR_BY_USER_SQL
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_AVATAR_BY_USER_SQL)) {

            // Set parameters in the new order
            stmt.setString(1, avatar.getAvatarName());
            stmt.setString(2, avatar.getColor());
            stmt.setString(3, avatar.getAccessory());
            stmt.setInt(4, avatar.getLevel());
            stmt.setInt(5, avatar.getTotalExperience()); // Set total_experience
            stmt.setInt(6, avatar.getUserId());          // Set user_id for WHERE clause

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.err.println("Error updating avatar in database for user ID " + avatar.getUserId() + ": " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed error
            return false;
        }
    }

    // --- TODO: Implement delete method if required ---
    /*
    public boolean deleteAvatarByUserId(int userId) {
        // Implementation using DELETE_AVATAR_BY_USER_SQL
        return false; // Placeholder
    }
    */
}