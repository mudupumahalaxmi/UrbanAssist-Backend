package com.urbanassist.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FeedbackRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private Integer rating;

    private String comments;

    private Boolean isSatisfied = true;

    public FeedbackRequest() {
    }

    public FeedbackRequest(Integer rating, String comments, Boolean isSatisfied) {
        this.rating = rating;
        this.comments = comments;
        this.isSatisfied = isSatisfied;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getIsSatisfied() {
        return isSatisfied;
    }

    public void setIsSatisfied(Boolean satisfied) {
        isSatisfied = satisfied;
    }
}
