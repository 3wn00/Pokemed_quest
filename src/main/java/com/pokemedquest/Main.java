package com.pokemedquest;

import com.pokemedquest.cli.CliHandler;
import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.dao.DatabaseManager; // Import DatabaseManager if DAOs need it
import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService;
import java.util.Scanner;

/**
 * Main entry point for the PokeMed_Quest application.
 * Initializes dependencies and starts the command-line interface.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to PokeMed Quest!");

        // --- Dependency Initialization ---
        // Ideally, use a dependency injection framework, but manual setup for now.
        // Note: Assuming your DAOs handle their own DB connection or use a static DatabaseManager
        // If they require a DatabaseManager instance, you'd create it here and pass it to DAO constructors.
        /* Example if DAOs need DatabaseManager:
        DatabaseManager dbManager = new DatabaseManager("jdbc:sqlite:data/application.db"); // Adjust your DB path/URL
        UserDao userDao = new UserDao(dbManager);
        AvatarDao avatarDao = new AvatarDao(dbManager);
        TestProgressDao testProgressDao = new TestProgressDao(dbManager);
        */

        // Using your current DAO instantiation approach (default constructors):
        UserDao userDao = new UserDao();
        AvatarDao avatarDao = new AvatarDao();
        TestProgressDao testProgressDao = new TestProgressDao(); // Ensure this DAO has the findAllProgressRecords() method

        // 2. Create Service instances, injecting dependencies
        // AvatarService needs AvatarDao
        AvatarService avatarService = new AvatarService(avatarDao);

        // AuthService needs UserDao and potentially AvatarService (as per your previous code)
        // Make sure your AuthService constructor matches this call: AuthService(UserDao, AvatarService)
        AuthService authService = new AuthService(userDao, avatarService);

        // *** THIS IS THE CORRECTED LINE ***
        // ProgressService now needs TestProgressDao AND AvatarService
        ProgressService progressService = new ProgressService(testProgressDao, avatarService);

        // 3. Create Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // 4. Create CLI Handler, injecting Services and Scanner
        // Make sure CliHandler constructor signature matches: CliHandler(Scanner, AuthService, AvatarService, ProgressService)
        CliHandler cliHandler = new CliHandler(scanner, authService, avatarService, progressService);

        // --- Start the Application ---
        try {
            cliHandler.run(); // Start the main application loop
        } catch (Exception e) {
            // Catching general Exception can hide specific issues, consider more specific catches if needed
            System.err.println("\n!! An unexpected error occurred in the application !!");
            e.printStackTrace(); // Print stack trace for debugging
            System.err.println("Exiting due to error.");
        } finally {
            // Ensure scanner is always closed
            if (scanner != null) {
                scanner.close();
            }
            // Goodbye message might be printed even after error, which is acceptable
            System.out.println("\nExiting PokeMed Quest session.");
        }
    }
}