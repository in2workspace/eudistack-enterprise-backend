package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoteSignaturePropertiesTest {

    @Test
    void createRemoteSignatureProperties_WithProvidedValues_PropertiesMatchExpectedValues() {
        // Arrange
        RemoteSignatureProperties.Paths paths = new RemoteSignatureProperties.Paths("/signature/sign");

        // Act
        RemoteSignatureProperties properties = new RemoteSignatureProperties(
                "server",
                "https://remote-signature.example.com",
                paths,
                "client-id",
                "client-secret",
                "credential-id",
                "credential-password",
                "PT15M"
        );

        // Assert
        assertThat(properties.type()).isEqualTo("server");
        assertThat(properties.url()).isEqualTo("https://remote-signature.example.com");
        assertThat(properties.paths()).isEqualTo(paths);
        assertThat(properties.paths().signPath()).isEqualTo("/signature/sign");
        assertThat(properties.clientId()).isEqualTo("client-id");
        assertThat(properties.clientSecret()).isEqualTo("client-secret");
        assertThat(properties.credentialId()).isEqualTo("credential-id");
        assertThat(properties.credentialPassword()).isEqualTo("credential-password");
        assertThat(properties.certificateInfoCacheTtl()).isEqualTo("PT15M");
    }

    @Test
    void createRemoteSignatureProperties_WithNullPathsAndCacheTtl_DefaultValuesApplied() {
        // Arrange & Act
        RemoteSignatureProperties properties = new RemoteSignatureProperties(
                "server",
                "https://remote-signature.example.com",
                null,
                "client-id",
                "client-secret",
                "credential-id",
                "credential-password",
                null
        );

        // Assert
        assertThat(properties.type()).isEqualTo("server");
        assertThat(properties.url()).isEqualTo("https://remote-signature.example.com");
        assertThat(properties.paths()).isNotNull();
        assertThat(properties.paths().signPath()).isEmpty();
        assertThat(properties.clientId()).isEqualTo("client-id");
        assertThat(properties.clientSecret()).isEqualTo("client-secret");
        assertThat(properties.credentialId()).isEqualTo("credential-id");
        assertThat(properties.credentialPassword()).isEqualTo("credential-password");
        assertThat(properties.certificateInfoCacheTtl()).isEqualTo("PT10M");
    }
}