package com.pokemedquest.service;

import com.pokemedquest.dao.AvatarDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.User;
import java.util.List;
import java.util.Optional;


public class AvatarService {

    private final AvatarDao avatarDao;
    
    public AvatarService(AvatarDao avatarDao) {
        this.avatarDao = avatarDao;
    }

    public List<Avatar> getAllAvatars() {
        return avatarDao.getAllAvatars();
    }

    
    public Optional<Avatar> createDefaultAvatar(User user, String avatarName) {
        if (avatarDao.findAvatarByUserId(user.getId()).isPresent()) {
            System.err.println("User already has an avatar. Cannot create a new one.");
            return Optional.empty();
        }

       
        String defaultColor = "blue";
        String defaultAccessory = "none";

        
        Avatar newAvatar = new Avatar(user.getId(), avatarName, defaultColor, defaultAccessory);

        
        boolean success = avatarDao.createAvatar(newAvatar);

        if (success) {
            return avatarDao.findAvatarByUserId(user.getId());
        } else {
            System.err.println("Failed to create default avatar for user ID: " + user.getId());
            return Optional.empty();
        }
    }

    
    public boolean createAvatar(Avatar avatar) {
        boolean success = avatarDao.createAvatar(avatar);
        if (success) {
            System.out.println("Avatar created for user ID: " + avatar.getUserId());
        } else {
            System.err.println("Failed to create avatar for user ID: " + avatar.getUserId());
        }
        return success;
    }


    public Optional<Avatar> getAvatarForUser(int userId) {
        return avatarDao.findAvatarByUserId(userId);
    }

  
    public Optional<Avatar> getAvatarByUserId(int userId) {
        return avatarDao.findAvatarByUserId(userId);
    }

 
    public boolean updateAvatarCustomization(int userId, String newName, String newColor, String newAccessory) {
        Optional<Avatar> avatarOpt = avatarDao.findAvatarByUserId(userId);
        if (avatarOpt.isPresent()) {
            Avatar avatar = avatarOpt.get();

      
            avatar.setAvatarName(newName);
            avatar.setColor(newColor);
            avatar.setAccessory(newAccessory);
            return avatarDao.updateAvatarByUserId(avatar);
        } else {
            System.err.println("Cannot update customization: Avatar not found for user ID: " + userId);
            return false;
        }
    }

 
    public boolean updateAvatarStats(Avatar avatar) {
        boolean success = avatarDao.updateAvatarByUserId(avatar);
         if (!success) {
             System.err.println("Failed to update avatar stats in DB for avatar ID: " + avatar.getAvatarId());
         }
        return success;
    }

   
}