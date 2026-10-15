package com.urbanassist.dto.response;

import com.urbanassist.entity.Complaint;
import java.util.List;
import java.util.stream.Collectors;

public class ComplaintDetailResponse extends ComplaintResponse {

    private String audioUrl;
    private List<ComplaintMediaResponse> media;
    private List<ComplaintTimelineResponse> timeline;
    private CitizenFeedbackResponse feedback;

    public ComplaintDetailResponse() {
    }

    public static ComplaintDetailResponse fromEntity(Complaint complaint) {
        ComplaintDetailResponse dto = new ComplaintDetailResponse();

        // Populate base fields
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
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        // Detailed fields
        dto.setAudioUrl(complaint.getAudioUrl());
        if (complaint.getMediaList() != null) {
            dto.setMedia(complaint.getMediaList().stream()
                    .map(ComplaintMediaResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (complaint.getTimelineList() != null) {
            dto.setTimeline(complaint.getTimelineList().stream()
                    .map(ComplaintTimelineResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (complaint.getFeedback() != null) {
            dto.setFeedback(CitizenFeedbackResponse.fromEntity(complaint.getFeedback()));
        }

        return dto;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public List<ComplaintMediaResponse> getMedia() {
        return media;
    }

    public void setMedia(List<ComplaintMediaResponse> media) {
        this.media = media;
    }

    public List<ComplaintTimelineResponse> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<ComplaintTimelineResponse> timeline) {
        this.timeline = timeline;
    }

    public CitizenFeedbackResponse getFeedback() {
        return feedback;
    }

    public void setFeedback(CitizenFeedbackResponse feedback) {
        this.feedback = feedback;
    }
}
