package com.bcone.connector.bcone.controller;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
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
    private final Logger logger=LoggerFactory.getLogger(SlackController.class);

    public SlackController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping("/incident")
    public IncidentResponse createIncident(
            @RequestParam("text") String text) {

        String[] parts = text.split("\\|", 4);

        logger.info(()->"Text received"+ text);
        
        IncidentRequest request = new IncidentRequest();
        request.setPriority(parts[0]);
        request.setSubject(parts[1]);
        request.setDescription(parts[2]);
        request.setOrigin(parts[3]);

        logger.info(()->"Incident created from slack input"+request);
        return
                incidentService.createIncident(request).toString();

    }
}
