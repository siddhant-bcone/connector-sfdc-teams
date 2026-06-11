package com.bcone.connector.bcone.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import com.bcone.connector.bcone.dto.IncidentRequest;
import com.bcone.connector.bcone.service.IncidentService;

@RestController
@RequestMapping("/slack")
@EnableAsync
public class SlackController {

    private final IncidentService incidentService;
    private static final Logger logger = LoggerFactory.getLogger(SlackController.class);

    public SlackController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping(value = "/incident", produces = "application/json")
    public String createIncident(@RequestParam("text") String text) {

        logger.info("Slack /incident command received");

        // Kick off async processing so Slack does NOT wait
        processIncidentAsync(text);

        // Immediate response to Slack (must be fast)
        return "{\"response_type\":\"ephemeral\",\"text\":\"Incident is being created...\"}";
    }

    @Async
    public void processIncidentAsync(String text) {
        try {
            logger.info("Async incident processing started");

            String[] parts = text.split("\\|", 4);

            if (parts.length < 4) {
                logger.error("Invalid input format: {}", text);
                return;
            }

            IncidentRequest request = new IncidentRequest();
            request.setPriority(parts[0]);
            request.setSubject(parts[1]);
            request.setDescription(parts[2]);
            request.setOrigin(parts[3]);

            incidentService.createIncident(request);

            logger.info("Incident successfully created");

        } catch (Exception e) {
            logger.error("Error while creating incident", e);
        }
    }
}
