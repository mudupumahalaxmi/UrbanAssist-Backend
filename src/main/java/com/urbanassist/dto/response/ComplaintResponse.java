package com.urbanassist.dto.response;

import com.urbanassist.entity.Complaint;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import com.urbanassist.entity.SeverityLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ComplaintResponse {
    private Long id;
    private String complaintNumber;
    private String title;
    private String description;
    private ComplaintCategory category;
    private SeverityLevel severityLevel;
    private BigDecimal priorityScore;
    private ComplaintStatus status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String landmark;
    private String pincode;
    private boolean isDuplicate;
    private Long duplicateOfId;
    private String citizenName;
    private Long departmentId;
    private String departmentName;
    private String primaryImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplaintResponse() {
    }

    public static ComplaintResponse fromEntity(Complaint complaint) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setComplaintNumber(complaint.getComplaintNumber());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setSeverityLevel(complaint.getSeverityLevel());
        dto.setPriorityScore(complaint.getPriorityScore());
        dto.setStatus(complaint.getStatus());
        dto.setLatitude(complaint.getLatitude());
        dto.setLongitude(complaint.getLongitude());
        dto.setAddress(complaint.getAddress());
        dto.setLandmark(complaint.getLandmark());
        dto.setPincode(complaint.getPincode());
        dto.setDuplicate(complaint.isDuplicate());
        if (complaint.getDuplicateOf() != null) {
            dto.setDuplicateOfId(complaint.getDuplicateOf().getId());
        }
        if (complaint.getCitizen() != null) {
            dto.setCitizenName(complaint.getCitizen().getFullName());
        }
        if (complaint.getDepartment() != null) {
            dto.setDepartmentId(complaint.getDepartment().getId());
            dto.setDepartmentName(complaint.getDepartment().getName());
        }
        if (complaint.getMediaList() != null && !complaint.getMediaList().isEmpty()) {
            dto.setPrimaryImageUrl(complaint.getMediaList().get(0).getMediaUrl());
        }
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BigDecimal getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(BigDecimal priorityScore) {
        this.priorityScore = priorityScore;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public Long getDuplicateOfId() {
        return duplicateOfId;
    }

    public void setDuplicateOfId(Long duplicateOfId) {
        this.duplicateOfId = duplicateOfId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
