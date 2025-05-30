package com.pokemedquest.model;

/**
 * Represents a user in the PokeMed_Quest application.
 * This is a simple Plain Old Java Object (POJO) or data class.
 */
public class User {

    // --- Fields ---
    private int id; // Unique identifier, often generated by the database
    private String username; // User's login name
    private String passwordHash; // Stores the HASH of the password, NOT the plain text password
    private String role; // User's role (e.g., "child", "admin", "doctor")

    // --- Constructor ---

    /**
     * Constructor for creating a new User object, typically used before saving to the database
     * or when retrieving from the database.
     * Note: The ID is often set separately after being generated by the database or when read from it.
     *
     * @param username The username.
     * @param passwordHash The hashed password.
     * @param role The user's role.
     */
    public User(String username, String passwordHash, String role) {
        // id is often initialized later (e.g., by setId or potentially another constructor)
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

     /**
     * Overloaded Constructor including ID, useful when creating objects
     * from data already retrieved from the database.
     *
     * @param id The user's unique ID from the database.
     * @param username The username.
     * @param passwordHash The hashed password.
     * @param role The user's role.
     */
    public User(int id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }


    // --- Getters and Setters ---
    // Provide public methods to access and modify the private fields (encapsulation)

    public int getId() {
        return id;
    }

    /**
     * Sets the user's ID. This is often used after inserting a new user into the
     * database and retrieving the auto-generated ID.
     * @param id The unique identifier.
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password. Ensure you are setting a HASH, not the plain password.
     * @param passwordHash The hashed password string.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Optional: toString() method for debugging ---
    // Good for easily printing user info (e.g., for logging or debugging)
    // IMPORTANT: Do NOT include the passwordHash in toString() for security reasons.

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }

    // --- Optional: equals() and hashCode() ---
    // You might need to implement these later if you store User objects
    // in HashMaps or HashSets, but they are not strictly required for basic functionality.
}