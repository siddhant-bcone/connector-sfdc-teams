package com.bcone.connector.bcone.controller;

import static org.hamcrest.CoreMatchers.startsWith;

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
    public String createIncident(
            @RequestParam("text") String text) {
    	 IncidentRequest request = new IncidentRequest();
new Thread(()->{
		try {
        String[] parts = text.split("\\|", 4);
        request.setPriority(parts[0]);
        request.setSubject(parts[1]);
        request.setDescription(parts[2]);
        request.setOrigin(parts[3]);
}
		catch(Exception e)
		{
			e.printStackTrace();
		}
}).start();
        return
               "Incident created with details"+ incidentService.createIncident(request).toString();

    }
}
