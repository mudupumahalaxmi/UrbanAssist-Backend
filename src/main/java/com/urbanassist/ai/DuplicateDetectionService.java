package com.urbanassist.ai;

import com.urbanassist.entity.Complaint;
import com.urbanassist.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * DuplicateDetectionService — Geospatial proximity (Haversine formula)
 * fused with NLP text similarity to identify duplicate complaints.
 */
@Service
public class DuplicateDetectionService {

    private static final double MAX_PROXIMITY_METERS = 150.0; // 150 meters radius
    private static final double SIMILARITY_THRESHOLD = 0.60;   // 60% Jaccard word overlap

    private final ComplaintRepository complaintRepository;

    public DuplicateDetectionService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Complaint> checkForDuplicate(BigDecimal lat, BigDecimal lng, String title, String description) {
        if (lat == null || lng == null) {
            return Optional.empty();
        }

        // Bounding box approximation (~200m offset: 0.002 degrees lat/lng)
        BigDecimal offset = new BigDecimal("0.002");
        BigDecimal minLat = lat.subtract(offset);
        BigDecimal maxLat = lat.add(offset);
        BigDecimal minLng = lng.subtract(offset);
        BigDecimal maxLng = lng.add(offset);

        List<Complaint> candidates = complaintRepository.findActiveInBoundingBox(minLat, maxLat, minLng, maxLng);
        String newText = (title + " " + description).toLowerCase(Locale.ROOT);

        for (Complaint candidate : candidates) {
            double distanceMeters = calculateHaversineDistance(
                    lat.doubleValue(), lng.doubleValue(),
                    candidate.getLatitude().doubleValue(), candidate.getLongitude().doubleValue()
            );

            if (distanceMeters <= MAX_PROXIMITY_METERS) {
                String existingText = (candidate.getTitle() + " " + candidate.getDescription()).toLowerCase(Locale.ROOT);
                double similarity = calculateJaccardSimilarity(newText, existingText);

                if (similarity >= SIMILARITY_THRESHOLD) {
                    return Optional.of(candidate);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Haversine formula calculating distance in meters between two coordinates.
     */
    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Jaccard word set similarity between two text strings.
     */
    public double calculateJaccardSimilarity(String text1, String text2) {
        Set<String> set1 = new HashSet<>(Arrays.asList(text1.split("\\W+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(text2.split("\\W+")));

        if (set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
