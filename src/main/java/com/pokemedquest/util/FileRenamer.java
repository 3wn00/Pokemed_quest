package com.pokemedquest.util;

import java.io.File;

public class FileRenamer {
    public static void main(String[] args) {
        // Path to the directory containing the ASCII art files
        String directoryPath = "src/main/resources/ascii_art/";

        // Get all files in the directory
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();

                // Check if the file matches the incorrect naming pattern
                if (fileName.matches("avatar1_default_.*_.*\\.txt")) {
                    // Extract the accessory and color
                    String[] parts = fileName.replace(".txt", "").split("_");
                    if (parts.length == 4) {
                        String accessory = parts[2];
                        String color = parts[3];

                        // Create the new file name
                        String newFileName = "avatar1_default_" + color + "_" + accessory + ".txt";

                        // Rename the file
                        File newFile = new File(directoryPath + newFileName);
                        if (file.renameTo(newFile)) {
                            System.out.println("Renamed: " + fileName + " -> " + newFileName);
                        } else {
                            System.err.println("Failed to rename: " + fileName);
                        }
                    }
                }
            }
        } else {
            System.err.println("No files found in directory: " + directoryPath);
        }
    }
}