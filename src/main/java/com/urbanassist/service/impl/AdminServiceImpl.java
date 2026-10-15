package com.urbanassist.service.impl;

import com.urbanassist.dto.request.DepartmentRequest;
import com.urbanassist.dto.request.StaffCreateRequest;
import com.urbanassist.dto.response.DepartmentResponse;
import com.urbanassist.dto.response.UserResponse;
import com.urbanassist.entity.Department;
import com.urbanassist.entity.Role;
import com.urbanassist.entity.User;
import com.urbanassist.exception.ResourceNotFoundException;
import com.urbanassist.repository.DepartmentRepository;
import com.urbanassist.repository.UserRepository;
import com.urbanassist.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Role role, Pageable pageable) {
        if (role != null) {
            return userRepository.findByRole(role, pageable).map(UserResponse::fromEntity);
        }
        return userRepository.findAll(pageable).map(UserResponse::fromEntity);
    }

    @Override
    @Transactional
    public UserResponse createStaffAccount(StaffCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        User staff = new User(
                request.getFullName().trim(),
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_STAFF
        );
        staff.setPhone(request.getPhone());
        staff.setDepartment(department);

        User saved = userRepository.save(staff);
        return UserResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(isActive);
        User updated = userRepository.save(user);
        return UserResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode().toUpperCase().trim())) {
            throw new IllegalArgumentException("Department with code " + request.getCode() + " already exists.");
        }

        Department dept = new Department(
                request.getName().trim(),
                request.getCode().toUpperCase().trim(),
                request.getDescription(),
                request.getContactEmail()
        );
        if (request.getIsActive() != null) {
            dept.setActive(request.getIsActive());
        }

        Department saved = departmentRepository.save(dept);
        return DepartmentResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        dept.setName(request.getName().trim());
        dept.setCode(request.getCode().toUpperCase().trim());
        dept.setDescription(request.getDescription());
        dept.setContactEmail(request.getContactEmail());
        if (request.getIsActive() != null) {
            dept.setActive(request.getIsActive());
        }

        Department updated = departmentRepository.save(dept);
        return DepartmentResponse.fromEntity(updated);
    }
}
