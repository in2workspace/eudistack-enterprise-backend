package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataAcquisitionConfigurationTest {

    @Test
    void sourcesByCredentialConfigurationId_ValidSources_ReturnsMapByCredentialConfigurationId() {
        // Arrange
        DataAcquisitionProperties.Source firstSource = buildSource("LEARCredentialEmployee");
        DataAcquisitionProperties.Source secondSource = buildSource("AnotherCredential");

        DataAcquisitionProperties properties = new DataAcquisitionProperties(
                List.of(firstSource, secondSource)
        );

        DataAcquisitionConfiguration configuration = new DataAcquisitionConfiguration(properties);

        // Act
        Map<String, DataAcquisitionProperties.Source> result =
                configuration.sourcesByCredentialConfigurationId();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
                .containsEntry("LEARCredentialEmployee", firstSource)
                .containsEntry("AnotherCredential", secondSource);
    }

    @Test
    void sourcesByCredentialConfigurationId_NullSources_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProperties properties = new DataAcquisitionProperties(null);
        DataAcquisitionConfiguration configuration = new DataAcquisitionConfiguration(properties);

        // Act & Assert
        assertThatThrownBy(configuration::sourcesByCredentialConfigurationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No data acquisition sources configured");
    }

    @Test
    void sourcesByCredentialConfigurationId_EmptySources_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProperties properties = new DataAcquisitionProperties(List.of());
        DataAcquisitionConfiguration configuration = new DataAcquisitionConfiguration(properties);

        // Act & Assert
        assertThatThrownBy(configuration::sourcesByCredentialConfigurationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No data acquisition sources configured");
    }

    @Test
    void sourcesByCredentialConfigurationId_DuplicatedCredentialConfigurationId_ThrowsIllegalStateException() {
        // Arrange
        DataAcquisitionProperties.Source firstSource = buildSource("LEARCredentialEmployee");
        DataAcquisitionProperties.Source duplicatedSource = buildSource("LEARCredentialEmployee");

        DataAcquisitionProperties properties = new DataAcquisitionProperties(
                List.of(firstSource, duplicatedSource)
        );

        DataAcquisitionConfiguration configuration = new DataAcquisitionConfiguration(properties);

        // Act & Assert
        assertThatThrownBy(configuration::sourcesByCredentialConfigurationId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Duplicate credentialConfigurationId 'LEARCredentialEmployee' detected in data acquisition configuration; " +
                                "please check your YAML/properties configuration for duplicate entries."
                );
    }

    private DataAcquisitionProperties.Source buildSource(String credentialConfigurationId) {
        return new DataAcquisitionProperties.Source(
                credentialConfigurationId,
                DataAcquisitionProperties.SourceType.MOCK,
                "https://endpoint.example.com",
                "ou=people,dc=example,dc=com",
                "cn=bind-user,dc=example,dc=com",
                "bind-password",
                "uid",
                new DataAcquisitionProperties.Mapping(
                        "givenName",
                        "sn",
                        "mail",
                        "employeeNumber"
                ),
                Duration.ofSeconds(5),
                new DataAcquisitionProperties.Retry(3, Duration.ofSeconds(1))
        );
    }
}