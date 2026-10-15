package com.urbanassist.dto.response;

import com.urbanassist.entity.CitizenFeedback;
import java.time.LocalDateTime;

public class CitizenFeedbackResponse {
    private Long id;
    private Long complaintId;
    private String citizenName;
    private int rating;
    private String comments;
    private boolean isSatisfied;
    private LocalDateTime createdAt;

    public CitizenFeedbackResponse() {
    }

    public static CitizenFeedbackResponse fromEntity(CitizenFeedback feedback) {
        CitizenFeedbackResponse dto = new CitizenFeedbackResponse();
        dto.setId(feedback.getId());
        if (feedback.getComplaint() != null) {
            dto.setComplaintId(feedback.getComplaint().getId());
        }
        if (feedback.getCitizen() != null) {
            dto.setCitizenName(feedback.getCitizen().getFullName());
        }
        dto.setRating(feedback.getRating());
        dto.setComments(feedback.getComments());
        dto.setSatisfied(feedback.isSatisfied());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isSatisfied() {
        return isSatisfied;
    }

    public void setSatisfied(boolean satisfied) {
        isSatisfied = satisfied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
