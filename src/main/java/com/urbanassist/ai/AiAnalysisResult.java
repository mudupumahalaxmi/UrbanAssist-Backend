package com.urbanassist.ai;

import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.SeverityLevel;

public class AiAnalysisResult {
    private ComplaintCategory category;
    private SeverityLevel severityLevel;
    private int urgencyScore; // 1 to 5
    private double confidence;
    private boolean isFallbackUsed;

    public AiAnalysisResult() {
    }

    public AiAnalysisResult(ComplaintCategory category, SeverityLevel severityLevel, int urgencyScore, double confidence, boolean isFallbackUsed) {
        this.category = category;
        this.severityLevel = severityLevel;
        this.urgencyScore = urgencyScore;
        this.confidence = confidence;
        this.isFallbackUsed = isFallbackUsed;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }

    public int getUrgencyScore() {
        return urgencyScore;
    }

    public void setUrgencyScore(int urgencyScore) {
        this.urgencyScore = urgencyScore;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean isFallbackUsed() {
        return isFallbackUsed;
    }

    public void setFallbackUsed(boolean fallbackUsed) {
        isFallbackUsed = fallbackUsed;
    }
}
