package com.urbanassist.repository;

import com.urbanassist.entity.Complaint;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {

    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    Page<Complaint> findByCitizenId(Long citizenId, Pageable pageable);

    Page<Complaint> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Complaint> findByDepartmentIdAndStatus(Long departmentId, ComplaintStatus status, Pageable pageable);

    Page<Complaint> findByStatus(ComplaintStatus status, Pageable pageable);

    long countByStatus(ComplaintStatus status);

    long countByDepartmentIdAndStatus(Long departmentId, ComplaintStatus status);

    long countByCategory(ComplaintCategory category);

    long countByDepartmentId(Long departmentId);

    long countByIsDuplicateTrue();

    @Query("SELECT c FROM Complaint c WHERE c.status <> 'RESOLVED' AND c.status <> 'REJECTED' AND " +
           "c.latitude BETWEEN :minLat AND :maxLat AND c.longitude BETWEEN :minLng AND :maxLng")
    List<Complaint> findActiveInBoundingBox(
        @Param("minLat") BigDecimal minLat,
        @Param("maxLat") BigDecimal maxLat,
        @Param("minLng") BigDecimal minLng,
        @Param("maxLng") BigDecimal maxLng
    );

    @Query("SELECT c.category as category, COUNT(c) as count FROM Complaint c GROUP BY c.category")
    List<Object[]> countByCategoryGrouped();

    @Query("SELECT c.status as status, COUNT(c) as count FROM Complaint c GROUP BY c.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT d.name as departmentName, COUNT(c) as count FROM Complaint c JOIN c.department d GROUP BY d.name")
    List<Object[]> countByDepartmentGrouped();

    @Query("SELECT c.severityLevel as severity, COUNT(c) as count FROM Complaint c GROUP BY c.severityLevel")
    List<Object[]> countBySeverityGrouped();

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.duplicateOf.id = :parentId")
    long countDuplicatesForComplaint(@Param("parentId") Long parentId);
}
