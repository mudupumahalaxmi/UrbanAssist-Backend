package com.urbanassist.ai;

import com.urbanassist.entity.Complaint;
import com.urbanassist.entity.SeverityLevel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * PriorityScoringService — Computes dynamic priority scores (0.00 to 100.00)
 * combining AI severity, urgency score, duplicate count, and complaint aging.
 */
@Service
public class PriorityScoringService {

    public BigDecimal calculatePriorityScore(SeverityLevel severity, int urgencyScore, long duplicateCount, LocalDateTime createdAt) {
        // 1. Severity Weight (0-100)
        double severityScore = switch (severity) {
            case CRITICAL -> 100.0;
            case HIGH -> 75.0;
            case MEDIUM -> 50.0;
            case LOW -> 25.0;
        };

        // 2. Urgency Weight (0-100)
        double urgencyWeight = Math.min(100.0, Math.max(20.0, urgencyScore * 20.0));

        // 3. Duplicate Count Boost (0-100) — more citizens filing = higher priority
        double duplicateBoost = Math.min(100.0, duplicateCount * 25.0);

        // 4. Aging Boost (0-100) — older unattended complaints gain priority over time
        double agingDays = 0;
        if (createdAt != null) {
            agingDays = Duration.between(createdAt, LocalDateTime.now()).toHours() / 24.0;
        }
        double agingBoost = Math.min(100.0, agingDays * 5.0);

        // 5. Formulated Priority Calculation
        double totalScore = (severityScore * 0.40) + (urgencyWeight * 0.25) + (duplicateBoost * 0.20) + (agingBoost * 0.15);

        return BigDecimal.valueOf(Math.min(100.0, Math.max(0.0, totalScore)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal recalculateForComplaint(Complaint complaint, long duplicateCount) {
        return calculatePriorityScore(
                complaint.getSeverityLevel(),
                3, // default urgency
                duplicateCount,
                complaint.getCreatedAt()
        );
    }
}
