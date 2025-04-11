package com.pokemedquest.cli;

import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.TestProgress;
import com.pokemedquest.model.User;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService;
import com.pokemedquest.service.ProgressService.LevelUpResult; // Import the inner record

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Handles the Command Line Interface interactions for PokeMed Quest.
 */
public class CliHandler {

    private final Scanner scanner;
    private final AuthService authService;
    private final AvatarService avatarService;
    private final ProgressService progressService;

    private User currentUser = null; // Stores the currently logged-in user

    // Formatter for displaying dates/times nicely
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public CliHandler(Scanner scanner, AuthService authService, AvatarService avatarService, ProgressService progressService) {
        this.scanner = scanner;
        this.authService = authService;
        this.avatarService = avatarService;
        this.progressService = progressService;
    }


    /**
     * Starts the main application loop.
     */
    public void run() {
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                showMainMenu();
                int choice = promptForInt("Enter choice: ");
                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        handleRegister();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                // User is logged in - show appropriate menu
                System.out.println("\n--- Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ") ---");
                if ("child".equalsIgnoreCase(currentUser.getRole())) {
                    showChildMenu();
                    int choice = promptForInt("Enter choice: ");
                    running = handleChildChoice(choice); // handleChildChoice returns false if user logs out/exits
                } else { // Assume "admin" or "doctor" role
                    showAdminMenu();
                    int choice = promptForInt("Enter choice: ");
                    running = handleAdminChoice(choice); // handleAdminChoice returns false if user logs out/exits
                }
            }
            System.out.println(); // Add a newline for spacing
        }
        System.out.println("Exiting PokeMed Quest. Goodbye!");
    }

    // --- Menu Display Methods ---

    private void showMainMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
    }

    private void showChildMenu() {
        System.out.println("--- Child Menu ---");
        System.out.println("1. View My Avatar");
        System.out.println("2. Customize Avatar");
        System.out.println("3. Record CMAS Score");
        System.out.println("4. View My Progress History");
        // Removed old Level Up option 5
        System.out.println("5. View Avatar ASCII Art"); // Renumbered from 6
        System.out.println("0. Logout");
    }

    private void showAdminMenu() {
        System.out.println("--- Admin/Doctor Menu ---");
        System.out.println("1. View Patient Progress");
        System.out.println("2. Delete User Account");
        System.out.println("3. List All Users");
        System.out.println("4. View All Avatars");
        System.out.println("5. View All Progress Records");
        System.out.println("0. Logout");
    }


    // --- Input Helper Methods ---

    private String promptForString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private int promptForInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine()); // Use parseInt with nextLine
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            } catch (InputMismatchException e) { // Keep just in case nextInt was used somewhere else
                 System.out.println("Invalid input. Please enter a number.");
                 scanner.nextLine(); // Consume the invalid input if nextInt was used
            }
        }
    }

    // --- Action Handler Methods ---

    private void handleRegister() {
        System.out.println("--- Register New User ---");
        String username = promptForString("Enter username: ");
        // Basic validation - check if username exists? (AuthService should handle this)
        String password = promptForString("Enter password: ");
        String role = "";
        while (!role.equals("child") && !role.equals("admin")) {
             role = promptForString("Enter role (child/admin): ").toLowerCase();
             if (!role.equals("child") && !role.equals("admin")) {
                 System.out.println("Invalid role. Please enter 'child' or 'admin'.");
             }
        }

        Optional<User> registeredUser = authService.registerUser(username, password, role);

        if (registeredUser.isPresent()) {
            User newUser = registeredUser.get();
            System.out.println("Registration successful for user: " + newUser.getUsername());

            if ("child".equals(role)) {
                System.out.println("Choose an avatar type:");
                System.out.println("1. Warrior");
                System.out.println("2. Mage");
                System.out.println("3. Archer");

                int avatarChoice = promptForInt("Enter your choice (1-3): ");
                String avatarName;

                switch (avatarChoice) {
                    case 1: avatarName = "Warrior"; break;
                    case 2: avatarName = "Mage"; break;
                    case 3: avatarName = "Archer"; break;
                    default:
                        System.out.println("Invalid choice. Defaulting to 'Warrior'.");
                        avatarName = "Warrior";
                        break;
                }

                // Create the default avatar using the service
                Optional<Avatar> createdAvatarOpt = avatarService.createDefaultAvatar(newUser, avatarName);

                if (createdAvatarOpt.isPresent()) {
                    System.out.println("Avatar '" + avatarName + "' created successfully!");
                    System.out.println("Initial Avatar State:");
                    System.out.println(createdAvatarOpt.get()); // Show initial state including level 1, 0 xp
                } else {
                    System.out.println("Failed to create avatar after registration.");
                }
            }
        } else {
            System.out.println("Registration failed (username might be taken or database error occurred).");
        }
    }


    private void handleLogin() {
        System.out.println("--- Login ---");
        String username = promptForString("Enter username: ");
        String password = promptForString("Enter password: ");

        Optional<User> userOptional = authService.loginUser(username, password);

        if (userOptional.isPresent()) {
            currentUser = userOptional.get(); // Set the logged-in user
            System.out.println("Login successful! Welcome, " + currentUser.getUsername() + "!");
        } else {
            System.out.println("Login failed. Invalid username or password.");
            currentUser = null;
        }
    }

    private void handleLogout() {
        System.out.println("Logging out " + currentUser.getUsername() + "...");
        currentUser = null; // Clear the current user
    }


    private boolean handleChildChoice(int choice) {
        switch (choice) {
            case 1:
                handleViewAvatar();
                break;
            case 2:
                handleCustomizeAvatar();
                break;
            case 3:
                handleRecordProgress();
                break;
            case 4:
                handleViewHistory();
                break;
            case 5: // Renumbered from 6
                handleViewAvatarAsciiArt();
                break;
            // Removed case for old handleLevelUp()
            case 0:
                handleLogout();
                return true; // Still running, just logged out
            default:
                System.out.println("Invalid choice.");
        }
        return true; // Keep running
    }

    private boolean handleAdminChoice(int choice) {
        switch (choice) {
            case 1:
                handleViewPatientProgress();
                break;
            case 2:
                handleDeleteUserAccount();
                break;
            case 3:
                handleListAllUsers();
                break;
            case 4:
                handleViewAllAvatars();
                break;
            case 5:
                handleViewAllProgressRecords();
                break;
            case 0:
                handleLogout();
                return true; // Still running, just logged out
            default:
                System.out.println("Invalid choice.");
        }
        return true; // Keep running
    }


    // --- Admin Action Handlers ---

     private void handleViewPatientProgress() {
        System.out.println("--- View Patient Progress ---");
        String username = promptForString("Enter the username of the patient: ");
        Optional<User> userOpt = authService.findUserByUsername(username); // Assuming this method exists

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<TestProgress> progressRecords = progressService.getProgressHistoryForUser(user.getId());
            if (progressRecords.isEmpty()) {
                System.out.println("No progress records found for user '" + username + "'.");
            } else {
                System.out.println("Progress History for " + username + ":");
                System.out.println("+---------------------+-------+");
                System.out.println("| Date & Time         | Score |");
                System.out.println("+---------------------+-------+");
                for (TestProgress progress : progressRecords) {
                    System.out.printf("| %-19s | %-5d |\n",
                            progress.getTestTimestamp().format(DTF),
                            progress.getCmasScore());
                }
                System.out.println("+---------------------+-------+");
            }
        } else {
            System.out.println("User '" + username + "' not found.");
        }
    }

    private void handleDeleteUserAccount() {
        System.out.println("--- Delete User Account ---");
        String username = promptForString("Enter the username of the account to delete: ");
        if (currentUser != null && currentUser.getUsername().equalsIgnoreCase(username)) {
            System.out.println("You cannot delete your own account while logged in.");
            return;
        }

        // Add confirmation step
        String confirm = promptForString("Are you sure you want to delete user '" + username + "' and all associated data? (yes/no): ");
        if (!"yes".equalsIgnoreCase(confirm)) {
            System.out.println("Deletion cancelled.");
            return;
        }

        boolean success = authService.deleteUserByUsername(username); // Assuming this cascades via FK constraints

        if (success) {
            System.out.println("User account '" + username + "' deleted successfully.");
        } else {
            System.out.println("Failed to delete user account '" + username + "' (user may not exist or database error).");
        }
    }

    private void handleListAllUsers() {
        System.out.println("--- List All Users ---");
        List<User> users = authService.getAllUsers(); // Assuming this method exists

        if (users.isEmpty()) {
            System.out.println("No users found in the system.");
        } else {
            System.out.println("+------------+---------+");
            System.out.println("| Username   | Role    |");
            System.out.println("+------------+---------+");
            for (User user : users) {
                System.out.printf("| %-10s | %-7s |\n", user.getUsername(), user.getRole());
            }
            System.out.println("+------------+---------+");
        }
    }

     private void handleViewAllAvatars() {
        System.out.println("--- View All Avatars ---");
        List<Avatar> avatars = avatarService.getAllAvatars();

        if (avatars.isEmpty()) {
            System.out.println("No avatars found.");
        } else {
            System.out.println("------------------------------------------------------------------------------------");
            for (Avatar avatar : avatars) {
                // Display avatar details, including new experience field
                 System.out.printf("User ID: %d | Avatar ID: %d | Name: %s | Level: %d | Experience: %d | Color: %s | Accessory: %s%n",
                                 avatar.getUserId(),
                                 avatar.getAvatarId(),
                                 avatar.getAvatarName(),
                                 avatar.getLevel(),
                                 avatar.getTotalExperience(), // Display experience
                                 avatar.getColor(),
                                 avatar.getAccessory());
                 System.out.println("  ASCII Path: " + avatar.getAsciiArtPath());
                 System.out.println("------------------------------------------------------------------------------------");

            }
        }
    }

    private void handleViewAllProgressRecords() {
        System.out.println("--- View All Progress Records ---");
        // Make sure ProgressService.getAllProgressRecords() calls a DAO method that exists
        List<TestProgress> progressRecords = progressService.getAllProgressRecords();

        if (progressRecords.isEmpty()) {
            System.out.println("No progress records found in the system.");
        } else {
            System.out.println("+---------+---------------------+-------+");
            System.out.println("| User ID | Date & Time         | Score |");
            System.out.println("+---------+---------------------+-------+");
            for (TestProgress progress : progressRecords) {
                System.out.printf("| %-7d | %-19s | %-5d |\n",
                        progress.getUserId(),
                        progress.getTestTimestamp().format(DTF),
                        progress.getCmasScore());
            }
             System.out.println("+---------+---------------------+-------+");
        }
    }

    // --- Child Action Handlers ---

    private void handleViewAvatar() {
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if(avatarOpt.isPresent()){
            System.out.println("--- Your Avatar ---");
            // Use the updated toString() which includes totalExperience
            System.out.println(avatarOpt.get());
            // Optionally show ASCII art here too
            // avatarOpt.get().displayAsciiArt();
        } else {
            System.out.println("You don't seem to have an avatar yet. One should have been created during registration.");
            // Consider adding logic to create one if missing, though it shouldn't happen with current registration flow.
        }
    }

    private void handleCustomizeAvatar() {
        System.out.println("--- Customize Avatar ---");
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if (!avatarOpt.isPresent()) {
            System.out.println("You need an avatar first!");
            return;
        }

        Avatar avatar = avatarOpt.get();

        System.out.println("Current Avatar Details:");
        System.out.println(avatar); // Display current avatar details

        System.out.println("\nChoose an option to customize:");
        System.out.println("1. Change Avatar Name");
        System.out.println("2. Change Avatar Color");
        System.out.println("3. Add/Change Cosmetic");
        System.out.println("4. Remove Cosmetic");
        System.out.println("0. Cancel");

        int choice = promptForInt("Enter your choice (0-4): ");
        boolean success = false;
        String updatedField = "";

        switch (choice) {
            case 1: {
                String currentName = avatar.getAvatarName();
                String newName = promptForString("Enter new avatar name (" + currentName + "): ");
                if (!newName.isBlank() && !newName.equals(currentName)) {
                     success = avatarService.updateAvatarCustomization(currentUser.getId(), newName, avatar.getColor(), avatar.getAccessory());
                     updatedField = "name";
                } else {
                    System.out.println("Name not changed.");
                }
                break;
            }
            case 2: {
                 String currentColor = avatar.getColor();
                 String newColor = promptForString("Enter new avatar color (" + currentColor + "): ");
                 if (!newColor.isBlank() && !newColor.equals(currentColor)) {
                     success = avatarService.updateAvatarCustomization(currentUser.getId(), avatar.getAvatarName(), newColor, avatar.getAccessory());
                     updatedField = "color";
                 } else {
                     System.out.println("Color not changed.");
                 }
                break;
            }
            case 3: {
                System.out.println("Choose a cosmetic to add/change (Current: " + avatar.getAccessory() + "):");
                System.out.println("1. Bow");
                System.out.println("2. Mustache");
                System.out.println("3. Necklace");

                int cosmeticChoice = promptForInt("Enter your choice (1-3): ");
                String newAccessory;

                switch (cosmeticChoice) {
                    case 1: newAccessory = "bow"; break;
                    case 2: newAccessory = "mustache"; break;
                    case 3: newAccessory = "necklace"; break;
                    default:
                        System.out.println("Invalid cosmetic choice.");
                        return; // Exit customization
                }

                if (!newAccessory.equals(avatar.getAccessory())) {
                    success = avatarService.updateAvatarCustomization(currentUser.getId(), avatar.getAvatarName(), avatar.getColor(), newAccessory);
                    updatedField = "cosmetic to " + newAccessory;
                } else {
                     System.out.println("Cosmetic already set to " + newAccessory + ".");
                }
                break;
            }
            case 4: {
                 if (!"none".equalsIgnoreCase(avatar.getAccessory())) {
                      success = avatarService.updateAvatarCustomization(currentUser.getId(), avatar.getAvatarName(), avatar.getColor(), "none");
                      updatedField = "cosmetic removed";
                 } else {
                      System.out.println("No cosmetic to remove.");
                 }
                break;
            }
            case 0:
                System.out.println("Customization canceled.");
                return;
            default:
                System.out.println("Invalid choice. No changes made.");
                return;
        }

        // Report outcome
         if (!updatedField.isEmpty()) { // Only report if an attempt was made
            if (success) {
                System.out.println("Avatar " + updatedField + " updated successfully!");
                // Show updated avatar
                handleViewAvatar();
            } else {
                System.out.println("Failed to update avatar " + updatedField + ".");
            }
         }
    }


    private void handleRecordProgress() {
        System.out.println("--- Record CMAS Score ---");
        int score = -1;
        while (score < 0) { // Basic validation: Ensure score is not negative
             score = promptForInt("Enter the CMAS score achieved (points gained): ");
             if (score < 0) {
                 System.out.println("Score cannot be negative. Please enter 0 or higher.");
             }
        }

        Optional<LevelUpResult> resultOpt = progressService.recordTestResult(currentUser.getId(), score);

        if (resultOpt.isPresent()) {
            LevelUpResult result = resultOpt.get();
            System.out.println("Progress recorded successfully!");
            System.out.println("  Timestamp: " + result.progress().getTestTimestamp().format(DTF));
            System.out.println("  Score Gained: " + result.gainedExperience());

            if (result.leveledUp()) {
                System.out.println("\n*************************************");
                System.out.println("* CONGRATULATIONS! Avatar Leveled Up! *");
                System.out.println("* New Level: " + result.newLevel() + "            *");
                System.out.println("*************************************\n");
                 // Optionally show the updated avatar details immediately
                 handleViewAvatar();
                 handleViewAvatarAsciiArt(); // Show new art if level changed evolution
            } else if (result.newLevel() == -1) {
                 System.out.println(" (Could not update avatar stats - avatar not found)");
            } else {
                 // Optionally show updated experience even if no level up
                 Optional<Avatar> currentAvatarOpt = avatarService.getAvatarForUser(currentUser.getId());
                 currentAvatarOpt.ifPresent(av -> System.out.println("  Total Experience: " + av.getTotalExperience()));
            }

        } else {
            System.out.println("Failed to record progress (Database error?).");
        }
    }


    private void handleViewHistory() {
        System.out.println("--- Your Progress History ---");
        List<TestProgress> history = progressService.getProgressHistoryForUser(currentUser.getId());
        if (history.isEmpty()) {
            System.out.println("No progress history found.");
        } else {
            System.out.println("+---------------------+-------+");
            System.out.println("| Date & Time         | Score |");
            System.out.println("+---------------------+-------+");
            for (TestProgress progress : history) {
                System.out.printf("| %-19s | %-5d |\n",
                        progress.getTestTimestamp().format(DTF),
                        progress.getCmasScore());
            }
            System.out.println("+---------------------+-------+");
            // Optionally show total score/level here too
            Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
            avatarOpt.ifPresent(av -> System.out.printf("Current Level: %d | Total Experience: %d\n", av.getLevel(), av.getTotalExperience()));
        }
    }

    private void handleViewAvatarAsciiArt() {
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if (avatarOpt.isPresent()) {
            System.out.println("--- Avatar ---");
            avatarOpt.get().displayAsciiArt(); // Display the ASCII art
        } else {
            System.out.println("You don't seem to have an avatar yet.");
        }
    }

    // handleLevelUp() method is now removed as leveling is automatic.

}