package com.pokemedquest.cli;

import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.TestProgress;
import com.pokemedquest.model.User;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService;
import com.pokemedquest.util.AnsiColor;

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
                int choice = promptForInt(AnsiColor.CYAN + "Enter choice: " + AnsiColor.RESET);
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
                        System.out.println(AnsiColor.RED + "Invalid choice. Please try again." + AnsiColor.RESET);
                }
            } else {
                // User is logged in - show appropriate menu
                System.out.println(AnsiColor.BRIGHT_YELLOW + "\n--- Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ") ---" + AnsiColor.RESET);
                if ("child".equalsIgnoreCase(currentUser.getRole())) {
                    showChildMenu();
                    int choice = promptForInt(AnsiColor.CYAN + "Enter choice: " + AnsiColor.RESET);
                    running = handleChildChoice(choice); // handleChildChoice returns false if user logs out/exits
                } else { // Assume "admin" or "doctor" role
                    showAdminMenu();
                    int choice = promptForInt(AnsiColor.CYAN + "Enter choice: " + AnsiColor.RESET);
                    running = handleAdminChoice(choice); // handleAdminChoice returns false if user logs out/exits
                }
            }
            System.out.println(); // Add a newline for spacing
        }
    }


    // --- Menu Display Methods ---

    private void showMainMenu() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Main Menu ---" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_GREEN + "1. Login" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_BLUE + "2. Register" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_RED + "0. Exit" + AnsiColor.RESET);
    }

    private void showChildMenu() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Child Menu ---" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_GREEN + "1. View My Avatar" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_BLUE + "2. Customize Avatar" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_PURPLE + "3. Record CMAS Score" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_CYAN + "4. View My Progress History" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_YELLOW + "5. Level Up Avatar (Test)" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_GREEN + "6. View Avatar ASCII Art" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_RED + "0. Logout" + AnsiColor.RESET);
    }

    private void showAdminMenu() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Admin/Doctor Menu ---" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_GREEN + "1. View Patient Progress (Example - Requires selecting patient)" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_RED + "0. Logout" + AnsiColor.RESET);
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
                System.out.println(AnsiColor.RED + "Invalid input. Please enter a number." + AnsiColor.RESET);
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
        System.out.println("DEBUG: Child menu choice selected: " + choice); // Debug log
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
                handleLevelUp();
                break;
            case 6:
                handleViewAvatarAsciiArt(); // Handle option 6
                break;
            case 0:
                handleLogout();
                return true; // Still running, just logged out
            default:
                System.out.println(AnsiColor.RED + "Invalid choice. Please try again." + AnsiColor.RESET);
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

    private void handleViewAvatarAsciiArt() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- View Avatar ASCII Art ---" + AnsiColor.RESET);
    
        // Retrieve the avatar for the current user
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if (avatarOpt.isPresent()) {
            Avatar avatar = avatarOpt.get();
    
            // Debugging: Log avatar details
            System.out.println("DEBUG: Retrieved avatar - Name: " + avatar.getAvatarName() +
                               ", Level: " + avatar.getLevel() +
                               ", Color: " + avatar.getColor() +
                               ", Accessory: " + avatar.getAccessory());
    
            // Display the ASCII art
            avatarService.displayAvatarAsciiArt(avatar);
        } else {
            System.out.println(AnsiColor.RED + "You don't seem to have an avatar yet." + AnsiColor.RESET);
        }
    }

    private void handleCustomizeAvatar() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Customize Avatar ---" + AnsiColor.RESET);
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(currentUser.getId());
        if (!avatarOpt.isPresent()) {
            System.out.println(AnsiColor.RED + "You need an avatar first!" + AnsiColor.RESET);
            return;
        }
    
        Avatar avatar = avatarOpt.get();
    
        System.out.println(AnsiColor.BRIGHT_GREEN + "Current Avatar Details:" + AnsiColor.RESET);
        avatarService.displayAvatarAsciiArt(avatar);
    
        System.out.println(AnsiColor.BRIGHT_BLUE + "Choose an option to customize:" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_GREEN + "1. Change Avatar Name" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_BLUE + "2. Change Avatar Color" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_PURPLE + "3. Add/Change Cosmetic" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_RED + "4. Remove Cosmetic" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_YELLOW + "0. Cancel" + AnsiColor.RESET);
    
        int choice = promptForInt(AnsiColor.CYAN + "Enter your choice (0-4): " + AnsiColor.RESET);
    
        switch (choice) {
            case 1 -> {
                String newName = promptForString(AnsiColor.CYAN + "Enter new avatar name (" + avatar.getAvatarName() + "): " + AnsiColor.RESET);
                avatarService.updateAvatarCustomization(avatar.getUserId(), newName, avatar.getColor(), avatar.getAccessory());
                System.out.println(AnsiColor.GREEN + "Avatar name updated successfully!" + AnsiColor.RESET);
            }
            case 2 -> {
                String newColor = chooseAvatarColor();
                if (newColor != null) {
                    avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), newColor, avatar.getAccessory());
                    System.out.println(AnsiColor.GREEN + "Avatar color updated successfully!" + AnsiColor.RESET);
                }
            }
            case 3 -> {
                System.out.println(AnsiColor.BRIGHT_BLUE + "Choose a cosmetic to add/change:" + AnsiColor.RESET);
                System.out.println(AnsiColor.BRIGHT_GREEN + "1. Bow" + AnsiColor.RESET);
                System.out.println(AnsiColor.BRIGHT_BLUE + "2. Mustache" + AnsiColor.RESET);
                System.out.println(AnsiColor.BRIGHT_PURPLE + "3. Necklace" + AnsiColor.RESET);
    
                int cosmeticChoice = promptForInt(AnsiColor.CYAN + "Enter your choice (1-3): " + AnsiColor.RESET);
                String newAccessory;
    
                switch (cosmeticChoice) {
                    case 1 -> newAccessory = "bow";
                    case 2 -> newAccessory = "mustache";
                    case 3 -> newAccessory = "necklace";
                    default -> {
                        System.out.println(AnsiColor.RED + "Invalid choice. No changes made." + AnsiColor.RESET);
                        return;
                    }
                }
    
                avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), avatar.getColor(), newAccessory);
                System.out.println(AnsiColor.GREEN + "Cosmetic updated successfully!" + AnsiColor.RESET);
            }
            case 4 -> {
                avatarService.updateAvatarCustomization(avatar.getUserId(), avatar.getAvatarName(), avatar.getColor(), "none");
                System.out.println(AnsiColor.GREEN + "Cosmetic removed successfully!" + AnsiColor.RESET);
            }
            case 0 -> System.out.println(AnsiColor.YELLOW + "Customization canceled." + AnsiColor.RESET);
            default -> System.out.println(AnsiColor.RED + "Invalid choice. No changes made." + AnsiColor.RESET);
        }
    }

    private String chooseAvatarColor() {
        System.out.println(AnsiColor.BRIGHT_YELLOW + "--- Choose a Color ---" + AnsiColor.RESET);
        System.out.println(AnsiColor.RED + "1. Red" + AnsiColor.RESET);
        System.out.println(AnsiColor.GREEN + "2. Green" + AnsiColor.RESET);
        System.out.println(AnsiColor.BLUE + "3. Blue" + AnsiColor.RESET);
        System.out.println(AnsiColor.YELLOW + "4. Yellow" + AnsiColor.RESET);
        System.out.println(AnsiColor.PURPLE + "5. Purple" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "6. Cyan" + AnsiColor.RESET);
        System.out.println(AnsiColor.WHITE + "7. White" + AnsiColor.RESET);
        System.out.println(AnsiColor.BRIGHT_RED + "0. Cancel" + AnsiColor.RESET);
    
        int choice = promptForInt(AnsiColor.CYAN + "Enter your choice (0-7): " + AnsiColor.RESET);
    
        return switch (choice) {
            case 1 -> "red";
            case 2 -> "green";
            case 3 -> "blue";
            case 4 -> "yellow";
            case 5 -> "purple";
            case 6 -> "cyan";
            case 7 -> "white";
            case 0 -> {
                System.out.println(AnsiColor.YELLOW + "Color selection canceled." + AnsiColor.RESET);
                yield null;
            }
            default -> {
                System.out.println(AnsiColor.RED + "Invalid choice. No changes made." + AnsiColor.RESET);
                yield null;
            }
        };
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