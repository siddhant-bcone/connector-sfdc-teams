package com.bcone.connector.bcone.controller;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcone.connector.bcone.dto.IncidentRequest;
import com.bcone.connector.bcone.dto.IncidentResponse;
import com.bcone.connector.bcone.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final Logger logger=LoggerFactory.getLogger(IncidentController.class); 

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public IncidentResponse createIncident(@RequestBody IncidentRequest request) {
    	logger.info(()->"Received incident creation request: "+request);
        return incidentService.createIncident(request);
    }

}