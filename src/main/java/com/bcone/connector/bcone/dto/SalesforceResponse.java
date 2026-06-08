package com.bcone.connector.bcone.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesforceResponse {
    private String id;
    private Boolean success;
    private List<String> errors;
}
