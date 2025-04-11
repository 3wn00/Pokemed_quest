package com.pokemedquest.service;

import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.User;
import java.util.List;
import java.util.Optional;

/**
 * AvatarService provides logic related to user avatars.
 */
public class AvatarService {

    private final AvatarDao avatarDao;

    /**
     * Constructor for dependency injection.
     * @param avatarDao The AvatarDao instance.
     */
    public AvatarService(AvatarDao avatarDao) {
        this.avatarDao = avatarDao;
    }

    public List<Avatar> getAllAvatars() {
        // Ensure DAO fetches total_experience
        return avatarDao.getAllAvatars();
    }

    /**
     * Creates a default avatar for a newly registered user.
     * Initializes level to 1 and experience to 0.
     * @param user The user for whom to create the avatar.
     * @param avatarName The initial name for the avatar.
     * @return An Optional containing the created Avatar (with ID and initial stats) if successful, empty otherwise.
     */
    public Optional<Avatar> createDefaultAvatar(User user, String avatarName) {
        // Check if the user already has an avatar
        if (avatarDao.findAvatarByUserId(user.getId()).isPresent()) {
            System.err.println("User already has an avatar. Cannot create a new one.");
            return Optional.empty();
        }

        // Define default attributes for the avatar
        String defaultColor = "blue";
        String defaultAccessory = "none"; // Will be handled by constructor logic

        // Use constructor that sets level=1 and experience=0 automatically
        Avatar newAvatar = new Avatar(user.getId(), avatarName, defaultColor, defaultAccessory);

        // Ensure the DAO's createAvatar handles the total_experience field (should be 0 here)
        boolean success = avatarDao.createAvatar(newAvatar);

        if (success) {
            // Refetch the avatar to get the ID assigned by the database
            return avatarDao.findAvatarByUserId(user.getId());
        } else {
            System.err.println("Failed to create default avatar for user ID: " + user.getId());
            return Optional.empty();
        }
    }

    /**
     * Creates a new avatar and stores it in the database.
     * Assumes the Avatar object already has desired initial state (level, experience).
     *
     * @param avatar The avatar to create.
     * @return true if the avatar was successfully created, false otherwise.
     */
    public boolean createAvatar(Avatar avatar) {
        // Ensure DAO's createAvatar saves all fields including level and total_experience
        boolean success = avatarDao.createAvatar(avatar);
        if (success) {
            System.out.println("Avatar created for user ID: " + avatar.getUserId());
        } else {
            System.err.println("Failed to create avatar for user ID: " + avatar.getUserId());
        }
        return success;
    }

    /**
     * Retrieves the avatar for a specific user.
     * @param userId The ID of the user.
     * @return An Optional containing the Avatar (with totalExperience) if found, empty otherwise.
     */
    public Optional<Avatar> getAvatarForUser(int userId) {
        // Ensure DAO fetches total_experience
        return avatarDao.findAvatarByUserId(userId);
    }

    /**
     * Retrieves an avatar by user ID. (Duplicate of getAvatarForUser - can be removed if desired)
     *
     * @param userId The ID of the user whose avatar is being retrieved.
     * @return An Optional containing the Avatar if found, otherwise empty.
     */
    public Optional<Avatar> getAvatarByUserId(int userId) {
        // Ensure DAO fetches total_experience
        return avatarDao.findAvatarByUserId(userId);
    }

    /**
     * Updates the customization (name, color, accessory) of a user's avatar.
     * Level and Experience are NOT changed here.
     * @param userId The ID of the user whose avatar is being updated.
     * @param newName New name for the avatar.
     * @param newColor New color for the avatar.
     * @param newAccessory New accessory for the avatar.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateAvatarCustomization(int userId, String newName, String newColor, String newAccessory) {
        Optional<Avatar> avatarOpt = avatarDao.findAvatarByUserId(userId);
        if (avatarOpt.isPresent()) {
            Avatar avatar = avatarOpt.get();

            // Update only customization fields
            avatar.setAvatarName(newName);
            avatar.setColor(newColor);
            avatar.setAccessory(newAccessory); // Setter handles path update

            // Update in the database using the DAO method that saves all fields
            // The DAO method needs to persist the changes to name, color, accessory,
            // while keeping the existing level and totalExperience from the avatar object.
            return avatarDao.updateAvatarByUserId(avatar);
        } else {
            System.err.println("Cannot update customization: Avatar not found for user ID: " + userId);
            return false;
        }
    }

    /**
     * Updates the core stats (level, totalExperience) of an avatar.
     * Used by ProgressService after calculating changes based on score.
     * @param avatar The Avatar object containing the updated level and totalExperience.
     * @return true if the database update was successful, false otherwise.
     */
    public boolean updateAvatarStats(Avatar avatar) {
        // Ensure the DAO's update method correctly saves level and total_experience
        boolean success = avatarDao.updateAvatarByUserId(avatar);
         if (!success) {
             System.err.println("Failed to update avatar stats in DB for avatar ID: " + avatar.getAvatarId());
         }
        return success;
    }

    // Removed the old levelUpAvatar(int userId) method.
}