package com.pokemedquest.service;

import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.User;
import com.pokemedquest.util.AnsiColor;

import java.util.Optional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        // Check if the user already has an avatar
        if (avatarDao.findAvatarByUserId(user.getId()).isPresent()) {
            System.err.println(AnsiColor.RED + "User already has an avatar. Cannot create a new one." + AnsiColor.RESET);
            return Optional.empty();
        }

        // Define default attributes for the avatar
        String defaultColor = "blue";
        String defaultAccessory = "none";
        int defaultLevel = 1;

        Avatar newAvatar = new Avatar(user.getId(), avatarName, defaultColor, defaultAccessory, defaultLevel);
        boolean success = avatarDao.createAvatar(newAvatar);

        if (success) {
            System.out.println(AnsiColor.GREEN + "Default avatar created successfully for user ID: " + user.getId() + AnsiColor.RESET);
            return Optional.of(newAvatar);
        } else {
            System.err.println(AnsiColor.RED + "Failed to create default avatar for user ID: " + user.getId() + AnsiColor.RESET);
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
            System.out.println(AnsiColor.GREEN + "Avatar created for user ID: " + avatar.getUserId() + AnsiColor.RESET);
        } else {
            System.err.println(AnsiColor.RED + "Failed to create avatar for user ID: " + avatar.getUserId() + AnsiColor.RESET);
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
     * Displays the ASCII art of the avatar with color.
     * @param avatar The avatar whose ASCII art is to be displayed.
     */
    public void displayAvatarAsciiArt(Avatar avatar) {
        // Validate avatar attributes
        if (avatar == null) {
            System.err.println(AnsiColor.RED + "Error: Avatar is null. Cannot display ASCII art." + AnsiColor.RESET);
            return;
        }
        if (avatar.getColor() == null || avatar.getColor().isEmpty()) {
            System.err.println(AnsiColor.RED + "Error: Avatar color is missing. Cannot determine file path." + AnsiColor.RESET);
            return;
        }
        if (avatar.getAccessory() == null) {
            avatar.setAccessory("none"); // Default to "none" if accessory is null
        }
    
        // Dynamically determine the file path based on the avatar's attributes
        String filePath = "src/main/resources/ascii_art/avatar" + avatar.getLevel() + "_default_" + avatar.getColor().toLowerCase();
        if (!"none".equals(avatar.getAccessory())) {
            filePath += "_" + avatar.getAccessory().toLowerCase();
        }
        filePath += ".txt";
    
        // Debugging information
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Avatar ASCII Art ---" + AnsiColor.RESET);
        System.out.println("Displaying ASCII art from: " + filePath);
        System.out.println("DEBUG: File path is " + filePath);
        System.out.println("DEBUG: Avatar details - Name: " + avatar.getAvatarName() +
                           ", Level: " + avatar.getLevel() +
                           ", Color: " + avatar.getColor() +
                           ", Accessory: " + avatar.getAccessory());
    
        // Attempt to read and display the ASCII art
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Display the ASCII art line by line
            }
        } catch (IOException e) {
            System.err.println(AnsiColor.RED + "Error loading ASCII art from file: " + filePath + ". " + e.getMessage() + AnsiColor.RESET);
        }
    }

    /**
 * Maps a color name to its corresponding ANSI color code.
 * @param color The name of the color.
 * @return The ANSI color code for the given color.
 */
private String getColorCode(String color) {
    return switch (color.toLowerCase()) {
        case "red" -> "\u001B[31m";    // Red
        case "green" -> "\u001B[32m";  // Green
        case "blue" -> "\u001B[34m";   // Blue
        case "yellow" -> "\u001B[33m"; // Yellow
        case "purple" -> "\u001B[35m"; // Purple
        case "cyan" -> "\u001B[36m";   // Cyan
        case "white" -> "\u001B[37m";  // White
        default -> "\u001B[0m";        // Reset (default color)
    };
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
        Optional<Avatar> avatarOpt = this.getAvatarForUser(userId); // Use 'this' to call the method within the same class
        if (avatarOpt.isPresent()) {
            Avatar avatar = avatarOpt.get();
            avatar.setAvatarName(newName);
            avatar.setColor(newColor);
    
            // Update accessory and dynamically adjust ASCII art path
            String filePath = "src/main/resources/ascii_art/avatar" + avatar.getLevel() + "_default_" + newColor.toLowerCase();
            if (!"none".equals(newAccessory)) {
                filePath += "_" + newAccessory.toLowerCase();
            }
            filePath += ".txt";
    
            avatar.setAccessory(newAccessory); // Update the accessory
            avatar.setAsciiArtPath(filePath);  // Update the ASCII art path
    
            // Update in the database
            boolean success = avatarDao.updateAvatarByUserId(avatar);
            if (success) {
                System.out.println(AnsiColor.GREEN + "Avatar customization updated successfully!" + AnsiColor.RESET);
            } else {
                System.err.println(AnsiColor.RED + "Failed to update avatar customization." + AnsiColor.RESET);
            }
            return success;
        } else {
            System.err.println(AnsiColor.RED + "Cannot update: Avatar not found for user ID: " + userId + AnsiColor.RESET);
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
            if (success) {
                System.out.println(AnsiColor.GREEN + "Avatar for user " + userId + " leveled up to " + avatar.getLevel() + AnsiColor.RESET);
            } else {
                System.err.println(AnsiColor.RED + "Failed to level up avatar." + AnsiColor.RESET);
            }
            return success;
        } else {
            System.err.println(AnsiColor.RED + "Cannot level up: Avatar not found for user ID: " + userId + AnsiColor.RESET);
            return false;
        }
    }
}