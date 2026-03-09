package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;


import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionProviderRegistry;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DataAcquisitionServiceImplTest {

    private static final String CREDENTIAL_CONFIGURATION_ID = "LEARCredentialEmployee";
    private static final String SUBJECT_IDENTIFIER = "subject-123";

    @Mock
    private DataAcquisitionConfiguration configuration;

    @Mock
    private DataAcquisitionProviderRegistry providerRegistry;

    @Mock
    private DataAcquisitionProvider provider;

    private DataAcquisitionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DataAcquisitionServiceImpl(configuration, providerRegistry);
    }

    @Test
    void shouldAcquireDataUsingProviderResolvedFromSourceType() {
        DataAcquisitionProperties.Source source = buildSource(CREDENTIAL_CONFIGURATION_ID);

        given(configuration.sourcesByCredentialConfigurationId())
                .willReturn(Map.of(CREDENTIAL_CONFIGURATION_ID, source));
        given(providerRegistry.get(DataAcquisitionProperties.SourceType.MOCK))
                .willReturn(provider);
        given(provider.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER))
                .willReturn("acquired-data");

        String result = service.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);

        assertThat(result).isEqualTo("acquired-data");

        verify(configuration).sourcesByCredentialConfigurationId();
        verify(providerRegistry).get(DataAcquisitionProperties.SourceType.MOCK);
        verify(provider).acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);
        verifyNoMoreInteractions(configuration, providerRegistry, provider);
    }

    @Test
    void shouldThrowExceptionWhenCredentialConfigurationIdIsUnknown() {
        given(configuration.sourcesByCredentialConfigurationId())
                .willReturn(Map.of());

        assertThatThrownBy(() -> service.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown credentialConfigurationId: " + CREDENTIAL_CONFIGURATION_ID);

        verify(configuration).sourcesByCredentialConfigurationId();
        verifyNoMoreInteractions(configuration, providerRegistry, provider);
    }

    @Test
    void shouldDelegateToProviderResolvedFromSourceType() {
        DataAcquisitionProperties.Source source = buildSource(CREDENTIAL_CONFIGURATION_ID);

        given(configuration.sourcesByCredentialConfigurationId())
                .willReturn(Map.of(CREDENTIAL_CONFIGURATION_ID, source));
        given(providerRegistry.get(source.type()))
                .willReturn(provider);
        given(provider.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER))
                .willReturn("mapped-credential-subject");

        String result = service.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);

        assertThat(result).isEqualTo("mapped-credential-subject");

        verify(providerRegistry).get(DataAcquisitionProperties.SourceType.MOCK);
        verify(provider).acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);
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
