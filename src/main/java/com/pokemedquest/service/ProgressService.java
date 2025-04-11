package com.pokemedquest.service;

import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.model.Avatar;
import com.pokemedquest.model.TestProgress;
import com.pokemedquest.service.ProgressService.LevelUpResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ProgressService provides logic related to recording and retrieving CMAS test progress,
 * and handles avatar leveling based on scores.
 */
public class ProgressService {

    private final TestProgressDao testProgressDao;
    private final AvatarService avatarService; // Added dependency
    private static final int POINTS_PER_LEVEL = 50; // Level up threshold

    /**
     * Helper record to return results from recording progress.
     */
    public record LevelUpResult(TestProgress progress, boolean leveledUp, int newLevel, int gainedExperience) {}



    /**
     * Retrieves the complete test history for a specific user.
     *
     * @param userId The ID of the user.
     * @return A List of TestProgress objects (may be empty).
     */
    public List<TestProgress> getProgressHistoryForUser(int userId) {
        return testProgressDao.findProgressByUserId(userId);
    }

    /**
     * Retrieves all progress records from the database.
     *
     * @return A List of all TestProgress objects (may be empty).
     */
    public List<TestProgress> getAllProgressRecords() {
        return testProgressDao.findAllProgressRecords();
    }

    
    /**
     * Constructor for dependency injection.
     * @param testProgressDao The TestProgressDao instance.
     * @param avatarService The AvatarService instance. // Added parameter
     */
    public ProgressService(TestProgressDao testProgressDao, AvatarService avatarService) {
        this.testProgressDao = testProgressDao;
        this.avatarService = avatarService; // Initialize dependency
    }

    /**
     * Records a new CMAS test result for a user and updates avatar experience/level.
     * @param userId The ID of the user who took the test.
     * @param cmasScore The score achieved (this is the experience gained).
     * @return An Optional containing a LevelUpResult object if the progress record was saved,
     * indicating the progress record, whether a level up occurred, the new level,
     * and the score gained. Returns empty if saving the progress record failed.
     */
    public Optional<LevelUpResult> recordTestResult(int userId, int cmasScore) {
        if (cmasScore < 0) {
             System.err.println("Cannot record negative score for user ID: " + userId);
             return Optional.empty(); // Or handle as 0 points? For now, fail.
        }

        // Get the current timestamp
        LocalDateTime now = LocalDateTime.now();

        TestProgress newProgress = new TestProgress(userId, now, cmasScore);
        boolean progressSaved = testProgressDao.createTestProgress(newProgress);

        if (!progressSaved) {
            System.err.println("Failed to record test progress for user ID: " + userId);
            return Optional.empty();
        }

        // --- Now handle Avatar Experience and Leveling ---
        Optional<Avatar> avatarOpt = avatarService.getAvatarForUser(userId);

        if (!avatarOpt.isPresent()) {
            System.err.println("Could not find avatar for user ID: " + userId + " to update stats. Progress was recorded.");
            // Return progress record but indicate no level up was possible
            return Optional.of(new LevelUpResult(newProgress, false, -1, cmasScore)); // Use -1 level to indicate no avatar
        }

        Avatar avatar = avatarOpt.get();
        int currentLevel = avatar.getLevel();
        int currentExperience = avatar.getTotalExperience();

        int newTotalExperience = currentExperience + cmasScore;
        // Calculate the level *expected* based on the *new* total experience
        // Level 1: 0-49 xp, Level 2: 50-99 xp, Level 3: 100-149 xp, etc.
        // Formula: 1 + floor(totalExperience / POINTS_PER_LEVEL)
        int expectedLevel = 1 + (newTotalExperience / POINTS_PER_LEVEL);

        boolean leveledUp = false;
        int finalLevel = currentLevel; // Start with current level

        if (expectedLevel > currentLevel) {
            // Level Up occurred!
            avatar.setLevel(expectedLevel);
            avatar.setTotalExperience(newTotalExperience);
            boolean updateSuccess = avatarService.updateAvatarStats(avatar);
            if (updateSuccess) {
                 leveledUp = true;
                 finalLevel = expectedLevel;
                 System.out.println("User " + userId + " leveled up to " + finalLevel + " with " + newTotalExperience + " total XP."); // Log level up
            } else {
                 System.err.println("Progress recorded for user " + userId + ", but FAILED to save avatar level up stats.");
                 // Avatar object in memory has new stats, but DB might not. Revert? Or just report error?
                 // For now, report error and return state *before* update attempt for consistency.
                 // This means returning leveledUp = false and the original level.
                 avatar.setLevel(currentLevel); // Revert in-memory object
                 avatar.setTotalExperience(currentExperience); // Revert in-memory object (though newTotalExperience was calculated)
                 finalLevel = currentLevel;
                 leveledUp = false;
            }
        } else {
            // No level up, just update experience
            avatar.setTotalExperience(newTotalExperience);
            boolean updateSuccess = avatarService.updateAvatarStats(avatar);
             if (!updateSuccess) {
                  System.err.println("Progress recorded for user " + userId + ", but FAILED to save avatar experience update.");
                  // Revert in-memory object for consistency?
                  avatar.setTotalExperience(currentExperience);
             }
        }

        // Return the result containing the saved progress record and level-up status
        return Optional.of(new LevelUpResult(newProgress, leveledUp, finalLevel, cmasScore));
    }


    /**
     * Retrieves the complete test history for a user, ordered most recent first.
     * @param userId The ID of the user.
     * @return A List of TestProgress objects (may be empty).
     */
    
    /**
     * Gets the most recent test progress record for a user, if any.
     * @param userId The ID of the user.
     * @return An Optional containing the latest TestProgress, or empty if no records exist.
     */
    public Optional<TestProgress> getLatestProgressForUser(int userId) {
        List<TestProgress> history = getProgressHistoryForUser(userId);
        return history.stream().findFirst(); // More direct way to get first element if list is sorted
    }

    /**
    * Retrieves all progress records from the database. Used by Admin/Doctor.
    * NOTE: This might be inefficient if there are many records. Consider pagination later.
    * @return A list of all TestProgress records.
    */
   

    // --- TODO: Add methods for anomaly detection logic later ---

}