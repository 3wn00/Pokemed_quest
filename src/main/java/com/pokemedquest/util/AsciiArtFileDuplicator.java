package com.pokemedquest.util;

import java.io.*;
import java.util.Map;

public class AsciiArtFileDuplicator {
    public static void main(String[] args) {
        
        String inputDirectory = "src/main/resources/ascii_art/";

        
        String outputDirectory = "src/main/resources/ascii_art/";

        
        Map<String, String> colors = Map.of(
            "red", "\u001B[31m",
            "green", "\u001B[32m",
            "blue", "\u001B[34m",
            "yellow", "\u001B[33m",
            "purple", "\u001B[35m",
            "cyan", "\u001B[36m",
            "white", "\u001B[37m"
        );

       
        String[] existingFiles = {
            "avatar1_default.txt",
            "avatar1_default_bow.txt",
            "avatar1_default_mustache.txt",
            "avatar1_default_necklace.txt",
            "avatar1_evolved1.txt",
            "avatar1_evolved1_bow.txt",
            "avatar1_evolved1_mustache.txt",
            "avatar1_evolved1_necklace.txt",
            "avatar1_evolved2.txt",
            "avatar1_evolved2_bow.txt",
            "avatar1_evolved2_mustache.txt",
            "avatar1_evolved2_necklace.txt",
            "avatar2_default.txt",
            "avatar2_default_bow.txt",
            "avatar2_default_mustache.txt",
            "avatar2_default_necklace.txt",
            "avatar2_evolved1.txt",
            "avatar2_evolved1_bow.txt",
            "avatar2_evolved1_mustache.txt",
            "avatar2_evolved1_necklace.txt",
            "avatar2_evolved2.txt",
            "avatar2_evolved2_bow.txt",
            "avatar2_evolved2_mustache.txt",
            "avatar2_evolved2_necklace.txt",
            "avatar3_default.txt",
            "avatar3_default_bow.txt",
            "avatar3_default_mustache.txt",
            "avatar3_default_necklace.txt",
            "avatar3_evolved1.txt",
            "avatar3_evolved1_bow.txt",
            "avatar3_evolved1_mustache.txt",
            "avatar3_evolved1_necklace.txt",
            "avatar3_evolved2.txt",
            "avatar3_evolved2_bow.txt",
            "avatar3_evolved2_mustache.txt",
            "avatar3_evolved2_necklace.txt",
        };

        
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        
        for (String fileName : existingFiles) {
            String inputFilePath = inputDirectory + fileName;

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                StringBuilder originalContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    originalContent.append(line).append("\n");
                }

               
                for (Map.Entry<String, String> entry : colors.entrySet()) {
                    String colorName = entry.getKey();
                    String colorCode = entry.getValue();

                   
                    String coloredContent = colorCode + originalContent + "\u001B[0m";

                   
                    String newFileName = fileName.replace(".txt", "_" + colorName + ".txt");
                    String newFilePath = outputDirectory + newFileName;

                    
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath))) {
                        writer.write(coloredContent);
                    }

                    System.out.println("Created file: " + newFilePath);
                }
            } catch (IOException e) {
                System.err.println("Error processing file: " + inputFilePath + " - " + e.getMessage());
            }
        }
    }
}