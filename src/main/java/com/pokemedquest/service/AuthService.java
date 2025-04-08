package com.pokemedquest.service;

import org.mindrot.jbcrypt.BCrypt;
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.model.User;
import com.pokemedquest.model.Avatar;

import java.util.Optional;

/**
 * AuthService provides authentication-related services like user registration and login.
 */
public class AuthService {

    private final UserDao userDao;
    private final AvatarService avatarService; // Add AvatarService dependency

    /**
     * Constructor for dependency injection.
     * Requires a UserDao and AvatarService instance to interact with the database.
     *
     * @param userDao The UserDao instance.
     * @param avatarService The AvatarService instance.
     */
    public AuthService(UserDao userDao, AvatarService avatarService) {
        this.userDao = userDao;
        this.avatarService = avatarService;
    }

    /**
     * Registers a new user and creates a default avatar for them.
     *
     * @param username The desired username.
     * @param plainPassword The user's chosen plain text password.
     * @param role The user's role (e.g., "child", "admin").
     * @return An Optional containing the newly created User (with ID) if successful,
     * otherwise an empty Optional.
     */
    public Optional<User> registerUser(String username, String plainPassword, String role) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    
        if (userDao.findUserByUsername(username).isPresent()) {
            System.err.println("Username '" + username + "' already exists.");
            return Optional.empty();
        }
    
        User newUser = new User(username, hashedPassword, role);
        boolean success = userDao.createUser(newUser);
    
        if (success) {
            return Optional.of(newUser);
        } else {
            return Optional.empty();
        }
    }

    /**
 * Attempts to log in a user.
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

        if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            // Retrieve the user's avatar using the updated method
            Optional<Avatar> avatarOptional = avatarService.getAvatarForUser(user.getId());
            avatarOptional.ifPresent(avatar -> System.out.println("Welcome back, " + avatar.getAvatarName() + "!"));

            return Optional.of(user);
        }
    }

    return Optional.empty();
}
}