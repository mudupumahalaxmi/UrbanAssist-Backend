package com.urbanassist.service;

import com.urbanassist.dto.request.*;
import com.urbanassist.dto.response.*;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComplaintService {
    ComplaintDetailResponse createComplaint(ComplaintCreateRequest request, List<MultipartFile> images, MultipartFile audio, String citizenEmail);
    Page<ComplaintResponse> getCitizenComplaints(String citizenEmail, Pageable pageable);
    ComplaintDetailResponse getComplaintDetail(Long id, String currentUserEmail, boolean isStaffOrAdmin);
    CitizenFeedbackResponse submitFeedback(Long complaintId, FeedbackRequest request, String citizenEmail);
    Page<ComplaintResponse> getDepartmentQueue(Long departmentId, ComplaintStatus status, Pageable pageable);
    StaffStatsResponse getStaffStats(Long departmentId);
    ComplaintDetailResponse updateStatus(Long id, StatusUpdateRequest request, String staffEmail);
    ComplaintDetailResponse resolveComplaint(Long id, ResolutionRequest request, List<MultipartFile> resolutionPhotos, String staffEmail);
    ComplaintDetailResponse assignDepartment(Long complaintId, Long departmentId, String adminEmail);
    Page<ComplaintResponse> getAllComplaints(ComplaintStatus status, ComplaintCategory category, Long departmentId, Pageable pageable);
    List<MapComplaintResponse> getPublicMapComplaints();
    ComplaintAnalysisResponse preAnalyzeComplaint(ComplaintAnalysisRequest request);
}
