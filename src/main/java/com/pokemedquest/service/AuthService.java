package com.pokemedquest.service;

import org.mindrot.jbcrypt.BCrypt; // Always use the jBCrypt class
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.model.User;
import java.util.List;
import java.util.Optional;

/**
 * AuthService provides authentication-related services like user registration and login.
 */
public class AuthService {

    private final UserDao userDao;

    /**
     * Constructor for dependency injection.
     * Requires a UserDao instance to interact with the database.
     * @param userDao The UserDao instance.
     */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new user.
     * NOTE: Password hashing MUST be implemented here in a real application.
     *
     * @param username The desired username.
     * @param plainPassword The user's chosen plain text password.
     * @param role The user's role (e.g., "child", "admin").
     * @return An Optional containing the newly created User (with ID) if successful,
     * otherwise an empty Optional.
     */
    public Optional<User> registerUser(String username, String plainPassword, String role) {
        // Use BCrypt from jBCrypt to hash the password
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // Check if username already exists (optional, depends on DB constraints)
        if (userDao.findUserByUsername(username).isPresent()) {
            System.err.println("Username '" + username + "' already exists.");
            return Optional.empty(); // Username taken
        }

        User newUser = new User(username, hashedPassword, role);
        boolean success = userDao.createUser(newUser);

        if (success) {
            // The newUser object should now have its ID set by the createUser method
            return Optional.of(newUser);
        } else {
            return Optional.empty(); // Registration failed
        }
    }

    /**
     * Attempts to log in a user.
     * NOTE: Password verification MUST use a proper hashing check in a real application.
     *
     * @param username The username attempting to log in.
     * @param plainPassword The plain text password entered by the user.
     * @return An Optional containing the User object if login is successful,
     * otherwise an empty Optional.
     */
    public Optional<User> loginUser(String username, String plainPassword) {
        Optional<User> userOptional = userDao.findUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Use BCrypt to verify the hashed password
            if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
                return Optional.of(user); // Login successful
            }
        }

        // User not found OR password mismatch
        return Optional.empty();
    }
    
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

}
