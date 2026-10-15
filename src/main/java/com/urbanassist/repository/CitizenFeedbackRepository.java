package com.urbanassist.repository;

import com.urbanassist.entity.CitizenFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenFeedbackRepository extends JpaRepository<CitizenFeedback, Long> {
    Optional<CitizenFeedback> findByComplaintId(Long complaintId);
    boolean existsByComplaintId(Long complaintId);

    @Query("SELECT AVG(f.rating) FROM CitizenFeedback f")
    Double findAverageRating();
}
