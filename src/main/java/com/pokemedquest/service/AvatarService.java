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
        return avatarDao.getAllAvatars();
    }

    /**
     * Creates a default avatar for a newly registered user.
     * @param user The user for whom to create the avatar.
     * @param avatarName The initial name for the avatar.
     * @return An Optional containing the created Avatar (with ID) if successful, empty otherwise.
     */
    public Optional<Avatar> createDefaultAvatar(User user, String avatarName) {
        // Check if the user already has an avatar
        if (avatarDao.findAvatarByUserId(user.getId()).isPresent()) {
            System.err.println("User already has an avatar. Cannot create a new one.");
            return Optional.empty();
        }
    
        // Define default attributes for the avatar
        String defaultColor = "blue";
        String defaultAccessory = "none";
        int defaultLevel = 1;
    
        Avatar newAvatar = new Avatar(user.getId(), avatarName, defaultColor, defaultAccessory, defaultLevel);
        boolean success = avatarDao.createAvatar(newAvatar);
    
        if (success) {
            return Optional.of(newAvatar);
        } else {
            System.err.println("Failed to create default avatar for user ID: " + user.getId());
            return Optional.empty();
        }
    }

    /**
     * Creates a new avatar and stores it in the database.
     *
     * @param avatar The avatar to create.
     * @return true if the avatar was successfully created, false otherwise.
     */
    public boolean createAvatar(Avatar avatar) {
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
     * @return An Optional containing the Avatar if found, empty otherwise.
     */
    public Optional<Avatar> getAvatarForUser(int userId) {
        return avatarDao.findAvatarByUserId(userId);
    }

    /**
     * Retrieves an avatar by user ID.
     *
     * @param userId The ID of the user whose avatar is being retrieved.
     * @return An Optional containing the Avatar if found, otherwise empty.
     */
    public Optional<Avatar> getAvatarByUserId(int userId) {
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
            avatar.setAvatarName(newName);
            avatar.setColor(newColor);
    
            // Update accessory and dynamically adjust ASCII art path
            String basePath = "src/main/resources/ascii_art/";
            String fileName = avatar.getAsciiArtPath();
    
            if (!"none".equals(newAccessory)) {
                // Add the new accessory to the file name
                fileName = fileName.replace(".txt", "_" + newAccessory + ".txt");
            } else {
                // Remove the existing accessory from the file name
                fileName = fileName.replace("_" + avatar.getAccessory() + ".txt", ".txt");
            }
    
            avatar.setAccessory(newAccessory); // Update the accessory
            avatar.setAsciiArtPath(basePath + fileName); // Update the ASCII art path
    
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