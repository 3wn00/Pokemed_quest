package com.pokemedquest.service;

import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.User; // May be needed for context

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

    /**
     * Creates a default avatar for a newly registered user.
     * @param user The user for whom to create the avatar.
     * @param avatarName The initial name for the avatar.
     * @return An Optional containing the created Avatar (with ID) if successful, empty otherwise.
     */
    public Optional<Avatar> createDefaultAvatar(User user, String avatarName) {
        // Define default values
        String defaultColor = "blue";
        String defaultAccessory = "none";
        int defaultLevel = 1;

        Avatar newAvatar = new Avatar(user.getId(), avatarName, defaultColor, defaultAccessory, defaultLevel);
        boolean success = avatarDao.createAvatar(newAvatar);

        if (success) {
            // newAvatar object should have its ID set by createAvatar
            return Optional.of(newAvatar);
        } else {
            System.err.println("Failed to create default avatar for user ID: " + user.getId());
            return Optional.empty();
        }
    }

    /**
     * Retrieves the avatar for a specific user.
     * @param userId The ID of the user.
     * @return An Optional containing the Avatar if found, empty otherwise.
     */
    public Optional<Avatar> getAvatarForUser(int userId) {
        return avatarDao.findAvatarByUserId(userId);
    }

    /**
     * Updates the customization of a user's avatar.
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
            // Update fields
            avatar.setAvatarName(newName);
            avatar.setColor(newColor);
            avatar.setAccessory(newAccessory);
            // Update in the database
            return avatarDao.updateAvatarByUserId(avatar);
        } else {
            System.err.println("Cannot update: Avatar not found for user ID: " + userId);
            return false;
        }
    }

    /**
     * Example gamification: Increases the avatar's level.
     * @param userId The ID of the user whose avatar should level up.
     * @return true if level up was successful, false otherwise.
     */
    public boolean levelUpAvatar(int userId) {
         Optional<Avatar> avatarOpt = avatarDao.findAvatarByUserId(userId);
         if (avatarOpt.isPresent()) {
             Avatar avatar = avatarOpt.get();
             avatar.setLevel(avatar.getLevel() + 1); // Increment level
             boolean success = avatarDao.updateAvatarByUserId(avatar);
             if(success){
                 System.out.println("Avatar for user " + userId + " leveled up to " + avatar.getLevel());
             }
             return success;
         } else {
             System.err.println("Cannot level up: Avatar not found for user ID: " + userId);
             return false;
         }
    }
}