package com.pokemedquest.model;

import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 


public class TestProgress {

    private int progressId;    
    private int userId;       
    private LocalDateTime testTimestamp; 
    private int cmasScore;     

   
    public TestProgress(int userId, LocalDateTime testTimestamp, int cmasScore) {
        this.userId = userId;
        this.testTimestamp = testTimestamp;
        this.cmasScore = cmasScore;
    }

   
    public TestProgress(int progressId, int userId, LocalDateTime testTimestamp, int cmasScore) {
        this.progressId = progressId;
        this.userId = userId;
        this.testTimestamp = testTimestamp;
        this.cmasScore = cmasScore;
    }

    
    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTestTimestamp() {
        return testTimestamp;
    }

    public void setTestTimestamp(LocalDateTime testTimestamp) {
        this.testTimestamp = testTimestamp;
    }

    public int getCmasScore() {
        return cmasScore;
    }

    public void setCmasScore(int cmasScore) {
        this.cmasScore = cmasScore;
    }

    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = (testTimestamp != null) ? testTimestamp.format(formatter) : "N/A";

        return "TestProgress{" +
               "progressId=" + progressId +
               ", userId=" + userId +
               ", testTimestamp=" + formattedTimestamp +
               ", cmasScore=" + cmasScore +
               '}';
    }
}