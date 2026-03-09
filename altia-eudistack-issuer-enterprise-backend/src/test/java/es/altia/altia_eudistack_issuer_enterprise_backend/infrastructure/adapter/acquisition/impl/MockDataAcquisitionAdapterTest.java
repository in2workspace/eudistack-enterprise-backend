package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockDataAcquisitionAdapterTest {

    private static final String SUPPORTED_CREDENTIAL_CONFIGURATION_ID = "LEARCredentialEmployee";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DataAcquisitionConfiguration dataAcquisitionConfiguration;

    private MockDataAcquisitionAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new MockDataAcquisitionAdapter(objectMapper, dataAcquisitionConfiguration);
    }

    @Test
    void shouldAcquireMappedCredentialSubjectSuccessfully() throws Exception {
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

        String result = adapter.acquire(SUPPORTED_CREDENTIAL_CONFIGURATION_ID, "subject-123");

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
    void shouldThrowExceptionWhenSourceIsNotConfigured() {
        when(dataAcquisitionConfiguration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of());

        assertThatThrownBy(() -> adapter.acquire("unknown-credential", "subject-123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No Data Acquisition source configured for credentialConfigurationId: unknown-credential");
    }

    @Test
    void shouldTransformSourceNodeToExpectedTargetFields() throws Exception {
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

        ObjectNode result = adapter.transform(sourceNode, mapping);

        assertThat(result.get("firstName").asText()).isEqualTo("John");
        assertThat(result.get("lastName").asText()).isEqualTo("Doe");
        assertThat(result.get("email").asText()).isEqualTo("john.doe@example.com");
        assertThat(result.get("employeeId").asText()).isEqualTo("12345");
    }

    @Test
    void shouldNotIncludeTargetFieldsWhenSourceFieldsAreMissingOrNull() throws Exception {
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

        ObjectNode result = adapter.transform(sourceNode, mapping);

        assertThat(result.get("firstName").asText()).isEqualTo("John");
        assertThat(result.has("lastName")).isFalse();
        assertThat(result.has("email")).isFalse();
        assertThat(result.has("employeeId")).isFalse();
    }

    @Test
    void shouldSupportExpectedCredentialConfigurationId() {
        assertThat(adapter.supports(SUPPORTED_CREDENTIAL_CONFIGURATION_ID)).isTrue();
    }

    @Test
    void shouldNotSupportUnexpectedCredentialConfigurationId() {
        assertThat(adapter.supports("AnotherCredential")).isFalse();
    }

    @Test
    void shouldReturnMockAsSupportedType() {
        assertThat(adapter.getSupportedType()).isEqualTo(DataAcquisitionProperties.SourceType.MOCK);
    }
}