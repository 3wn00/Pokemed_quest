package com.pokemedquest.service;

import com.pokemedquest.dao.TestProgressDao;
import com.pokemedquest.model.TestProgress;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ProgressService provides logic related to recording and retrieving CMAS test progress.
 */
public class ProgressService {

    private final TestProgressDao testProgressDao;

    /**
     * Constructor for dependency injection.
     * @param testProgressDao The TestProgressDao instance.
     */
    public ProgressService(TestProgressDao testProgressDao) {
        this.testProgressDao = testProgressDao;
    }

    /**
     * Records a new CMAS test result for a user.
     * @param userId The ID of the user who took the test.
     * @param cmasScore The score achieved.
     * @return An Optional containing the created TestProgress record (with ID) if successful,
     * empty otherwise.
     */
    public Optional<TestProgress> recordTestResult(int userId, int cmasScore) {
        // Get the current timestamp
        LocalDateTime now = LocalDateTime.now();

        TestProgress newProgress = new TestProgress(userId, now, cmasScore);
        boolean success = testProgressDao.createTestProgress(newProgress);

        if (success) {
            // newProgress object should have its ID set
            return Optional.of(newProgress);
        } else {
            System.err.println("Failed to record test progress for user ID: " + userId);
            return Optional.empty();
        }
    }

    /**
     * Retrieves the complete test history for a user, ordered most recent first.
     * @param userId The ID of the user.
     * @return A List of TestProgress objects (may be empty).
     */
    public List<TestProgress> getProgressHistoryForUser(int userId) {
        List<TestProgress> history = testProgressDao.findProgressByUserId(userId);
        // The DAO already sorts, but defensive programming could re-sort or return unmodifiable list
        // return Collections.unmodifiableList(history);
        return history;
    }

    /**
     * Gets the most recent test progress record for a user, if any.
     * @param userId The ID of the user.
     * @return An Optional containing the latest TestProgress, or empty if no records exist.
     */
    public Optional<TestProgress> getLatestProgressForUser(int userId) {
        List<TestProgress> history = getProgressHistoryForUser(userId);
        if (history.isEmpty()) {
            return Optional.empty(); // No history found
        } else {
            // DAO returns sorted list (most recent first)
            return Optional.of(history.get(0));
        }
    }

    // --- TODO: Add methods for anomaly detection logic later ---
    /*
    public List<String> findPotentialAnomalies(int userId) {
        // 1. Get progress history
        // 2. Implement logic to detect anomalies (e.g., sudden drops, scores outside range)
        // 3. Return list of anomaly descriptions
        return Collections.emptyList(); // Placeholder
    }
    */
    /**
     * Detects potential anomalies in a user's CMAS score history.
     * Anomalies include sudden drops OR sudden improvements (30% change or more).
     * @param userId The ID of the user.
     * @return A list of descriptive anomaly messages.
     * 
     * A CMAS score is considered anomalous if it drops by more than 30% compared to the previous score for the same patient and exercise.
     */
    public List<String> findPotentialAnomalies(int userId) {
        List<TestProgress> history = getProgressHistoryForUser(userId);
        if (history.size() < 2) {
            return Collections.singletonList("Not enough data to detect anomalies.");
        }

        Collections.reverse(history); // Make oldest first, newest last
        List<String> anomalies = new java.util.ArrayList<>();

        for (int i = 1; i < history.size(); i++) {
            TestProgress prev = history.get(i - 1);
            TestProgress curr = history.get(i);

            int prevScore = prev.getCmasScore();
            int currScore = curr.getCmasScore();

            if (prevScore > 0) {
                double change = (double)(currScore - prevScore) / prevScore;

                if (change <= -0.3) {
                    anomalies.add(String.format(
                            "⚠️ Sudden drop: %d → %d between %s and %s (%.1f%% decrease)",
                            prevScore,
                            currScore,
                            prev.getTestTimestamp(),
                            curr.getTestTimestamp(),
                            change * 100
                    ));
                } else if (change >= 0.3) {
                    anomalies.add(String.format(
                            "📈 Sudden improvement: %d → %d between %s and %s (%.1f%% increase)",
                            prevScore,
                            currScore,
                            prev.getTestTimestamp(),
                            curr.getTestTimestamp(),
                            change * 100
                    ));
                }
            }
        }

        if (anomalies.isEmpty()) {
            anomalies.add("No anomalies or sudden changes detected.");
        }

        return anomalies;
    }
}