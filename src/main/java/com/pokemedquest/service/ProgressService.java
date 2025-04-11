package com.pokemedquest.service;

import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.TestProgress;
import com.pokemedquest.service.ProgressService.LevelUpResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class ProgressService {

    private final TestProgressDao testProgressDao;
    private final AvatarService avatarService; 
    private static final int POINTS_PER_LEVEL = 50; 

 
    public record LevelUpResult(TestProgress progress, boolean leveledUp, int newLevel, int gainedExperience) {}



    public List<TestProgress> getProgressHistoryForUser(int userId) {
        return testProgressDao.findProgressByUserId(userId);
    }

    public List<TestProgress> getAllProgressRecords() {
        return testProgressDao.findAllProgressRecords();
    }

    
 
    public ProgressService(TestProgressDao testProgressDao, AvatarService avatarService) {
        this.testProgressDao = testProgressDao;
        this.avatarService = avatarService; 
    }


    public Optional<LevelUpResult> recordTestResult(int userId, int cmasScore) {
        if (cmasScore < 0) {
             System.err.println("Cannot record negative score for user ID: " + userId);
             return Optional.empty(); 
        }

        
        LocalDateTime now = LocalDateTime.now();

        TestProgress newProgress = new TestProgress(userId, now, cmasScore);
        boolean progressSaved = testProgressDao.createTestProgress(newProgress);

        if (!progressSaved) {
            System.err.println("Failed to record test progress for user ID: " + userId);
            return Optional.empty();
        }

        
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(userId);

        if (!avatarOpt.isPresent()) {
            System.err.println("Could not find avatar for user ID: " + userId + " to update stats. Progress was recorded.");
            
            return Optional.of(new LevelUpResult(newProgress, false, -1, cmasScore)); // Use -1 level to indicate no avatar
        }

        Avatar avatar = avatarOpt.get();
        int currentLevel = avatar.getLevel();
        int currentExperience = avatar.getTotalExperience();

        int newTotalExperience = currentExperience + cmasScore;
        int expectedLevel = 1 + (newTotalExperience / POINTS_PER_LEVEL);

        boolean leveledUp = false;
        int finalLevel = currentLevel; 

        if (expectedLevel > currentLevel) {
            avatar.setLevel(expectedLevel);
            avatar.setTotalExperience(newTotalExperience);
            boolean updateSuccess = avatarService.updateAvatarStats(avatar);
            if (updateSuccess) {
                 leveledUp = true;
                 finalLevel = expectedLevel;
                 System.out.println("User " + userId + " leveled up to " + finalLevel + " with " + newTotalExperience + " total XP."); // Log level up
            } else {
                 System.err.println("Progress recorded for user " + userId + ", but FAILED to save avatar level up stats.");
                 avatar.setLevel(currentLevel); 
                 avatar.setTotalExperience(currentExperience); 
                 finalLevel = currentLevel;
                 leveledUp = false;
            }
        } else {
            avatar.setTotalExperience(newTotalExperience);
            boolean updateSuccess = avatarService.updateAvatarStats(avatar);
             if (!updateSuccess) {
                  System.err.println("Progress recorded for user " + userId + ", but FAILED to save avatar experience update.");
                  
                  avatar.setTotalExperience(currentExperience);
             }
        }

     
        return Optional.of(new LevelUpResult(newProgress, leveledUp, finalLevel, cmasScore));
    }



    
 
    public Optional<TestProgress> getLatestProgressForUser(int userId) {
        List<TestProgress> history = getProgressHistoryForUser(userId);
        return history.stream().findFirst();  }



}