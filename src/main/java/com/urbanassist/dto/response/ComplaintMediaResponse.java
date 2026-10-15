package com.urbanassist.dto.response;

import com.urbanassist.entity.ComplaintMedia;
import com.urbanassist.entity.MediaStage;
import com.urbanassist.entity.MediaType;

import java.time.LocalDateTime;

public class ComplaintMediaResponse {
    private Long id;
    private String mediaUrl;
    private MediaType mediaType;
    private MediaStage stage;
    private String uploadedByName;
    private LocalDateTime createdAt;

    public ComplaintMediaResponse() {
    }

    public static ComplaintMediaResponse fromEntity(ComplaintMedia media) {
        ComplaintMediaResponse dto = new ComplaintMediaResponse();
        dto.setId(media.getId());
        dto.setMediaUrl(media.getMediaUrl());
        dto.setMediaType(media.getMediaType());
        dto.setStage(media.getStage());
        if (media.getUploadedBy() != null) {
            dto.setUploadedByName(media.getUploadedBy().getFullName());
        }
        dto.setCreatedAt(media.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaStage getStage() {
        return stage;
    }

    public void setStage(MediaStage stage) {
        this.stage = stage;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
