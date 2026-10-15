package com.urbanassist.dto.response;

import com.urbanassist.entity.Complaint;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import com.urbanassist.entity.SeverityLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MapComplaintResponse — Publicly safe geospatial complaint DTO for Leaflet map markers.
 * Excludes citizen PII (name, phone, email) to preserve privacy.
 */
public class MapComplaintResponse {
    private Long id;
    private String complaintNumber;
    private String title;
    private ComplaintCategory category;
    private SeverityLevel severityLevel;
    private BigDecimal priorityScore;
    private ComplaintStatus status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String landmark;
    private String departmentName;
    private LocalDateTime createdAt;

    public MapComplaintResponse() {
    }

    public static MapComplaintResponse fromEntity(Complaint complaint) {
        MapComplaintResponse dto = new MapComplaintResponse();
        dto.setId(complaint.getId());
        dto.setComplaintNumber(complaint.getComplaintNumber());
        dto.setTitle(complaint.getTitle());
        dto.setCategory(complaint.getCategory());
        dto.setSeverityLevel(complaint.getSeverityLevel());
        dto.setPriorityScore(complaint.getPriorityScore());
        dto.setStatus(complaint.getStatus());
        dto.setLatitude(complaint.getLatitude());
        dto.setLongitude(complaint.getLongitude());
        dto.setAddress(complaint.getAddress());
        dto.setLandmark(complaint.getLandmark());
        if (complaint.getDepartment() != null) {
            dto.setDepartmentName(complaint.getDepartment().getName());
        }
        dto.setCreatedAt(complaint.getCreatedAt());
        return dto;
    }

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
