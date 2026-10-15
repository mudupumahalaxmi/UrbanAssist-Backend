package com.urbanassist.dto.response;

import com.urbanassist.entity.ComplaintTimeline;
import java.time.LocalDateTime;

public class ComplaintTimelineResponse {
    private Long id;
    private String previousStatus;
    private String newStatus;
    private String updatedByName;
    private String remarks;
    private LocalDateTime createdAt;

    public ComplaintTimelineResponse() {
    }

    public static ComplaintTimelineResponse fromEntity(ComplaintTimeline timeline) {
        ComplaintTimelineResponse dto = new ComplaintTimelineResponse();
        dto.setId(timeline.getId());
        dto.setPreviousStatus(timeline.getPreviousStatus());
        dto.setNewStatus(timeline.getNewStatus());
        if (timeline.getUpdatedBy() != null) {
            dto.setUpdatedByName(timeline.getUpdatedBy().getFullName());
        }
        dto.setRemarks(timeline.getRemarks());
        dto.setCreatedAt(timeline.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
