package com.urbanassist.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ResolutionRequest {

    @NotBlank(message = "Resolution remarks are required")
    private String resolutionRemarks;

    public ResolutionRequest() {
    }

    public ResolutionRequest(String resolutionRemarks) {
        this.resolutionRemarks = resolutionRemarks;
    }

    public String getResolutionRemarks() {
        return resolutionRemarks;
    }

    public void setResolutionRemarks(String resolutionRemarks) {
        this.resolutionRemarks = resolutionRemarks;
    }
}
