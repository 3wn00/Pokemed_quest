package com.pokemedquest.service;

import org.mindrot.jbcrypt.BCrypt;
import com.pokemedquest.dao.UserDao;
import com.pokemedquest.model.User;
import com.pokemedquest.model.Avatar;

import java.util.Optional;
import java.util.List;


public class AuthService {

    private final UserDao userDao;
    private final AvatarService avatarService; 

   
    public AuthService(UserDao userDao, AvatarService avatarService) {
        this.userDao = userDao;
        this.avatarService = avatarService;
    }

    public boolean deleteUserByUsername(String username) {
        return userDao.deleteUserByUsername(username);
    }
   
       
    public Optional<User> findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }



    
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

    
    public Optional<User> loginUser(String username, String plainPassword) {
        Optional<User> userOptional = userDao.findUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
                Optional<Avatar> avatarOptional = avatarService.getAvatarByUserId(user.getId());
                avatarOptional.ifPresent(avatar -> System.out.println("Welcome back, " + avatar.getAvatarName() + "!"));

                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}