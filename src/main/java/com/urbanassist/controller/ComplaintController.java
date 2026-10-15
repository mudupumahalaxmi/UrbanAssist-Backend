package com.urbanassist.controller;

import com.urbanassist.dto.request.ComplaintAnalysisRequest;
import com.urbanassist.dto.request.ComplaintCreateRequest;
import com.urbanassist.dto.request.FeedbackRequest;
import com.urbanassist.dto.response.CitizenFeedbackResponse;
import com.urbanassist.dto.response.ComplaintAnalysisResponse;
import com.urbanassist.dto.response.ComplaintDetailResponse;
import com.urbanassist.dto.response.ComplaintResponse;
import com.urbanassist.service.ComplaintService;
import com.urbanassist.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final FileStorageService fileStorageService;

    public ComplaintController(ComplaintService complaintService, FileStorageService fileStorageService) {
        this.complaintService = complaintService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ComplaintDetailResponse> createComplaint(
            @Valid @RequestPart("data") ComplaintCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "audio", required = false) MultipartFile audio,
            @AuthenticationPrincipal UserDetails userDetails) {

        ComplaintDetailResponse response = complaintService.createComplaint(
                request, images, audio, userDetails.getUsername()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/pre-analyze")
    public ResponseEntity<ComplaintAnalysisResponse> preAnalyzeComplaint(
            @Valid @RequestBody ComplaintAnalysisRequest request) {
        ComplaintAnalysisResponse response = complaintService.preAnalyzeComplaint(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ComplaintResponse>> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal UserDetails userDetails) {

        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<ComplaintResponse> complaints = complaintService.getCitizenComplaints(
                userDetails.getUsername(), PageRequest.of(page, size, sort)
        );
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDetailResponse> getComplaintDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isStaffOrAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));

        ComplaintDetailResponse response = complaintService.getComplaintDetail(
                id, userDetails.getUsername(), isStaffOrAdmin
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<CitizenFeedbackResponse> submitFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CitizenFeedbackResponse response = complaintService.submitFeedback(
                id, request, userDetails.getUsername()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/media/{fileName:.+}")
    public ResponseEntity<Resource> getMediaFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = "application/octet-stream";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        } else if (fileName.endsWith(".webp")) {
            contentType = "image/webp";
        } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {
            contentType = "audio/mpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
