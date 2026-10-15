package com.urbanassist.controller;

import com.urbanassist.dto.request.DepartmentRequest;
import com.urbanassist.dto.request.StaffCreateRequest;
import com.urbanassist.dto.response.ComplaintDetailResponse;
import com.urbanassist.dto.response.ComplaintResponse;
import com.urbanassist.dto.response.DepartmentResponse;
import com.urbanassist.dto.response.UserResponse;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import com.urbanassist.entity.Role;
import com.urbanassist.service.AdminService;
import com.urbanassist.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ComplaintService complaintService;

    public AdminController(AdminService adminService, ComplaintService complaintService) {
        this.adminService = adminService;
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public ResponseEntity<Page<ComplaintResponse>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<ComplaintResponse> complaints = complaintService.getAllComplaints(
                status, category, departmentId, PageRequest.of(page, size, sort)
        );
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/complaints/{id}/assign")
    public ResponseEntity<ComplaintDetailResponse> assignDepartment(
            @PathVariable Long id,
            @RequestParam Long departmentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ComplaintDetailResponse response = complaintService.assignDepartment(id, departmentId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> users = adminService.getAllUsers(role, PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }

    @PostMapping("/staff")
    public ResponseEntity<UserResponse> createStaffAccount(@Valid @RequestBody StaffCreateRequest request) {
        UserResponse response = adminService.createStaffAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {

        UserResponse response = adminService.toggleUserStatus(id, isActive);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = adminService.createDepartment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        DepartmentResponse response = adminService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }
}
