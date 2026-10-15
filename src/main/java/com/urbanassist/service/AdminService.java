package com.urbanassist.service;

import com.urbanassist.dto.request.DepartmentRequest;
import com.urbanassist.dto.request.StaffCreateRequest;
import com.urbanassist.dto.response.DepartmentResponse;
import com.urbanassist.dto.response.UserResponse;
import com.urbanassist.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<UserResponse> getAllUsers(Role role, Pageable pageable);
    UserResponse createStaffAccount(StaffCreateRequest request);
    UserResponse toggleUserStatus(Long userId, boolean isActive);
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
}
