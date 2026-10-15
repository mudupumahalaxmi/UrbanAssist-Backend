package com.urbanassist.repository;

import com.urbanassist.entity.ComplaintMedia;
import com.urbanassist.entity.MediaStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintMediaRepository extends JpaRepository<ComplaintMedia, Long> {
    List<ComplaintMedia> findByComplaintId(Long complaintId);
    List<ComplaintMedia> findByComplaintIdAndStage(Long complaintId, MediaStage stage);
}
