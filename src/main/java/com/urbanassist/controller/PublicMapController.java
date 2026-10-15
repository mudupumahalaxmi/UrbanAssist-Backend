package com.urbanassist.controller;

import com.urbanassist.dto.response.MapComplaintResponse;
import com.urbanassist.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PublicMapController — Provides complaint geo-coordinates for React Leaflet OpenStreetMap.
 * Protects citizen privacy by stripping PII (name, phone, email).
 */
@RestController
@RequestMapping("/api/public/map")
public class PublicMapController {

    private final ComplaintService complaintService;

    public PublicMapController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<MapComplaintResponse>> getMapPoints() {
        List<MapComplaintResponse> points = complaintService.getPublicMapComplaints();
        return ResponseEntity.ok(points);
    }

    @GetMapping("/hotspots")
    public ResponseEntity<List<MapComplaintResponse>> getHotspots() {
        // High density / unresolved map points for heatmap overlay
        List<MapComplaintResponse> points = complaintService.getPublicMapComplaints();
        return ResponseEntity.ok(points);
    }
}
