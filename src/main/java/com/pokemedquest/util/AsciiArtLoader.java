package com.pokemedquest.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AsciiArtLoader {

    public static String loadAsciiArt(String filePath) {
        try {
            
            System.out.println("Attempting to load ASCII art from: " + Paths.get(filePath).toAbsolutePath());

            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error loading ASCII art from: " + filePath);
            e.printStackTrace();
            return "ASCII art not found!";
        }
    }
}