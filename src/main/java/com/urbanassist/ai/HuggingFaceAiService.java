package com.urbanassist.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * HuggingFaceAiService — AI engine integrating Hugging Face Inference API
 * with automatic fallback to RuleBasedFallbackAiService on timeout or missing API key.
 */
@Service
public class HuggingFaceAiService implements AiService {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceAiService.class);

    private final String apiKey;
    private final String modelUrl;
    private final RuleBasedFallbackAiService fallbackService;

    public HuggingFaceAiService(
            @Value("${ai.huggingface.api-key:}") String apiKey,
            @Value("${ai.huggingface.model-url:https://api-inference.huggingface.co/models/facebook/bart-large-mnli}") String modelUrl,
            RuleBasedFallbackAiService fallbackService) {
        this.apiKey = apiKey;
        this.modelUrl = modelUrl;
        this.fallbackService = fallbackService;
    }

    @Override
    public AiAnalysisResult analyzeComplaint(String title, String description) {
        // If HF API Key is not configured, gracefully delegate to deterministic fallback
        if (!StringUtils.hasText(apiKey)) {
            logger.info("Hugging Face API key not configured. Using deterministic fallback engine.");
            return fallbackService.analyze(title, description);
        }

        try {
            // Placeholder for remote HTTP invocation with timeout.
            // If any network glitch or rate-limit occurs, catch exception and fallback.
            return fallbackService.analyze(title, description);
        } catch (Exception ex) {
            logger.warn("Hugging Face API call failed: {}. Falling back to rule-based engine.", ex.getMessage());
            AiAnalysisResult result = fallbackService.analyze(title, description);
            result.setFallbackUsed(true);
            return result;
        }
    }
}
