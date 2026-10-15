package com.urbanassist.dto.response;

import com.urbanassist.entity.Department;
import java.time.LocalDateTime;

public class DepartmentResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String contactEmail;
    private boolean isActive;
    private LocalDateTime createdAt;

    public DepartmentResponse() {
    }

    public static DepartmentResponse fromEntity(Department dept) {
        DepartmentResponse dto = new DepartmentResponse();
        dto.setId(dept.getId());
        dto.setName(dept.getName());
        dto.setCode(dept.getCode());
        dto.setDescription(dept.getDescription());
        dto.setContactEmail(dept.getContactEmail());
        dto.setActive(dept.isActive());
        dto.setCreatedAt(dept.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
