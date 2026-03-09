package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
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
        DataAcquisitionProperties.Mapping mapping = new DataAcquisitionProperties.Mapping(
                "givenName",
                "sn",
                "mail",
                "employeeNumber"
        );

        DataAcquisitionProperties.Source source = new DataAcquisitionProperties.Source(
                SUPPORTED_CREDENTIAL_CONFIGURATION_ID,
                DataAcquisitionProperties.SourceType.MOCK,
                null,
                null,
                null,
                null,
                null,
                mapping,
                Duration.ofSeconds(5),
                null
        );

        when(dataAcquisitionConfiguration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of(SUPPORTED_CREDENTIAL_CONFIGURATION_ID, source));

        // Act
        String result = adapter.acquire(SUPPORTED_CREDENTIAL_CONFIGURATION_ID, "subject-123");

        // Assert
        JsonNode root = objectMapper.readTree(result);
        JsonNode mandatee = root.get("mandatee");

        assertThat(mandatee).isNotNull();
        assertThat(mandatee.get("firstName").asText()).isEqualTo("John");
        assertThat(mandatee.get("lastName").asText()).isEqualTo("Doe");
        assertThat(mandatee.get("email").asText()).isEqualTo("albert.rodriguez@altia.es");
        assertThat(mandatee.get("employeeId").asText()).isEqualTo("1234567890");

        JsonNode mandator = root.get("mandator");
        assertThat(mandator).isNotNull();
        assertThat(mandator.get("organization").asText()).isEqualTo("ALTIA CONSULTORES, SA");

        JsonNode power = root.get("power");
        assertThat(power).isNotNull();
        assertThat(power.isArray()).isTrue();
        assertThat(power).hasSize(2);
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

        DataAcquisitionProperties.Mapping mapping = new DataAcquisitionProperties.Mapping(
                "givenName",
                "sn",
                "mail",
                "employeeNumber"
        );

        // Act
        ObjectNode result = adapter.transform(sourceNode, mapping);

        // Assert
        assertThat(result.get("firstName").asText()).isEqualTo("John");
        assertThat(result.get("lastName").asText()).isEqualTo("Doe");
        assertThat(result.get("email").asText()).isEqualTo("john.doe@example.com");
        assertThat(result.get("employeeId").asText()).isEqualTo("12345");
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

        DataAcquisitionProperties.Mapping mapping = new DataAcquisitionProperties.Mapping(
                "givenName",
                "sn",
                "mail",
                "employeeNumber"
        );

        // Act
        ObjectNode result = adapter.transform(sourceNode, mapping);

        // Assert
        assertThat(result.get("firstName").asText()).isEqualTo("John");
        assertThat(result.has("lastName")).isFalse();
        assertThat(result.has("email")).isFalse();
        assertThat(result.has("employeeId")).isFalse();
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
}