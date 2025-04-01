package com.pokemedquest.dao;

import com.pokemedquest.model.TestProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TestProgressDao (Data Access Object) for TestProgress entities.
 * Handles database operations related to CMAS test results.
 */
public class TestProgressDao {

    // SQL query strings - Adjust table/column names as needed
    private static final String INSERT_PROGRESS_SQL = "INSERT INTO test_progress (user_id, test_timestamp, cmas_score) VALUES (?, ?, ?)";
    private static final String SELECT_PROGRESS_BY_USER_SQL = "SELECT progress_id, user_id, test_timestamp, cmas_score FROM test_progress WHERE user_id = ? ORDER BY test_timestamp DESC"; // Corrected placeholder
    private static final String SELECT_PROGRESS_BY_ID_SQL = "SELECT progress_id, user_id, test_timestamp, cmas_score FROM test_progress WHERE progress_id = ?";
    private static final String UPDATE_PROGRESS_SQL = "UPDATE test_progress SET user_id = ?, test_timestamp = ?, cmas_score = ? WHERE progress_id = ?"; // Added
    private static final String DELETE_PROGRESS_SQL = "DELETE FROM test_progress WHERE progress_id = ?"; // Added


    /**
     * Creates a new test progress record in the database.
     * Updates the passed TestProgress object with the auto-generated ID.
     *
     * @param progress The TestProgress object to save.
     * @return true if the record was created successfully, false otherwise.
     */
    public boolean createTestProgress(TestProgress progress) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROGRESS_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, progress.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(progress.getTestTimestamp()));
            preparedStatement.setInt(3, progress.getCmasScore());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        progress.setProgressId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating test progress record: " + e.getMessage());
        }
        return false;
    }

    /**
     * Finds all test progress records for a specific user, ordered by most recent first.
     *
     * @param userId The ID of the user whose progress records to find.
     * @return A List of TestProgress objects (potentially empty).
     */
    public List<TestProgress> findProgressByUserId(int userId) {
        List<TestProgress> progressList = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROGRESS_BY_USER_SQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    int progressId = rs.getInt("progress_id");
                    LocalDateTime timestamp = rs.getTimestamp("test_timestamp").toLocalDateTime();
                    int cmasScore = rs.getInt("cmas_score");
                    TestProgress progress = new TestProgress(progressId, userId, timestamp, cmasScore);
                    progressList.add(progress);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding progress records by user ID: " + e.getMessage());
        }
        return progressList;
    }

    /**
     * Finds a specific test progress record by its unique ID.
     *
     * @param progressId The ID of the progress record to find.
     * @return An Optional containing the TestProgress if found, otherwise an empty Optional.
     */
    public Optional<TestProgress> findProgressById(int progressId) {
        TestProgress progress = null;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PROGRESS_BY_ID_SQL)) {

            preparedStatement.setInt(1, progressId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    LocalDateTime timestamp = rs.getTimestamp("test_timestamp").toLocalDateTime();
                    int cmasScore = rs.getInt("cmas_score");
                    progress = new TestProgress(progressId, userId, timestamp, cmasScore);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding progress record by ID: " + e.getMessage());
        }
        return Optional.ofNullable(progress);
    }

    /**
     * Updates an existing test progress record in the database.
     *
     * @param progress The TestProgress object containing the updated data (must have a valid progressId).
     * @return true if the update was successful (at least one row affected), false otherwise.
     */
    public boolean updateTestProgress(TestProgress progress) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PROGRESS_SQL)) {

            preparedStatement.setInt(1, progress.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(progress.getTestTimestamp()));
            preparedStatement.setInt(3, progress.getCmasScore());
            preparedStatement.setInt(4, progress.getProgressId()); // progressId for WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating test progress record: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a specific test progress record from the database.
     *
     * @param progressId The ID of the progress record to delete.
     * @return true if the deletion was successful (at least one row affected), false otherwise.
     */
    public boolean deleteTestProgress(int progressId) {
         try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PROGRESS_SQL)) {

            preparedStatement.setInt(1, progressId); // progressId for WHERE clause

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting test progress record: " + e.getMessage());
        }
        return false;
    }
}