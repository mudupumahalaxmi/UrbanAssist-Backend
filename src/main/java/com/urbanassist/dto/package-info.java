/**
 * com.urbanassist.dto — Data Transfer Objects (request and response payloads).
 *
 * DTOs are plain Java classes used to define the exact shape of JSON that
 * the API accepts (requests) and returns (responses). They are separate
 * from JPA entities to avoid exposing internal database structure.
 *
 * SUB-PACKAGES COMING IN PHASE 2:
 *   - dto.request/   — LoginRequest, RegisterRequest, ComplaintCreateRequest, etc.
 *   - dto.response/  — AuthResponse, ComplaintResponse, AnalyticsSummaryResponse, etc.
 */
package com.urbanassist.dto;
