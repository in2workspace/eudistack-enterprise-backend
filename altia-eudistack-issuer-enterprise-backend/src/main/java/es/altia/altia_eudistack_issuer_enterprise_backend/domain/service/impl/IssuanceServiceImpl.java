package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.IssuanceException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.PreSubmittedCredentialDataRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.IssuanceService;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.IssuanceHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssuanceServiceImpl implements IssuanceService {

    private static final String DEFAULT_FORMAT = "jwt_vc_json";
    private static final String OPERATION_MODE = "S";

    private final IssuanceHttpClient issuanceHttpClient;
    private final ObjectMapper objectMapper;

    @Override
    public void issueCredential(String bearerToken, String credentialConfigurationId, String acquiredData, String holderEmail) {
        PreSubmittedCredentialDataRequest request = buildRequest(credentialConfigurationId, acquiredData, holderEmail);

        executeIssuanceRequest(bearerToken, request);
    }

    private PreSubmittedCredentialDataRequest buildRequest(
            String credentialConfigurationId,
            String acquiredData,
            String holderEmail) {
        JsonNode payload = parsePayload(acquiredData);

        return PreSubmittedCredentialDataRequest.builder()
                .schema(credentialConfigurationId)
                .format(DEFAULT_FORMAT)
                .payload(payload)
                .operationMode(OPERATION_MODE)
                .email(getPayloadEmailOrDefault(payload, holderEmail))
                .build();
    }

    private String getPayloadEmailOrDefault(JsonNode payload, String holderEmail) {
        if (payload == null) {
            return holderEmail;
        }

        String payloadEmail = payload.path("email").asText(null);
        return payloadEmail == null || payloadEmail.isBlank()
                ? holderEmail
                : payloadEmail;
    }

    private JsonNode parsePayload(String acquiredData) {
        try {
            return objectMapper.readTree(acquiredData);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse acquired data as JSON: {}", acquiredData);
            throw new IssuanceException("Invalid acquired data format", e);
        }
    }

    private void executeIssuanceRequest(String bearerToken, PreSubmittedCredentialDataRequest request) {
        issuanceHttpClient.executeIssuanceRequest(bearerToken, request);
    }
}