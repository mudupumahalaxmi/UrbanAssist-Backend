package com.urbanassist.service.impl;

import com.urbanassist.ai.AiAnalysisResult;
import com.urbanassist.ai.AiService;
import com.urbanassist.ai.DuplicateDetectionService;
import com.urbanassist.ai.PriorityScoringService;
import com.urbanassist.dto.request.*;
import com.urbanassist.dto.response.*;
import com.urbanassist.entity.*;
import com.urbanassist.exception.ResourceNotFoundException;
import com.urbanassist.exception.UnauthorizedException;
import com.urbanassist.repository.*;
import com.urbanassist.service.ComplaintService;
import com.urbanassist.service.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintMediaRepository mediaRepository;
    private final ComplaintTimelineRepository timelineRepository;
    private final CitizenFeedbackRepository feedbackRepository;
    private final FileStorageService fileStorageService;
    private final AiService aiService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final PriorityScoringService priorityScoringService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            ComplaintMediaRepository mediaRepository,
            ComplaintTimelineRepository timelineRepository,
            CitizenFeedbackRepository feedbackRepository,
            FileStorageService fileStorageService,
            AiService aiService,
            DuplicateDetectionService duplicateDetectionService,
            PriorityScoringService priorityScoringService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.mediaRepository = mediaRepository;
        this.timelineRepository = timelineRepository;
        this.feedbackRepository = feedbackRepository;
        this.fileStorageService = fileStorageService;
        this.aiService = aiService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.priorityScoringService = priorityScoringService;
    }

    @Override
    @Transactional
    public ComplaintDetailResponse createComplaint(
            ComplaintCreateRequest request,
            List<MultipartFile> images,
            MultipartFile audio,
            String citizenEmail) {

        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", citizenEmail));

        // 1. AI Analysis (Category classification & severity estimation)
        AiAnalysisResult aiResult = aiService.analyzeComplaint(request.getTitle(), request.getDescription());
        ComplaintCategory category = request.getCategory() != null ? request.getCategory() : aiResult.getCategory();

        // 2. Department Auto-Assignment based on Category
        String deptCode = category.name();
        Department department = departmentRepository.findByCode(deptCode)
                .orElse(departmentRepository.findAll().stream().findFirst().orElse(null));

        // 3. Duplicate Detection via Haversine Proximity + Text Similarity
        Optional<Complaint> duplicateParent = duplicateDetectionService.checkForDuplicate(
                request.getLatitude(), request.getLongitude(), request.getTitle(), request.getDescription()
        );

        long duplicateCount = 0;
        if (duplicateParent.isPresent()) {
            duplicateCount = complaintRepository.countDuplicatesForComplaint(duplicateParent.get().getId()) + 1;
        }

        // 4. Dynamic Priority Score Calculation
        BigDecimal priorityScore = priorityScoringService.calculatePriorityScore(
                aiResult.getSeverityLevel(),
                aiResult.getUrgencyScore(),
                duplicateCount,
                LocalDateTime.now()
        );

        // 5. Build Complaint Entity
        Complaint complaint = new Complaint();
        complaint.setComplaintNumber("URA-" + System.currentTimeMillis() % 1000000);
        complaint.setCitizen(citizen);
        complaint.setDepartment(department);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(category);
        complaint.setSeverityLevel(aiResult.getSeverityLevel());
        complaint.setPriorityScore(priorityScore);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());
        complaint.setAddress(request.getAddress());
        complaint.setLandmark(request.getLandmark());
        complaint.setPincode(request.getPincode());

        if (duplicateParent.isPresent()) {
            complaint.setDuplicate(true);
            complaint.setDuplicateOf(duplicateParent.get());
        }

        // Save Complaint first to get generated ID
        Complaint savedComplaint = complaintRepository.save(complaint);

        // 6. Handle Audio File Upload
        if (audio != null && !audio.isEmpty()) {
            String audioFileName = fileStorageService.storeFile(audio, "audio");
            savedComplaint.setAudioUrl("/api/complaints/media/" + audioFileName);
        }

        // 7. Handle Photographs (Before-Submission Photos)
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                if (!img.isEmpty()) {
                    String fileName = fileStorageService.storeFile(img, "photos");
                    ComplaintMedia media = new ComplaintMedia(
                            savedComplaint,
                            "/api/complaints/media/" + fileName,
                            MediaType.IMAGE,
                            MediaStage.BEFORE_SUBMISSION,
                            citizen
                    );
                    savedComplaint.addMedia(media);
                }
            }
        }

        // 8. Initial Audit Timeline Log
        ComplaintTimeline timeline = new ComplaintTimeline(
                savedComplaint,
                null,
                ComplaintStatus.PENDING.name(),
                citizen,
                "Complaint filed by citizen. AI Severity: " + aiResult.getSeverityLevel() +
                (aiResult.isFallbackUsed() ? " (Rule engine)" : " (HF Engine)")
        );
        savedComplaint.addTimeline(timeline);

        // Save again with media & timeline
        savedComplaint = complaintRepository.save(savedComplaint);

        // If duplicate, recalculate parent complaint's priority score to boost it
        if (duplicateParent.isPresent()) {
            Complaint parent = duplicateParent.get();
            long newParentDupCount = complaintRepository.countDuplicatesForComplaint(parent.getId());
            BigDecimal updatedParentPriority = priorityScoringService.recalculateForComplaint(parent, newParentDupCount);
            parent.setPriorityScore(updatedParentPriority);
            complaintRepository.save(parent);
        }

        return ComplaintDetailResponse.fromEntity(savedComplaint);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getCitizenComplaints(String citizenEmail, Pageable pageable) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", citizenEmail));
        return complaintRepository.findByCitizenId(citizen.getId(), pageable)
                .map(ComplaintResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintDetailResponse getComplaintDetail(Long id, String currentUserEmail, boolean isStaffOrAdmin) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        // Enforce authorization: citizens can only view their own complaints
        if (!isStaffOrAdmin && !complaint.getCitizen().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new UnauthorizedException("You are not authorized to view another citizen's complaint details.");
        }

        return ComplaintDetailResponse.fromEntity(complaint);
    }

    @Override
    @Transactional
    public CitizenFeedbackResponse submitFeedback(Long complaintId, FeedbackRequest request, String citizenEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));

        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", citizenEmail));

        if (!complaint.getCitizen().getId().equals(citizen.getId())) {
            throw new UnauthorizedException("You can only submit feedback for your own complaints.");
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            throw new IllegalArgumentException("Feedback can only be submitted for RESOLVED complaints.");
        }

        if (feedbackRepository.existsByComplaintId(complaintId)) {
            throw new IllegalArgumentException("Feedback has already been submitted for this complaint.");
        }

        CitizenFeedback feedback = new CitizenFeedback(
                complaint,
                citizen,
                request.getRating(),
                request.getComments(),
                request.getIsSatisfied() != null ? request.getIsSatisfied() : true
        );

        CitizenFeedback savedFeedback = feedbackRepository.save(feedback);
        complaint.setFeedback(savedFeedback);
        complaintRepository.save(complaint);

        return CitizenFeedbackResponse.fromEntity(savedFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getDepartmentQueue(Long departmentId, ComplaintStatus status, Pageable pageable) {
        if (departmentId != null) {
            if (status != null) {
                return complaintRepository.findByDepartmentIdAndStatus(departmentId, status, pageable)
                        .map(ComplaintResponse::fromEntity);
            }
            return complaintRepository.findByDepartmentId(departmentId, pageable)
                    .map(ComplaintResponse::fromEntity);
        } else {
            if (status != null) {
                return complaintRepository.findByStatus(status, pageable)
                        .map(ComplaintResponse::fromEntity);
            }
            return complaintRepository.findAll(pageable)
                    .map(ComplaintResponse::fromEntity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StaffStatsResponse getStaffStats(Long departmentId) {
        long pending, assigned, inProgress, resolved, rejected, total;

        if (departmentId != null) {
            pending    = complaintRepository.countByDepartmentIdAndStatus(departmentId, ComplaintStatus.PENDING);
            assigned   = complaintRepository.countByDepartmentIdAndStatus(departmentId, ComplaintStatus.ASSIGNED);
            inProgress = complaintRepository.countByDepartmentIdAndStatus(departmentId, ComplaintStatus.IN_PROGRESS);
            resolved   = complaintRepository.countByDepartmentIdAndStatus(departmentId, ComplaintStatus.RESOLVED);
            rejected   = complaintRepository.countByDepartmentIdAndStatus(departmentId, ComplaintStatus.REJECTED);
            total      = complaintRepository.countByDepartmentId(departmentId);
        } else {
            pending    = complaintRepository.countByStatus(ComplaintStatus.PENDING);
            assigned   = complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
            inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
            resolved   = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
            rejected   = complaintRepository.countByStatus(ComplaintStatus.REJECTED);
            total      = complaintRepository.count();
        }

        return new StaffStatsResponse(total, pending, assigned, inProgress, resolved, rejected);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse updateStatus(Long id, StatusUpdateRequest request, String staffEmail) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", staffEmail));

        // Enforce department authorization: ROLE_STAFF can only update complaints within their assigned department,
        // except for the designated cross‑department staff account (staff@urbanassist.gov)
        if (staff.getRole() == Role.ROLE_STAFF && !"staff@urbanassist.gov".equalsIgnoreCase(staff.getEmail())) {
            if (staff.getDepartment() == null || complaint.getDepartment() == null ||
                    !staff.getDepartment().getId().equals(complaint.getDepartment().getId())) {
                throw new UnauthorizedException("You are not authorized to update complaints outside your assigned department.");
            }
        }

        String oldStatus = complaint.getStatus().name();
        complaint.setStatus(request.getStatus());

        ComplaintTimeline timeline = new ComplaintTimeline(
                complaint,
                oldStatus,
                request.getStatus().name(),
                staff,
                request.getRemarks() != null ? request.getRemarks() : "Status updated to " + request.getStatus()
        );
        complaint.addTimeline(timeline);

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintDetailResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse resolveComplaint(
            Long id,
            ResolutionRequest request,
            List<MultipartFile> resolutionPhotos,
            String staffEmail) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", staffEmail));

        // Enforce department authorization: ROLE_STAFF can only resolve complaints within their assigned department,
        // except for the designated cross‑department staff account (staff@urbanassist.gov)
        if (staff.getRole() == Role.ROLE_STAFF && !"staff@urbanassist.gov".equalsIgnoreCase(staff.getEmail())) {
            if (staff.getDepartment() == null || complaint.getDepartment() == null ||
                    !staff.getDepartment().getId().equals(complaint.getDepartment().getId())) {
                throw new UnauthorizedException("You are not authorized to resolve complaints outside your assigned department.");
            }
        }

        if (resolutionPhotos == null || resolutionPhotos.isEmpty() || resolutionPhotos.stream().allMatch(MultipartFile::isEmpty)) {
            throw new IllegalArgumentException("At least one verified after-resolution photograph is required to resolve a complaint.");
        }

        String oldStatus = complaint.getStatus().name();
        complaint.setStatus(ComplaintStatus.RESOLVED);

        // Upload Resolution Photos (AFTER_RESOLUTION)
        for (MultipartFile photo : resolutionPhotos) {
            if (!photo.isEmpty()) {
                String fileName = fileStorageService.storeFile(photo, "resolution");
                ComplaintMedia media = new ComplaintMedia(
                        complaint,
                        "/api/complaints/media/" + fileName,
                        MediaType.IMAGE,
                        MediaStage.AFTER_RESOLUTION,
                        staff
                );
                complaint.addMedia(media);
            }
        }

        ComplaintTimeline timeline = new ComplaintTimeline(
                complaint,
                oldStatus,
                ComplaintStatus.RESOLVED.name(),
                staff,
                "Complaint resolved: " + request.getResolutionRemarks()
        );
        complaint.addTimeline(timeline);

        Complaint resolved = complaintRepository.save(complaint);
        return ComplaintDetailResponse.fromEntity(resolved);
    }

    @Override
    @Transactional
    public ComplaintDetailResponse assignDepartment(Long complaintId, Long departmentId, String adminEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        complaint.setDepartment(department);
        if (complaint.getStatus() == ComplaintStatus.PENDING) {
            complaint.setStatus(ComplaintStatus.ASSIGNED);
        }

        ComplaintTimeline timeline = new ComplaintTimeline(
                complaint,
                complaint.getStatus().name(),
                ComplaintStatus.ASSIGNED.name(),
                admin,
                "Re-assigned to department: " + department.getName()
        );
        complaint.addTimeline(timeline);

        Complaint updated = complaintRepository.save(complaint);
        return ComplaintDetailResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getAllComplaints(
            ComplaintStatus status,
            ComplaintCategory category,
            Long departmentId,
            Pageable pageable) {

        Specification<Complaint> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (departmentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId));
        }

        return complaintRepository.findAll(spec, pageable).map(ComplaintResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MapComplaintResponse> getPublicMapComplaints() {
        return complaintRepository.findAll().stream()
                .map(MapComplaintResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintAnalysisResponse preAnalyzeComplaint(ComplaintAnalysisRequest request) {
        String title = request.getTitle() != null ? request.getTitle() : "";
        String desc = request.getDescription() != null ? request.getDescription() : "";

        // 1. AI Analysis
        AiAnalysisResult aiResult = aiService.analyzeComplaint(title, desc);

        // 2. Suggested Department based on AI Category
        Department department = departmentRepository.findByCode(aiResult.getCategory().name())
                .orElse(departmentRepository.findAll().stream().findFirst().orElse(null));

        // 3. Duplicate Detection (if GPS coordinates provided)
        Optional<Complaint> duplicate = Optional.empty();
        long duplicateCount = 0;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            duplicate = duplicateDetectionService.checkForDuplicate(
                    request.getLatitude(), request.getLongitude(), title, desc
            );
            if (duplicate.isPresent()) {
                duplicateCount = complaintRepository.countDuplicatesForComplaint(duplicate.get().getId()) + 1;
            }
        }

        // 4. Formulated Priority Calculation
        BigDecimal estimatedPriority = priorityScoringService.calculatePriorityScore(
                aiResult.getSeverityLevel(),
                aiResult.getUrgencyScore(),
                duplicateCount,
                LocalDateTime.now()
        );

        // 5. Plain-language priority breakdown
        String explanation = String.format(
                "Estimated priority score of %.1f/100 based on %s severity, urgency rating %d/5%s.",
                estimatedPriority.doubleValue(),
                aiResult.getSeverityLevel(),
                aiResult.getUrgencyScore(),
                duplicate.isPresent() ? String.format(", and duplicate signal from nearby issue %s", duplicate.get().getComplaintNumber()) : ""
        );

        // 6. Build response DTO
        ComplaintAnalysisResponse response = new ComplaintAnalysisResponse();
        response.setPredictedCategory(aiResult.getCategory());
        response.setEstimatedSeverity(aiResult.getSeverityLevel());
        response.setUrgencyScore(aiResult.getUrgencyScore());
        response.setConfidence(aiResult.getConfidence());
        response.setFallbackUsed(aiResult.isFallbackUsed());

        if (department != null) {
            response.setSuggestedDepartmentId(department.getId());
            response.setSuggestedDepartmentName(department.getName());
            response.setSuggestedDepartmentCode(department.getCode());
        }

        response.setEstimatedPriorityScore(estimatedPriority);
        response.setPriorityExplanation(explanation);

        if (duplicate.isPresent()) {
            Complaint d = duplicate.get();
            response.setDuplicateDetected(true);
            response.setDuplicateOfComplaintId(d.getId());
            response.setDuplicateOfComplaintNumber(d.getComplaintNumber());
            response.setDuplicateOfTitle(d.getTitle());
            response.setDuplicateOfStatus(d.getStatus() != null ? d.getStatus().name() : "PENDING");
            response.setDuplicateOfAddress(d.getAddress());
            if (request.getLatitude() != null && request.getLongitude() != null && d.getLatitude() != null && d.getLongitude() != null) {
                response.setDuplicateDistanceMeters(duplicateDetectionService.calculateHaversineDistance(
                        request.getLatitude().doubleValue(), request.getLongitude().doubleValue(),
                        d.getLatitude().doubleValue(), d.getLongitude().doubleValue()
                ));
            }
        } else {
            response.setDuplicateDetected(false);
        }

        return response;
    }
}
