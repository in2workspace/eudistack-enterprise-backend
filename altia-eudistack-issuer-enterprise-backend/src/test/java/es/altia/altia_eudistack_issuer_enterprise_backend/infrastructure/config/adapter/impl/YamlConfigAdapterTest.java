package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YamlConfigAdapterTest {

    private final YamlConfigAdapter yamlConfigAdapter = new YamlConfigAdapter();

    @Test
    void GetConfiguration_ConfigurationKeyExists_ReturnsSameConfigurationKey() {
        // Arrange
        String key = "app.signing.core-url";

        // Act
        String result = yamlConfigAdapter.getConfiguration(key);

        // Assert
        assertThat(result).isEqualTo(key);
    }

    @Test
    void GetConfiguration_ConfigurationKeyIsNull_ReturnsNull() {
        // Act
        String result = yamlConfigAdapter.getConfiguration(null);

        // Assert
        assertThat(result).isNull();
    }
}