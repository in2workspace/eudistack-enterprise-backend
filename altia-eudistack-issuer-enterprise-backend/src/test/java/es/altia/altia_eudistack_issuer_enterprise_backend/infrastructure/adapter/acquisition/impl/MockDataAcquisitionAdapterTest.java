package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockDataAcquisitionAdapterTest {

    private static final String SUPPORTED_CREDENTIAL_CONFIGURATION_ID = "LEARCredentialEmployee";

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DataAcquisitionConfiguration dataAcquisitionConfiguration;

    @InjectMocks
    private MockDataAcquisitionAdapter adapter;

    @Test
    void Acquire_SourceIsConfigured_ReturnsMappedCredentialSubject() throws Exception {
        // Arrange
        when(dataAcquisitionConfiguration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of(SUPPORTED_CREDENTIAL_CONFIGURATION_ID, mockSource()));

        // Act
        String result = adapter.acquire(SUPPORTED_CREDENTIAL_CONFIGURATION_ID, "subject-123");

        // Assert
        JsonNode root = objectMapper.readTree(result);

        assertThat(objectMapper.convertValue(root, Map.class))
                .usingRecursiveComparison()
                .isEqualTo(expectedAcquireResult());
    }

    @Test
    void Acquire_SourceIsNotConfigured_ThrowsIllegalArgumentException() {
        // Arrange
        when(dataAcquisitionConfiguration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of());

        // Act & Assert
        assertThatThrownBy(() -> adapter.acquire("unknown-credential", "subject-123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No Data Acquisition source configured for credentialConfigurationId: unknown-credential");
    }

    @Test
    void Transform_SourceContainsAllMappedFields_ReturnsExpectedTargetFields() throws Exception {
        // Arrange
        JsonNode sourceNode = objectMapper.readTree("""
                {
                  "givenName": "John",
                  "sn": "Doe",
                  "mail": "john.doe@example.com",
                  "employeeNumber": "12345"
                }
                """);

        // Act
        ObjectNode result = adapter.transform(sourceNode, mockMapping());

        // Assert
        assertThat(objectMapper.convertValue(result, Map.class))
                .usingRecursiveComparison()
                .isEqualTo(expectedTransformResult());
    }

    @Test
    void Transform_SourceContainsMissingOrNullFields_DoesNotIncludeTargetFields() throws Exception {
        // Arrange
        JsonNode sourceNode = objectMapper.readTree("""
                {
                  "givenName": "John",
                  "sn": null
                }
                """);

        // Act
        ObjectNode result = adapter.transform(sourceNode, mockMapping());

        // Assert
        assertThat(objectMapper.convertValue(result, Map.class))
                .usingRecursiveComparison()
                .isEqualTo(expectedTransformResultWithMissingFields());
    }

    @Test
    void Supports_CredentialConfigurationIdIsSupported_ReturnsTrue() {
        // Act
        boolean result = adapter.supports(SUPPORTED_CREDENTIAL_CONFIGURATION_ID);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void Supports_CredentialConfigurationIdIsNotSupported_ReturnsFalse() {
        // Act
        boolean result = adapter.supports("AnotherCredential");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void GetSupportedType_AdapterIsMock_ReturnsMockSourceType() {
        // Act
        DataAcquisitionProperties.SourceType result = adapter.getSupportedType();

        // Assert
        assertThat(result).isEqualTo(DataAcquisitionProperties.SourceType.MOCK);
    }

    private DataAcquisitionProperties.Source mockSource() {
        return new DataAcquisitionProperties.Source(
                SUPPORTED_CREDENTIAL_CONFIGURATION_ID,
                DataAcquisitionProperties.SourceType.MOCK,
                null,
                null,
                null,
                null,
                null,
                mockMapping(),
                Duration.ofSeconds(5),
                null
        );
    }

    private DataAcquisitionProperties.Mapping mockMapping() {
        return new DataAcquisitionProperties.Mapping(
                "givenName",
                "sn",
                "mail",
                "employeeNumber"
        );
    }

    private Map<String, Object> expectedAcquireResult() {
        return Map.of(
                "mandatee", Map.of(
                        "firstName", "John",
                        "lastName", "Doe",
                        "email", "albert.rodriguez@altia.es",
                        "employeeId", "1234567890"
                ),
                "mandator", Map.of(
                        "commonName", "Constantino Fernández Pico",
                        "country", "ES",
                        "email", "tino.fernandez@altia.es",
                        "id", "did:elsi:VATES-A15456585",
                        "organization", "ALTIA CONSULTORES, SA",
                        "organizationIdentifier", "VATES-A15456585",
                        "serialNumber", "32771385L"
                ),
                "power", List.of(
                        Map.of(
                                "action", List.of("Execute"),
                                "domain", "DOME",
                                "function", "Onboarding",
                                "type", "domain"
                        ),
                        Map.of(
                                "action", List.of("Create", "Update", "Delete"),
                                "domain", "DOME",
                                "function", "ProductOffering",
                                "type", "domain"
                        )
                )
        );
    }

    private Map<String, Object> expectedTransformResult() {
        return Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "email", "john.doe@example.com",
                "employeeId", "12345"
        );
    }

    private Map<String, Object> expectedTransformResultWithMissingFields() {
        return Map.of(
                "firstName", "John"
        );
    }
}