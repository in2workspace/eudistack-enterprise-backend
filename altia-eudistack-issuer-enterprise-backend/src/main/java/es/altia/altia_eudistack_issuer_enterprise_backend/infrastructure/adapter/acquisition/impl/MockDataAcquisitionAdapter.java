package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockDataAcquisitionAdapter implements DataAcquisitionProvider {

    private static final String SUPPORTED_CREDENTIAL_CONFIGURATION_ID = "LEARCredentialEmployee";
    private final ObjectMapper objectMapper;

    private static final String credentialSubject = """
            {
                "mandatee": {
                    "email": "oriol.canades@altia.es",
                    "employeeId": "5401",
                    "firstName": "Oriol",
                    "id": "did:key:zDnaerjGwLiUDYwuo3N7DnzcXKN7mTQrUosk8gneHujW8QNB3",
                    "lastName": "Canadés Díez"
                },
                "mandator": {
                    "commonName": "Constantino Fernández Pico",
                    "country": "ES",
                    "email": "tino.fernandez@altia.es",
                    "id": "did:elsi:VATES-A15456585",
                    "organization": "ALTIA CONSULTORES, SA",
                    "organizationIdentifier": "VATES-A15456585",
                    "serialNumber": "32771385L"
                },
                "power": [
                    {
                        "action": [
                            "Execute"
                        ],
                        "domain": "DOME",
                        "function": "Onboarding",
                        "type": "domain"
                    },
                    {
                        "action": [
                            "Create",
                            "Update",
                            "Delete"
                        ],
                        "domain": "DOME",
                        "function": "ProductOffering",
                        "type": "domain"
                    }
                ]
            }""";

    private static final String TEMP_ACQUIRED_DATA = """
            {
              "givenName": "John",
              "sn": "Doe",
              "mail": "albert.rodriguez@altia.es",
              "employeeNumber": "1234567890"
            }""";

    private final DataAcquisitionConfiguration dataAcquisitionConfiguration;

    @Override
    public String acquire(String credentialConfigurationId, String subjectIdentifier) {
        log.debug("MockDataAcquisitionAdapter acquiring data for credentialConfigurationId={} and subjectIdentifier={}",
                credentialConfigurationId, subjectIdentifier);
        DataAcquisitionProperties.Source source =
                Optional.ofNullable(
                        dataAcquisitionConfiguration
                                .sourcesByCredentialConfigurationId()
                                .get(credentialConfigurationId)
                ).orElseThrow(() -> new IllegalArgumentException(
                        "No Data Acquisition source configured for credentialConfigurationId: "
                                + credentialConfigurationId
                ));

        String credentialSubject = mapResponse(TEMP_ACQUIRED_DATA, source.mapping());
        System.out.println("Credential Subject after mapping: " + credentialSubject);
        return credentialSubject;
    }

    private String mapResponse(String acquiredData, DataAcquisitionProperties.Mapping mapping) {
        try {
            JsonNode sourceNode = objectMapper.readTree(acquiredData);

            ObjectNode transformed = transform(sourceNode, mapping);

            ObjectNode root = (ObjectNode) objectMapper.readTree(credentialSubject);

            root.set("mandatee", transformed);

            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode transform(JsonNode sourceNode, DataAcquisitionProperties.Mapping mapping) {

        ObjectNode target = objectMapper.createObjectNode();

        putIfPresent(sourceNode, target, mapping.firstName(), "firstName");
        putIfPresent(sourceNode, target, mapping.familyName(), "lastName");
        putIfPresent(sourceNode, target, mapping.email(), "email");
        putIfPresent(sourceNode, target, mapping.employeeId(), "employeeId");

        return target;
    }

    private void putIfPresent(JsonNode source, ObjectNode target,
                              String sourceField, String targetField) {
        JsonNode value = source.get(sourceField);

        if (value != null && !value.isNull()) {
            target.set(targetField, value);
        }
    }


    @Override
    public boolean supports(String credentialConfigurationId) {
        boolean supported = SUPPORTED_CREDENTIAL_CONFIGURATION_ID.equals(credentialConfigurationId);

        log.debug("DataAcquisition support check - credentialConfigurationId={}, supported={}",
                credentialConfigurationId,
                supported);

        return supported;
    }

    @Override
    public DataAcquisitionProperties.SourceType getSupportedType() {
        return DataAcquisitionProperties.SourceType.MOCK;
    }
}
