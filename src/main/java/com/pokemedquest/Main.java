package com.pokemedquest; // Base package

import com.pokemedquest.cli.CliHandler;
import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService; // Assuming CliHandler is in 'cli' subpackage
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

        // 1. Create DAO instances
        UserDao userDao = new UserDao();
        AvatarDao avatarDao = new AvatarDao();
        TestProgressDao testProgressDao = new TestProgressDao();

        // 2. Create Service instances, injecting DAOs
        AuthService authService = new AuthService(userDao);
        AvatarService avatarService = new AvatarService(avatarDao);
        ProgressService progressService = new ProgressService(testProgressDao);

        // 3. Create Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // 4. Create CLI Handler, injecting Services and Scanner
        CliHandler cliHandler = new CliHandler(scanner, authService, avatarService, progressService);

        // --- Start the Application ---
        try {
            cliHandler.run(); // Start the main application loop
        } catch (Exception e) {
            System.err.println("An unexpected error occurred. Exiting.");
            e.printStackTrace();
        } finally {
            // Close the scanner when the application loop finishes
            scanner.close();
            System.out.println("Exiting PokeMed Quest. Goodbye!");
        }
    }
}