package com.urbanassist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HealthController — A simple endpoint to verify the backend is running.
 *
 * WHY THIS EXISTS:
 *   Before building complex features, we need a quick way to confirm:
 *     1. The Spring Boot application starts without errors.
 *     2. The embedded Tomcat server is accepting HTTP requests.
 *     3. Spring Security allows access to this endpoint.
 *
 * HOW TO TEST:
 *   Open a browser or Postman and visit:
 *     GET http://localhost:8080/api/health
 *
 *   Expected response (HTTP 200):
 *     {
 *       "status": "UP",
 *       "application": "UrbanAssist",
 *       "timestamp": "2026-10-09T14:30:00"
 *     }
 *
 * ANNOTATIONS EXPLAINED:
 *   @RestController — Combines @Controller + @ResponseBody. Every method return
 *                     value is automatically converted to JSON.
 *   @RequestMapping — Sets a base URL prefix for all methods in this controller.
 *   @GetMapping     — Maps HTTP GET requests to the annotated method.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Returns a JSON object confirming the application is running.
     *
     * ResponseEntity gives us control over the HTTP status code.
     * ResponseEntity.ok() returns HTTP 200 with the map serialized as JSON.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("application", "UrbanAssist");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
