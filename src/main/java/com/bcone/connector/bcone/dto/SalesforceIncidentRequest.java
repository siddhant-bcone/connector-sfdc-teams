package com.bcone.connector.bcone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesforceIncidentRequest {

    private String subject;
    private String description;
    private String priority;
    private String origin;

}