package com.bcone.connector.bcone.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bcone.connector.bcone.dto.SalesforceIncidentRequest;
import com.bcone.connector.bcone.dto.SalesforceResponse;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesforceClient {

    private final RestTemplate restTemplate;
    private final String sfdcEndpoint;
    private final String authToken;
    private final Logger logger = LoggerFactory.getLogger(SalesforceClient.class);

    public SalesforceClient(@Value("${sfdc.url:}") String sfdcEndpoint,
            @Value("${sfdc.token:}") String authToken) {
        this.restTemplate = new RestTemplate();
        this.sfdcEndpoint = sfdcEndpoint;
        this.authToken = authToken;
    }
    
    
    public SalesforceResponse createIncident(SalesforceIncidentRequest req) {
        if (sfdcEndpoint == null || sfdcEndpoint.isBlank()) {
            throw new IllegalStateException("Salesforce endpoint (sfdc.url) is not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null && !authToken.isBlank()) {
            headers.set("Authorization", "Bearer " + authToken);
        }
        HttpEntity<SalesforceIncidentRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(sfdcEndpoint, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Salesforce returned status " + resp.getStatusCode().value());
        }

        String body = resp.getBody();
        logger.info(() -> "Received response from Salesforce: " + body);
        if (body == null || body.isBlank()) {
            return SalesforceResponse.builder().id(null).success(false).errors(Collections.emptyList()).build();
        }

        // Extract id
        String id = null;
        String[] keys = { "incidentNumber", "id", "number" };
        for (String key : keys) {
            Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
            Matcher m = p.matcher(body);
            if (m.find()) {
                id = m.group(1);
                break;
            }
        }

        // Extract success boolean
        Boolean success = null;
        Pattern succP = Pattern.compile("\"success\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher succM = succP.matcher(body);
        if (succM.find()) {
            success = Boolean.parseBoolean(succM.group(1));
        }

        // Extract errors array (simple parsing of quoted strings inside errors array)
        List<String> errors = new ArrayList<>();
        Pattern errP = Pattern.compile("\"errors\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher errM = errP.matcher(body);
        if (errM.find()) {
            String inner = errM.group(1);
            Pattern strP = Pattern.compile("\"(.*?)\"");
            Matcher strM = strP.matcher(inner);
            while (strM.find()) {
                errors.add(strM.group(1));
            }
        }

        if (errors.isEmpty()) {
            errors = Collections.emptyList();
        }

        return SalesforceResponse.builder().id(id != null ? id : body).success(success).errors(errors).build();
    }

}

