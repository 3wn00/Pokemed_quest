package com.pokemedquest.cli;

import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.TestProgress;
import com.pokemedquest.model.User;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService;

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
        System.out.println("5. Level Up Avatar (Test)"); // Example action
        System.out.println("6. View Avatar ASCII Art"); // New option
        System.out.println("0. Logout");
    }

     private void showAdminMenu() {
        System.out.println("--- Admin/Doctor Menu ---");
        // Add admin specific options later
        System.out.println("1. View Patient Progress (Example - Requires selecting patient)");
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
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume the leftover newline character
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    // --- Action Handler Methods ---

    private void handleRegister() {
        System.out.println("--- Register New User ---");
        String username = promptForString("Enter username: ");
        String password = promptForString("Enter password: ");
        String role = promptForString("Enter role (child/admin): ").toLowerCase();
    
        if (!role.equals("child") && !role.equals("admin")) {
            System.out.println("Invalid role. Defaulting to 'child'.");
            role = "child";
        }
    
        Optional<User> registeredUser = authService.registerUser(username, password, role);
    
        if (registeredUser.isPresent()) {
            User newUser = registeredUser.get();
            System.out.println("Registration successful for user: " + newUser.getUsername());
    
            if ("child".equals(role)) {
                System.out.println("Choose an avatar from the following options:");
                System.out.println("1. Warrior (avatar1_default.txt)");
                System.out.println("2. Mage (avatar2_default.txt)");
                System.out.println("3. Archer (avatar3_default.txt)");
    
                int avatarChoice = promptForInt("Enter your choice (1-3): ");
                String avatarName;
                String asciiArtPath;
    
                switch (avatarChoice) {
                    case 1 -> {
                        avatarName = "Warrior";
                        asciiArtPath = "src/main/resources/ascii_art/avatar1_default.txt";
                    }
                    case 2 -> {
                        avatarName = "Mage";
                        asciiArtPath = "src/main/resources/ascii_art/avatar2_default.txt";
                    }
                    case 3 -> {
                        avatarName = "Archer";
                        asciiArtPath = "src/main/resources/ascii_art/avatar3_default.txt";
                    }
                    default -> {
                        System.out.println("Invalid choice. Defaulting to 'Warrior'.");
                        avatarName = "Warrior";
                        asciiArtPath = "src/main/resources/ascii_art/avatar1_default.txt";
                    }
                }
    
                Avatar newAvatar = new Avatar(newUser.getId(), avatarName, "blue", "none", 1);
                newAvatar.setAsciiArtPath(asciiArtPath); // Set the correct ASCII art path
                boolean success = avatarService.createAvatar(newAvatar);
    
                if (success) {
                    System.out.println("Avatar '" + avatarName + "' created successfully!");
                } else {
                    System.out.println("Failed to create avatar.");
                }
            }
        } else {
            System.out.println("Registration failed (username might be taken or error occurred).");
        }
    }

    private void handleLogin() {
        System.out.println("--- Login ---");
        String username = promptForString("Enter username: ");
        String password = promptForString("Enter password: ");

        // !!! REMEMBER: Password should be VERIFIED AGAINST HASH by the service !!!
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
            case 5:
                handleLevelUp(); // Example action
                break;
            case 6:
                handleViewAvatarAsciiArt(); // New action
            break;
            case 0:
                handleLogout();
                return true; // Still running, just logged out
            default:
                System.out.println("Invalid choice.");
        }
        return true; // Keep running
    }

     private boolean handleAdminChoice(int choice) {
         switch(choice) {
             case 1:
                 System.out.println("Viewing Patient Progress (Not fully implemented)...");
                 // TODO: Add logic to list patients, select one, view progress
                 break;
             case 0:
                 handleLogout();
                 return true; // Still running, just logged out
             default:
                 System.out.println("Invalid choice.");
         }
        return true; // Keep running
     }


    // Child Action Handlers
    private void handleViewAvatar() {
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if(avatarOpt.isPresent()){
            System.out.println("--- Your Avatar ---");
            System.out.println(avatarOpt.get()); // Uses Avatar's toString() method
        } else {
            System.out.println("You don't seem to have an avatar yet.");
             // Optionally offer to create one
             // String avatarName = promptForString("Enter a name for your new avatar: ");
             // avatarService.createDefaultAvatar(currentUser, avatarName);
        }
    }

    /**
 * Handles viewing the avatar's ASCII art.
 */
