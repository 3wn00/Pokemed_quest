package com.pokemedquest.dao;

import com.pokemedquest.model.Avatar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvatarDao {

    private static final String INSERT_AVATAR_SQL = "INSERT INTO avatars (user_id, avatar_name, color, accessory, level, total_experience) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_AVATAR_BY_USER_SQL = "SELECT avatar_id, user_id, avatar_name, color, accessory, level, total_experience FROM avatars WHERE user_id = ?";
    private static final String UPDATE_AVATAR_BY_USER_SQL = "UPDATE avatars SET avatar_name = ?, color = ?, accessory = ?, level = ?, total_experience = ? WHERE user_id = ?";
    private static final String SELECT_ALL_AVATARS_SQL = "SELECT avatar_id, user_id, avatar_name, color, accessory, level, total_experience FROM avatars";

    public List<Avatar> getAllAvatars() {
        List<Avatar> avatars = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_AVATARS_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Avatar avatar = new Avatar(
                        resultSet.getInt("avatar_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("avatar_name"),
                        resultSet.getString("color"),
                        resultSet.getString("accessory"),
                        resultSet.getInt("level"),
                        resultSet.getInt("total_experience")
                );
                avatars.add(avatar);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving avatars from database: " + e.getMessage());
            e.printStackTrace();
        }
        return avatars;
    }

    public boolean createAvatar(Avatar avatar) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_AVATAR_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, avatar.getUserId());
            preparedStatement.setString(2, avatar.getAvatarName());
            preparedStatement.setString(3, avatar.getColor());
            preparedStatement.setString(4, avatar.getAccessory());
            preparedStatement.setInt(5, avatar.getLevel());
            preparedStatement.setInt(6, avatar.getTotalExperience());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        avatar.setAvatarId(generatedKeys.getInt(1));
                        return true;
                    } else {
                        System.err.println("Creating avatar succeeded but failed to retrieve the generated ID.");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating avatar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Optional<Avatar> findAvatarByUserId(int userId) {
        Avatar avatar = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVATAR_BY_USER_SQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int avatarId = rs.getInt("avatar_id");
                    String avatarName = rs.getString("avatar_name");
                    String color = rs.getString("color");
                    String accessory = rs.getString("accessory");
                    int level = rs.getInt("level");
                    int totalExperience = rs.getInt("total_experience");

                    avatar = new Avatar(avatarId, userId, avatarName, color, accessory, level, totalExperience);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding avatar by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(avatar);
    }

    public boolean updateAvatarByUserId(Avatar avatar) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_AVATAR_BY_USER_SQL)) {

            stmt.setString(1, avatar.getAvatarName());
            stmt.setString(2, avatar.getColor());
            stmt.setString(3, avatar.getAccessory());
            stmt.setInt(4, avatar.getLevel());
            stmt.setInt(5, avatar.getTotalExperience());
            stmt.setInt(6, avatar.getUserId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating avatar in database for user ID " + avatar.getUserId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
