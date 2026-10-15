package com.urbanassist.dto.response;

public class DepartmentStatResponse {
    private String departmentName;
    private long count;

    public DepartmentStatResponse() {
    }

    public DepartmentStatResponse(String departmentName, long count) {
        this.departmentName = departmentName;
        this.count = count;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
