package com.civicpulse.event;

import com.civicpulse.service.AiEnrichmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IncidentListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IncidentListener.class);

    private final AiEnrichmentService aiEnrichmentService;

    public IncidentListener(AiEnrichmentService aiEnrichmentService) {
        this.aiEnrichmentService = aiEnrichmentService;
    }

    @KafkaListener(topics = "incident-reported", groupId = "civic-pulse-group")
    public void handleIncidentReported(String incidentId) {
        log.info("Received incident event: {}", incidentId);
        try {
            aiEnrichmentService.enrichIncident(UUID.fromString(incidentId));
        } catch (Exception e) {
            log.error("Error processing incident event", e);
        }
    }
}
