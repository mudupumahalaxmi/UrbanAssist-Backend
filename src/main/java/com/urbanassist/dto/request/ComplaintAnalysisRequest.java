package com.urbanassist.dto.request;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * ComplaintAnalysisRequest — DTO for requesting AI pre-submission analysis
 * of a civic complaint (classification, severity estimation, and duplicate check).
 */
public class ComplaintAnalysisRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String description;

    private BigDecimal latitude;

    private BigDecimal longitude;

    public ComplaintAnalysisRequest() {
    }

    public ComplaintAnalysisRequest(String title, String description, BigDecimal latitude, BigDecimal longitude) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
