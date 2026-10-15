package com.urbanassist;

import com.urbanassist.ai.DuplicateDetectionService;
import com.urbanassist.ai.PriorityScoringService;
import com.urbanassist.entity.SeverityLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiDuplicateDetectionTest {

    @Autowired
    private DuplicateDetectionService duplicateDetectionService;

    @Autowired
    private PriorityScoringService priorityScoringService;

    @Test
    void testHaversineDistanceCalculation() {
        // Distance between two points ~100m apart in New Delhi
        double lat1 = 28.6139;
        double lon1 = 77.2090;
        double lat2 = 28.6145;
        double lon2 = 77.2095;

        double distanceMeters = duplicateDetectionService.calculateHaversineDistance(lat1, lon1, lat2, lon2);
        assertTrue(distanceMeters > 0 && distanceMeters < 150.0, "Distance should be within 150 meters");
    }

    @Test
    void testJaccardSimilarityCalculation() {
        String desc1 = "Huge pothole on MG Road near main intersection causing traffic jam";
        String desc2 = "Deep pothole on MG Road near intersection causing accident hazard";

        double similarity = duplicateDetectionService.calculateJaccardSimilarity(desc1, desc2);
        assertTrue(similarity >= 0.50, "Similarity should detect high word overlap");
    }

    @Test
    void testPriorityScoringFormula() {
        BigDecimal criticalScore = priorityScoringService.calculatePriorityScore(
                SeverityLevel.CRITICAL, 5, 3, LocalDateTime.now().minusDays(2)
        );

        BigDecimal lowScore = priorityScoringService.calculatePriorityScore(
                SeverityLevel.LOW, 1, 0, LocalDateTime.now()
        );

        assertTrue(criticalScore.compareTo(lowScore) > 0, "Critical complaint with duplicates should rank higher than low complaint");
        assertTrue(criticalScore.compareTo(new BigDecimal("70.00")) >= 0, "Critical score should be >= 70.00");
    }
}
