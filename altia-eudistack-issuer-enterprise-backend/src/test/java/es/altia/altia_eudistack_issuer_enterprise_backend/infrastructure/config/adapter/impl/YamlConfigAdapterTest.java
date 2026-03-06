package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YamlConfigAdapterTest {

    private final YamlConfigAdapter yamlConfigAdapter = new YamlConfigAdapter();

    @Test
    void shouldReturnSameConfigurationKey() {
        String key = "app.signing.core-url";

        String result = yamlConfigAdapter.getConfiguration(key);

        assertThat(result).isEqualTo(key);
    }

    @Test
    void shouldReturnNullWhenConfigurationKeyIsNull() {
        String result = yamlConfigAdapter.getConfiguration(null);

        assertThat(result).isNull();
    }
}