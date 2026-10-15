package com.urbanassist.service.impl;

import com.urbanassist.dto.response.*;
import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.ComplaintStatus;
import com.urbanassist.repository.CitizenFeedbackRepository;
import com.urbanassist.repository.ComplaintRepository;
import com.urbanassist.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ComplaintRepository complaintRepository;
    private final CitizenFeedbackRepository feedbackRepository;

    public AnalyticsServiceImpl(ComplaintRepository complaintRepository, CitizenFeedbackRepository feedbackRepository) {
        this.complaintRepository = complaintRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse getSummaryMetrics() {
        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();

        long total = complaintRepository.count();
        long pending = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS) + complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        long rejected = complaintRepository.countByStatus(ComplaintStatus.REJECTED);
        long duplicate = complaintRepository.countByIsDuplicateTrue();

        summary.setTotalComplaints(total);
        summary.setPendingComplaints(pending);
        summary.setInProgressComplaints(inProgress);
        summary.setResolvedComplaints(resolved);
        summary.setRejectedComplaints(rejected);
        summary.setDuplicateComplaints(duplicate);

        double rate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;
        summary.setResolutionRatePercentage(Math.round(rate * 100.0) / 100.0);

        Double avgRating = feedbackRepository.findAverageRating();
        summary.setAverageCitizenRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 5.0);

        // Category breakdown
        List<CategoryStatResponse> categoryStats = new ArrayList<>();
        List<Object[]> catRows = complaintRepository.countByCategoryGrouped();
        for (Object[] row : catRows) {
            ComplaintCategory cat = (ComplaintCategory) row[0];
            long count = ((Number) row[1]).longValue();
            categoryStats.add(new CategoryStatResponse(cat.name(), count));
        }
        summary.setCategoryBreakdown(categoryStats);

        // Status breakdown
        List<StatusStatResponse> statusStats = new ArrayList<>();
        List<Object[]> statusRows = complaintRepository.countByStatusGrouped();
        for (Object[] row : statusRows) {
            ComplaintStatus st = (ComplaintStatus) row[0];
            long count = ((Number) row[1]).longValue();
            statusStats.add(new StatusStatResponse(st.name(), count));
        }
        summary.setStatusBreakdown(statusStats);

        // Department breakdown
        List<DepartmentStatResponse> deptStats = new ArrayList<>();
        List<Object[]> deptRows = complaintRepository.countByDepartmentGrouped();
        for (Object[] row : deptRows) {
            String deptName = (String) row[0];
            long count = ((Number) row[1]).longValue();
            deptStats.add(new DepartmentStatResponse(deptName, count));
        }
        summary.setDepartmentBreakdown(deptStats);

        return summary;
    }
}
