package com.bcone.connector.bcone.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcone.connector.bcone.dto.IncidentRequest;
import com.bcone.connector.bcone.dto.IncidentResponse;
import com.bcone.connector.bcone.service.IncidentService;

@RestController
@RequestMapping("/slack")
public class SlackController {

    private final IncidentService incidentService;

    public SlackController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping("/incident")
    public String createIncident(
            @RequestParam("text") String text) {

        String[] parts = text.split("\\|", 3);

        if (parts.length < 3) {
            return "Usage: /incident <Priority>|<Subject>|<Description>";
        }

        IncidentRequest request = new IncidentRequest();
        request.setPriority(parts[0]);
        request.setSubject(parts[1]);
        request.setDescription(parts[2]);
        request.setOrigin("Slack");

        IncidentResponse response =
                incidentService.createIncident(request);

        return "✅ Incident created successfully";
    }
}
