package com.bcone.connector.bcone.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bcone.connector.bcone.dto.IncidentRequest;
import com.bcone.connector.bcone.dto.IncidentResponse;
import com.bcone.connector.bcone.dto.SalesforceIncidentRequest;
import com.bcone.connector.bcone.dto.SalesforceResponse;

@Service
public class IncidentService {

	private final SalesforceClient salesforceClient;

	public IncidentService(SalesforceClient salesforceClient) {
		this.salesforceClient = salesforceClient;
	}

	/**
	 * Create an incident based on incoming IncidentRequest.
	 */
	public IncidentResponse createIncident(IncidentRequest incidentRequest) {
		SalesforceIncidentRequest sfRequest = mapToSalesforce(incidentRequest);
		SalesforceResponse sfResp = null;
		try {
			sfResp = salesforceClient.createIncident(sfRequest);
		} catch (Exception e) {
			return IncidentResponse.builder()
					.incidentNumber(null)
					.status("Failed")
					.message("Failed to create incident: " + e.getMessage())
					.id(null)
					.success(false)
					.errors(null)
					.build();
		}

		String incidentNumber = null;
		Boolean success = null;
		java.util.List<String> errors = null;
		if (sfResp != null) {
			incidentNumber = sfResp.getId();
			success = sfResp.getSuccess();
			errors = sfResp.getErrors();
		}

		if (incidentNumber == null || incidentNumber.isEmpty()) {
			incidentNumber = generateIncidentNumber();
		}

		String status = Boolean.TRUE.equals(success) ? "Created" : "Failed";
		String message;
		if (Boolean.TRUE.equals(success)) {
			message = "Incident created in Salesforce with id " + incidentNumber + " at " + Instant.now().toString();
		} else if (errors != null && !errors.isEmpty()) {
			message = "Salesforce failed to create incident: " + String.join("; ", errors);
		} else {
			message = "Salesforce did not return a successful response; returned id/raw: " + incidentNumber;
		}

		return IncidentResponse.builder()
				.incidentNumber(incidentNumber)
				.status(status)
				.message(message)
				.id(sfResp != null ? sfResp.getId() : null)
				.success(success)
				.errors(errors)
				.build();
	}

	private SalesforceIncidentRequest mapToSalesforce(IncidentRequest req) {
		if (req == null) {
			return null;
		}

		return new SalesforceIncidentRequest(req.getSubject(), req.getDescription(), req.getPriority(),
				req.getOrigin());
	}

	private String generateIncidentNumber() {
		String shortId = UUID.randomUUID().toString().split("-")[0].toUpperCase();
		return "INC-" + shortId;
	}

}
