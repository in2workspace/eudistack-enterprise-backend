package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataAcquisitionPropertiesTest {

    @Test
    void createDataAcquisitionProperties_WithNestedRecords_PropertiesMatchExpectedValues() {
        // Arrange
        DataAcquisitionProperties.Mapping mapping = new DataAcquisitionProperties.Mapping(
                "givenName",
                "sn",
                "mail",
                "employeeNumber"
        );

        DataAcquisitionProperties.Retry retry = new DataAcquisitionProperties.Retry(
                3,
                Duration.ofSeconds(2)
        );

        DataAcquisitionProperties.Source source = new DataAcquisitionProperties.Source(
                "LEARCredentialEmployee",
                DataAcquisitionProperties.SourceType.MOCK,
                "https://endpoint.example.com",
                "ou=people,dc=example,dc=com",
                "cn=bind-user,dc=example,dc=com",
                "bind-password",
                "uid",
                mapping,
                Duration.ofSeconds(5),
                retry
        );

        // Act
        DataAcquisitionProperties properties = new DataAcquisitionProperties(List.of(source));

        // Assert
        assertThat(properties.sources()).containsExactly(source);

        DataAcquisitionProperties.Source createdSource = properties.sources().getFirst();
        assertThat(createdSource.credentialConfigurationId()).isEqualTo("LEARCredentialEmployee");
        assertThat(createdSource.type()).isEqualTo(DataAcquisitionProperties.SourceType.MOCK);
        assertThat(createdSource.endpoint()).isEqualTo("https://endpoint.example.com");
        assertThat(createdSource.baseDn()).isEqualTo("ou=people,dc=example,dc=com");
        assertThat(createdSource.bindDn()).isEqualTo("cn=bind-user,dc=example,dc=com");
        assertThat(createdSource.bindPassword()).isEqualTo("bind-password");
        assertThat(createdSource.subjectIdentifierAttribute()).isEqualTo("uid");
        assertThat(createdSource.mapping()).isEqualTo(mapping);
        assertThat(createdSource.timeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(createdSource.retry()).isEqualTo(retry);

        assertThat(mapping.firstName()).isEqualTo("givenName");
        assertThat(mapping.familyName()).isEqualTo("sn");
        assertThat(mapping.email()).isEqualTo("mail");
        assertThat(mapping.employeeId()).isEqualTo("employeeNumber");

        assertThat(retry.maxAttempts()).isEqualTo(3);
        assertThat(retry.backoff()).isEqualTo(Duration.ofSeconds(2));
    }

    @Test
    void createDataAcquisitionProperties_WithNullOptionalSourceValues_NullFieldsReturnedCorrectly() {
        // Arrange
        DataAcquisitionProperties.Source source = new DataAcquisitionProperties.Source(
                "LEARCredentialEmployee",
                DataAcquisitionProperties.SourceType.MOCK,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        DataAcquisitionProperties properties = new DataAcquisitionProperties(List.of(source));

        // Assert
        assertThat(properties.sources()).hasSize(1);

        DataAcquisitionProperties.Source createdSource = properties.sources().getFirst();
        assertThat(createdSource.credentialConfigurationId()).isEqualTo("LEARCredentialEmployee");
        assertThat(createdSource.type()).isEqualTo(DataAcquisitionProperties.SourceType.MOCK);
        assertThat(createdSource.endpoint()).isNull();
        assertThat(createdSource.baseDn()).isNull();
        assertThat(createdSource.bindDn()).isNull();
        assertThat(createdSource.bindPassword()).isNull();
        assertThat(createdSource.subjectIdentifierAttribute()).isNull();
        assertThat(createdSource.mapping()).isNull();
        assertThat(createdSource.timeout()).isNull();
        assertThat(createdSource.retry()).isNull();
    }
}