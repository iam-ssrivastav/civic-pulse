package com.civicpulse.service;

import com.civicpulse.model.Incident;
import com.civicpulse.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AiEnrichmentService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AiEnrichmentService.class);

    private final ChatModel chatModel;
    private final IncidentRepository incidentRepository;

    public AiEnrichmentService(ChatModel chatModel, IncidentRepository incidentRepository) {
        this.chatModel = chatModel;
        this.incidentRepository = incidentRepository;
    }

    public void enrichIncident(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found"));

        String userText = incident.getDescription();

        // Prompt Engineering
        String promptText = String.format(
                "Analyze the following incident description and provide a JSON response with 'category' (INFRASTRUCTURE, SAFETY, SANITATION, OTHER) and 'priority' (LOW, MEDIUM, HIGH). Description: %s",
                userText);
        Prompt prompt = new Prompt(promptText);

        try {
            String response = chatModel.call(prompt).getResult().getOutput().getContent();
            log.info("AI Response for incident {}: {}", incidentId, response);

            // Simple parsing (in a real app, use Jackson)
            String category = extractValue(response, "category");
            String priority = extractValue(response, "priority");

            incident.setCategory(category);
            incident.setPriority(priority);
            incident.setStatus(Incident.IncidentStatus.ANALYZED);

            incidentRepository.save(incident);

        } catch (Exception e) {
            log.error("Error calling AI service", e);
        }
    }

    private String extractValue(String json, String key) {
        try {
            int startIndex = json.indexOf("\"" + key + "\":");
            if (startIndex == -1)
                return "UNKNOWN";

            startIndex = json.indexOf("\"", startIndex + key.length() + 3) + 1;
            int endIndex = json.indexOf("\"", startIndex);

            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
