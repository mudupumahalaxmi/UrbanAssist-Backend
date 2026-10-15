package com.urbanassist.dto.response;

import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.SeverityLevel;

import java.math.BigDecimal;

/**
 * ComplaintAnalysisResponse — Publicly safe DTO returned to citizens
 * prior to final submission. Contains AI classification, estimated severity,
 * suggested department, priority explanation, and potential duplicate alert.
 */
public class ComplaintAnalysisResponse {

    private ComplaintCategory predictedCategory;
    private SeverityLevel estimatedSeverity;
    private int urgencyScore;
    private double confidence;
    private boolean fallbackUsed;

    private Long suggestedDepartmentId;
    private String suggestedDepartmentName;
    private String suggestedDepartmentCode;

    private BigDecimal estimatedPriorityScore;
    private String priorityExplanation;

    private boolean duplicateDetected;
    private Long duplicateOfComplaintId;
    private String duplicateOfComplaintNumber;
    private String duplicateOfTitle;
    private String duplicateOfStatus;
    private String duplicateOfAddress;
    private Double duplicateDistanceMeters;

    public ComplaintAnalysisResponse() {
    }

    public ComplaintCategory getPredictedCategory() {
        return predictedCategory;
    }

    public void setPredictedCategory(ComplaintCategory predictedCategory) {
        this.predictedCategory = predictedCategory;
    }

    public SeverityLevel getEstimatedSeverity() {
        return estimatedSeverity;
    }

    public void setEstimatedSeverity(SeverityLevel estimatedSeverity) {
        this.estimatedSeverity = estimatedSeverity;
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
        return fallbackUsed;
    }

    public void setFallbackUsed(boolean fallbackUsed) {
        this.fallbackUsed = fallbackUsed;
    }

    public Long getSuggestedDepartmentId() {
        return suggestedDepartmentId;
    }

    public void setSuggestedDepartmentId(Long suggestedDepartmentId) {
        this.suggestedDepartmentId = suggestedDepartmentId;
    }

    public String getSuggestedDepartmentName() {
        return suggestedDepartmentName;
    }

    public void setSuggestedDepartmentName(String suggestedDepartmentName) {
        this.suggestedDepartmentName = suggestedDepartmentName;
    }

    public String getSuggestedDepartmentCode() {
        return suggestedDepartmentCode;
    }

    public void setSuggestedDepartmentCode(String suggestedDepartmentCode) {
        this.suggestedDepartmentCode = suggestedDepartmentCode;
    }

    public BigDecimal getEstimatedPriorityScore() {
        return estimatedPriorityScore;
    }

    public void setEstimatedPriorityScore(BigDecimal estimatedPriorityScore) {
        this.estimatedPriorityScore = estimatedPriorityScore;
    }

    public String getPriorityExplanation() {
        return priorityExplanation;
    }

    public void setPriorityExplanation(String priorityExplanation) {
        this.priorityExplanation = priorityExplanation;
    }

    public boolean isDuplicateDetected() {
        return duplicateDetected;
    }

    public void setDuplicateDetected(boolean duplicateDetected) {
        this.duplicateDetected = duplicateDetected;
    }

    public Long getDuplicateOfComplaintId() {
        return duplicateOfComplaintId;
    }

    public void setDuplicateOfComplaintId(Long duplicateOfComplaintId) {
        this.duplicateOfComplaintId = duplicateOfComplaintId;
    }

    public String getDuplicateOfComplaintNumber() {
        return duplicateOfComplaintNumber;
    }

    public void setDuplicateOfComplaintNumber(String duplicateOfComplaintNumber) {
        this.duplicateOfComplaintNumber = duplicateOfComplaintNumber;
    }

    public String getDuplicateOfTitle() {
        return duplicateOfTitle;
    }

    public void setDuplicateOfTitle(String duplicateOfTitle) {
        this.duplicateOfTitle = duplicateOfTitle;
    }

    public String getDuplicateOfStatus() {
        return duplicateOfStatus;
    }

    public void setDuplicateOfStatus(String duplicateOfStatus) {
        this.duplicateOfStatus = duplicateOfStatus;
    }

    public String getDuplicateOfAddress() {
        return duplicateOfAddress;
    }

    public void setDuplicateOfAddress(String duplicateOfAddress) {
        this.duplicateOfAddress = duplicateOfAddress;
    }

    public Double getDuplicateDistanceMeters() {
        return duplicateDistanceMeters;
    }

    public void setDuplicateDistanceMeters(Double duplicateDistanceMeters) {
        this.duplicateDistanceMeters = duplicateDistanceMeters;
    }
}
