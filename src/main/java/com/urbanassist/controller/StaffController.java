package com.urbanassist.controller;

import com.urbanassist.dto.request.ResolutionRequest;
import com.urbanassist.dto.request.StatusUpdateRequest;
import com.urbanassist.dto.response.ComplaintDetailResponse;
import com.urbanassist.dto.response.ComplaintResponse;
import com.urbanassist.dto.response.StaffStatsResponse;
import com.urbanassist.entity.ComplaintStatus;
import com.urbanassist.entity.Role;
import com.urbanassist.entity.User;
import com.urbanassist.repository.UserRepository;
import com.urbanassist.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class StaffController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    public StaffController(ComplaintService complaintService, UserRepository userRepository) {
        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/queue")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ComplaintResponse>> getDepartmentQueue(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "priorityScore") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long effectiveDeptId;

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            // An admin can view a specific department if passed, or all departments if null
            effectiveDeptId = departmentId;
        } else {
            // Staff can view all departments if no specific departmentId is provided
            effectiveDeptId = departmentId;

        }

        Page<ComplaintResponse> queue = complaintService.getDepartmentQueue(
                effectiveDeptId, status, pageable
        );
        return ResponseEntity.ok(queue);
    }

    /**
     * GET /api/staff/stats — Returns complaint counts by status for the staff dashboard KPI cards.
     * Staff users are restricted to their assigned department. Admins may optionally filter by department.
     */
    @GetMapping("/stats")
    @Transactional(readOnly = true)
    public ResponseEntity<StaffStatsResponse> getDepartmentStats(
            @RequestParam(required = false) Long departmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long effectiveDeptId;

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            effectiveDeptId = departmentId;
        } else {
            if (currentUser.getDepartment() == null) {
                return ResponseEntity.ok(new StaffStatsResponse(0, 0, 0, 0, 0, 0));
            }
            // Staff can view all departments if no departmentId is provided
            effectiveDeptId = departmentId;
        }

        StaffStatsResponse stats = complaintService.getStaffStats(effectiveDeptId);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<ComplaintDetailResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ComplaintDetailResponse response = complaintService.updateStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/complaints/{id}/resolve", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ComplaintDetailResponse> resolveComplaint(
            @PathVariable Long id,
            @Valid @RequestPart("data") ResolutionRequest request,
            @RequestPart("photos") List<MultipartFile> photos,
            @AuthenticationPrincipal UserDetails userDetails) {

        ComplaintDetailResponse response = complaintService.resolveComplaint(
                id, request, photos, userDetails.getUsername()
        );
        return ResponseEntity.ok(response);
    }
}
