package com.pokemedquest.service;

import com.pokemedquest.dao.UserDao;
import com.pokemedquest.model.User;

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
        // --- TODO: Implement Password Hashing ---
        // Use a library like BCrypt:
        // String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        // For this example, we'll store the plain password HASH (WHICH IS WRONG - just demonstrating flow)
        // IN A REAL APP, NEVER STORE OR PASS AROUND PLAIN PASSWORDS LIKE THIS BEYOND INITIAL HASHING
        String hashedPassword = plainPassword; // <-- !!! REPLACE WITH ACTUAL HASHING !!!

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

            // --- TODO: Implement Password Verification ---
            // Use the same hashing library used during registration:
            // if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            //    return Optional.of(user); // Passwords match
            // }
            // For this example, we compare the plain password HASH (WHICH IS WRONG)
            // IN A REAL APP, NEVER COMPARE PLAIN PASSWORDS
            if (user.getPasswordHash().equals(plainPassword)) { // <-- !!! REPLACE WITH BCrypt.checkpw() !!!
                 return Optional.of(user); // Login successful (using insecure comparison)
            }
        }

        // User not found OR password mismatch
        return Optional.empty();
    }

        /**
     * Allows an admin user to delete another user account.
     * Includes a basic role check for permission.
     *
     * @param userIdToDelete The ID of the user account to be deleted.
     * @param requestingAdmin The User object representing the admin performing the action.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteUserAccount(int userIdToDelete, User requestingAdmin) {
        // Optional: Add permission check - is requestingAdmin allowed to delete?
        if (requestingAdmin != null && "admin".equalsIgnoreCase(requestingAdmin.getRole())) {
            // Prevent admin from deleting themselves? (Optional check)
            if (requestingAdmin.getId() == userIdToDelete) {
                System.err.println("Admin cannot delete their own account through this method.");
                return false;
            }
            System.out.println("Admin " + requestingAdmin.getUsername() + " attempting to delete user ID: " + userIdToDelete);
            return userDao.deleteUser(userIdToDelete); // Call the DAO method added earlier
        } else {
            System.err.println("Permission denied for user deletion. Admin role required.");
            return false;
        }
    }
}