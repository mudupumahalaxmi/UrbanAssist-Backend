package com.urbanassist.controller;

import com.urbanassist.dto.response.DepartmentResponse;
import com.urbanassist.entity.Department;
import com.urbanassist.exception.ResourceNotFoundException;
import com.urbanassist.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentRepository.findByIsActiveTrue().stream()
                .map(DepartmentResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return ResponseEntity.ok(DepartmentResponse.fromEntity(dept));
    }
}
