/**
 * com.urbanassist.ai — AI integration layer for intelligent complaint processing.
 *
 * FILES COMING IN PHASE 4:
 *   - AiService.java                  — Interface defining AI operations.
 *   - HuggingFaceAiService.java       — Calls Hugging Face Inference API for
 *                                        zero-shot classification and severity
 *                                        estimation.
 *   - RuleBasedFallbackAiService.java  — Deterministic keyword-based fallback
 *                                        when the external AI API is unavailable.
 *   - DuplicateDetectionService.java   — Haversine geospatial + text similarity
 *                                        logic to flag potential duplicate complaints.
 *   - PriorityScoringService.java      — Calculates dynamic priority scores
 *                                        based on severity, urgency, duplicate count,
 *                                        and aging factors.
 */
package com.urbanassist.ai;
