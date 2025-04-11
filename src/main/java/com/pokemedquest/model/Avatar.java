package com.pokemedquest.model;

import com.pokemedquest.util.AsciiArtLoader;

public class Avatar {

    private int avatarId;
    private int userId;
    private String avatarName;
    private String color;
    private String accessory;
    private int level;
    private int totalExperience;
    private String asciiArtPath;

    public Avatar(int userId, String avatarName, String color, String accessory) {
        this.userId = userId;
        this.avatarName = avatarName;
        this.color = color;
        this.accessory = (accessory == null || accessory.trim().isEmpty()) ? "none" : accessory.toLowerCase();
        this.level = 1;
        this.totalExperience = 0;
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
    }

    public Avatar(int avatarId, int userId, String avatarName, String color, String accessory, int level, int totalExperience) {
        this.avatarId = avatarId;
        this.userId = userId;
        this.avatarName = avatarName;
        this.color = color;
        this.accessory = (accessory == null || accessory.trim().isEmpty()) ? "none" : accessory.toLowerCase();
        this.level = level;
        this.totalExperience = totalExperience;
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
    }

    private String determineAsciiArtPath(String name, int lvl, String acc) {
        String basePath = "src/main/resources/ascii_art/";
        String fileName;
        String effectiveAccessory = (acc == null || acc.trim().isEmpty() || "none".equalsIgnoreCase(acc)) ? null : acc.toLowerCase();

        switch (name.toLowerCase()) {
            case "warrior":
                fileName = "avatar1";
                break;
            case "mage":
                fileName = "avatar2";
                break;
            case "archer":
                fileName = "avatar3";
                break;
            default:
                fileName = "avatar1";
        }

        if (lvl >= 5 && lvl < 10) {
            fileName += "_evolved1";
        } else if (lvl >= 10) {
            fileName += "_evolved2";
        } else {
            fileName += "_default";
        }

        if (effectiveAccessory != null) {
            fileName += "_" + effectiveAccessory;
        }

        fileName += ".txt";

        return basePath + fileName;
    }

    public void displayAsciiArt() {
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
        System.out.println("Displaying ASCII art from: " + asciiArtPath);
        System.out.println(AsciiArtLoader.loadAsciiArt(asciiArtPath));
    }

    public void setAsciiArtPath(String asciiArtPath) {
        this.asciiArtPath = asciiArtPath;
    }

    public String getAsciiArtPath() {
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
        return asciiArtPath;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = (accessory == null || accessory.trim().isEmpty()) ? "none" : accessory.toLowerCase();
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.asciiArtPath = determineAsciiArtPath(this.avatarName, this.level, this.accessory);
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(int totalExperience) {
        this.totalExperience = totalExperience;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "avatarId=" + avatarId +
                ", userId=" + userId +
                ", avatarName='" + avatarName + '\'' +
                ", color='" + color + '\'' +
                ", accessory='" + accessory + '\'' +
                ", level=" + level +
                ", totalExperience=" + totalExperience +
                ", asciiArtPath='" + getAsciiArtPath() + '\'' +
                '}';
    }
}
