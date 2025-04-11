package com.pokemedquest;

import com.pokemedquest.cli.CliHandler;
import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.service.AuthService;
import com.pokemedquest.service.AvatarService;
import com.pokemedquest.service.ProgressService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to PokeMed Quest!");

        UserDao userDao = new UserDao();
        AvatarDao avatarDao = new AvatarDao();
        TestProgressDao testProgressDao = new TestProgressDao();

        AvatarService avatarService = new AvatarService(avatarDao);
        AuthService authService = new AuthService(userDao, avatarService);
        ProgressService progressService = new ProgressService(testProgressDao, avatarService);

        Scanner scanner = new Scanner(System.in);
        CliHandler cliHandler = new CliHandler(scanner, authService, avatarService, progressService);

        try {
            cliHandler.run();
        } catch (Exception e) {
            System.err.println("\n!! An unexpected error occurred in the application !!");
            e.printStackTrace();
            System.err.println("Exiting due to error.");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("\nExiting PokeMed Quest session.");
        }
    }
}
