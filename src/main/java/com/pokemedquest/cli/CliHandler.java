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
        System.out.println("0. Logout");
    }

     private void showAdminMenu() {
        System.out.println("--- Admin/Doctor Menu ---");
        // Add admin specific options later
        System.out.println("1. View Patient Progress (Example - Requires selecting patient)");
        System.out.println("2. Delete User Account"); // Deletion (admin only)
        System.out.println("3. List All Users"); // List all users (admin only) not implemented
        System.out.println("4. View All Avatars"); // List all avatars (admin only)
        System.out.println("5. View All Progress Records"); // List all progress records (admin only)
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
        // Simple role assignment for now, could be more complex
        String role = promptForString("Enter role (child/admin): ").toLowerCase();
        if (!role.equals("child") && !role.equals("admin")) {
             System.out.println("Invalid role. Defaulting to 'child'.");
             role = "child";
        }


        // !!! REMEMBER: Password should be HASHED by the service !!!
        Optional<User> registeredUser = authService.registerUser(username, password, role);

        if (registeredUser.isPresent()) {
            System.out.println("Registration successful for user: " + registeredUser.get().getUsername());
            // Optionally create a default avatar for new child users
            if("child".equals(registeredUser.get().getRole())) {
                String avatarName = promptForString("Enter a name for your new avatar: ");
                avatarService.createDefaultAvatar(registeredUser.get(), avatarName);
                System.out.println("Default avatar created!");
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

    private void handleCustomizeAvatar() {
        System.out.println("--- Customize Avatar ---");
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if(!avatarOpt.isPresent()){
            System.out.println("You need an avatar first!");
            return;
        }

        String newName = promptForString("Enter new avatar name (" + avatarOpt.get().getAvatarName() + "): ");
        String newColor = promptForString("Enter new color (" + avatarOpt.get().getColor() + "): ");
        String newAccessory = promptForString("Enter new accessory (" + avatarOpt.get().getAccessory() + "): ");

        boolean success = avatarService.updateAvatarCustomization(currentUser.getId(), newName, newColor, newAccessory);
        if (success) {
            System.out.println("Avatar updated successfully!");
        } else {
            System.out.println("Failed to update avatar.");
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

     private void handleLevelUp() {
        System.out.println("--- Attempting Level Up ---");
        avatarService.levelUpAvatar(currentUser.getId()); // Service method prints success/failure
     }
}