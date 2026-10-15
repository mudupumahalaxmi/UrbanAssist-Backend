package com.urbanassist.ai;

import com.urbanassist.entity.ComplaintCategory;
import com.urbanassist.entity.SeverityLevel;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * RuleBasedFallbackAiService — Deterministic local NLP heuristic analyzer.
 * Used when external Hugging Face API key is missing, rate-limited, or unreachable.
 */
@Service
public class RuleBasedFallbackAiService {

    public AiAnalysisResult analyze(String title, String description) {
        String text = (title + " " + description).toLowerCase(Locale.ROOT);

        // 1. Determine Category based on keyword matching
        ComplaintCategory category = determineCategory(text);

        // 2. Determine Severity Level & Urgency Score (1-5)
        int urgencyScore = calculateUrgency(text);
        SeverityLevel severity = mapUrgencyToSeverity(urgencyScore);

        return new AiAnalysisResult(category, severity, urgencyScore, 0.85, true);
    }

    private ComplaintCategory determineCategory(String text) {
        if (containsAny(text, "pothole", "road", "tar", "asphalt", "ditch", "bridge", "traffic signal", "divider", "footpath")) {
            return ComplaintCategory.ROADS;
        }
        if (containsAny(text, "garbage", "trash", "waste", "dump", "bin", "dirt", "litter", "cleaning", "sweeping")) {
            return ComplaintCategory.SANITATION;
        }
        if (containsAny(text, "water", "leak", "pipe", "sewage", "drain", "drainage", "overflow", "supply", "tap")) {
            return ComplaintCategory.WATER;
        }
        if (containsAny(text, "electric", "light", "pole", "wire", "spark", "transformer", "power", "blackout", "streetlight")) {
            return ComplaintCategory.ELECTRICITY;
        }
        if (containsAny(text, "park", "tree", "garden", "bench", "playground", "branch", "fence")) {
            return ComplaintCategory.PARKS;
        }
        if (containsAny(text, "mosquito", "fever", "stray", "dog", "animal", "hygiene", "food", "disease", "smell")) {
            return ComplaintCategory.PUBLIC_HEALTH;
        }
        return ComplaintCategory.OTHER;
    }

    private int calculateUrgency(String text) {
        int score = 2; // Default Medium

        if (containsAny(text, "spark", "fire", "burst", "flooding", "collapsed", "hazard", "danger", "accident", "emergency", "open manhole", "poison")) {
            score = 5; // Critical
        } else if (containsAny(text, "severe", "major", "broken", "blocked", "overflowing", "huge", "foul", "dark")) {
            score = 4; // High
        } else if (containsAny(text, "minor", "small", "slow", "dim")) {
            score = 1; // Low
        }

        return score;
    }

    private SeverityLevel mapUrgencyToSeverity(int urgencyScore) {
        return switch (urgencyScore) {
            case 5 -> SeverityLevel.CRITICAL;
            case 4 -> SeverityLevel.HIGH;
            case 1 -> SeverityLevel.LOW;
            default -> SeverityLevel.MEDIUM;
        };
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