private void handleViewAvatarAsciiArt() {
    Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
    if (avatarOpt.isPresent()) {
        System.out.println("--- Avatar ASCII Art ---");
        avatarOpt.get().displayAsciiArt(); // Display the ASCII art
    } else {
        System.out.println("You don't seem to have an avatar yet.");
    }
}

    /**
 * Handles customizing the avatar.
 */
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

    System.out.println("Choose an option to customize:");
    System.out.println("1. Change Avatar Name");
    System.out.println("2. Change Avatar Color");
    System.out.println("3. Add/Change Cosmetic");
    System.out.println("4. Remove Cosmetic");
    System.out.println("0. Cancel");

    int choice = promptForInt("Enter your choice (0-4): ");

    switch (choice) {
        case 1 -> {
            String newName = promptForString("Enter new avatar name (" + avatar.getAvatarName() + "): ");
            avatarService.updateAvatarCustomization(avatar.getUserId(), newName, avatar.getColor(), avatar.getAccessory());
            System.out.println("Avatar name updated successfully!");
        }
        case 2 -> {
            String newColor = promptForString("Enter new avatar color (" + avatar.getColor() + "): ");
            avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), newColor, avatar.getAccessory());
            System.out.println("Avatar color updated successfully!");
        }
        case 3 -> {
            System.out.println("Choose a cosmetic to add/change:");
            System.out.println("1. Bow");
            System.out.println("2. Mustache");
            System.out.println("3. Necklace");

            int cosmeticChoice = promptForInt("Enter your choice (1-3): ");
            String newAccessory;

            switch (cosmeticChoice) {
                case 1 -> newAccessory = "bow";
                case 2 -> newAccessory = "mustache";
                case 3 -> newAccessory = "necklace";
                default -> {
                    System.out.println("Invalid choice. No changes made.");
                    return;
                }
            }

            avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), avatar.getColor(), newAccessory);
            System.out.println("Cosmetic updated successfully!");
        }
        case 4 -> {
            avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), avatar.getColor(), "none");
            System.out.println("Cosmetic removed successfully!");
        }
        case 0 -> System.out.println("Customization canceled.");
        default -> System.out.println("Invalid choice. No changes made.");
    }
}

    private void handleRecordProgress() {
        System.out.println("--- Record CMAS Score ---");
        int score = promptForInt("Enter the CMAS score achieved: ");
        // Add validation if score has specific range

        Optional<TestProgress> recordedProgress = progressService.recordTestResult(currentUser.getId(), score);
        if(recordedProgress.isPresent()){
            System.out.println("Progress recorded successfully!");
            System.out.println("New Record: " + recordedProgress.get()); // Uses TestProgress toString()
             // Maybe trigger avatar level up?
             // avatarService.levelUpAvatar(currentUser.getId());
        } else {
            System.out.println("Failed to record progress.");
        }
    }

    private void handleViewHistory() {
        System.out.println("--- Your Progress History ---");
        List<TestProgress> history = progressService.getProgressHistoryForUser(currentUser.getId());
        if (history.isEmpty()) {
            System.out.println("No progress history found.");
        } else {
            System.out.println("Date & Time        | Score");
            System.out.println("-------------------|-------");
            for (TestProgress progress : history) {
                // Use the formatter defined earlier
                System.out.printf("%-19s| %d%n",
                        progress.getTestTimestamp().format(DTF),
                        progress.getCmasScore());
            }
            System.out.println("---------------------------");
        }
    }

     /**
 * Handles leveling up the avatar.
 */
private void handleLevelUp() {
    System.out.println("--- Attempting Level Up ---");
    boolean success = avatarService.levelUpAvatar(currentUser.getId());
    if (success) {
        System.out.println("Avatar leveled up successfully!");
    } else {
        System.out.println("Failed to level up avatar.");
    }
}
}