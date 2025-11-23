package com.civicpulse.controller;

import com.civicpulse.model.Incident;
import com.civicpulse.repository.IncidentRepository;
import com.civicpulse.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*") // Allow frontend access
public class IncidentController {

    private final IncidentRepository incidentRepository;
    private final FileStorageService fileStorageService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public IncidentController(IncidentRepository incidentRepository, FileStorageService fileStorageService,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.incidentRepository = incidentRepository;
        this.fileStorageService = fileStorageService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Incident reportIncident(@RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Incident incident = new Incident();
        incident.setDescription(description);

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image);
            incident.setImageUrl(fileName);
        }

        Incident savedIncident = incidentRepository.save(incident);

        // Publish event to Kafka
        kafkaTemplate.send("incident-reported", savedIncident.getId().toString());

        return savedIncident;
    }

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAllByOrderByCreatedAtDesc();
    }
}
