package com.bcone.connector.bcone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {

    private String incidentNumber;
    private String status;
    private String message;

    // Salesforce response details (when available)
    private String id;
    private Boolean success;
    private List<String> errors;

}