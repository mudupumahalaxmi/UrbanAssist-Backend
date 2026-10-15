package com.urbanassist.dto.response;

import java.util.List;

public class AnalyticsSummaryResponse {
    private long totalComplaints;
    private long pendingComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long rejectedComplaints;
    private long duplicateComplaints;
    private double resolutionRatePercentage;
    private Double averageCitizenRating;
    private List<CategoryStatResponse> categoryBreakdown;
    private List<StatusStatResponse> statusBreakdown;
    private List<DepartmentStatResponse> departmentBreakdown;

    public AnalyticsSummaryResponse() {
    }

    public long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public long getInProgressComplaints() {
        return inProgressComplaints;
    }

    public void setInProgressComplaints(long inProgressComplaints) {
        this.inProgressComplaints = inProgressComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public long getRejectedComplaints() {
        return rejectedComplaints;
    }

    public void setRejectedComplaints(long rejectedComplaints) {
        this.rejectedComplaints = rejectedComplaints;
    }

    public long getDuplicateComplaints() {
        return duplicateComplaints;
    }

    public void setDuplicateComplaints(long duplicateComplaints) {
        this.duplicateComplaints = duplicateComplaints;
    }

    public double getResolutionRatePercentage() {
        return resolutionRatePercentage;
    }

    public void setResolutionRatePercentage(double resolutionRatePercentage) {
        this.resolutionRatePercentage = resolutionRatePercentage;
    }

    public Double getAverageCitizenRating() {
        return averageCitizenRating;
    }

    public void setAverageCitizenRating(Double averageCitizenRating) {
        this.averageCitizenRating = averageCitizenRating;
    }

    public List<CategoryStatResponse> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryStatResponse> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<StatusStatResponse> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(List<StatusStatResponse> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public List<DepartmentStatResponse> getDepartmentBreakdown() {
        return departmentBreakdown;
    }

    public void setDepartmentBreakdown(List<DepartmentStatResponse> departmentBreakdown) {
        this.departmentBreakdown = departmentBreakdown;
    }
}
