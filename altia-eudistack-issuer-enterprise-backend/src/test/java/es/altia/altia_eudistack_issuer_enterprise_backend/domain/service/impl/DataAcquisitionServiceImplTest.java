package es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.service.DataAcquisitionProviderRegistry;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.adapter.acquisition.DataAcquisitionProvider;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.DataAcquisitionConfiguration;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties.DataAcquisitionProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private DataAcquisitionServiceImpl service;

    @Test
    void Acquire_CredentialConfigurationIdExists_ReturnsAcquiredData() {
        // Arrange
        DataAcquisitionProperties.Source source = buildSource(CREDENTIAL_CONFIGURATION_ID);

        when(configuration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of(CREDENTIAL_CONFIGURATION_ID, source));
        when(providerRegistry.get(DataAcquisitionProperties.SourceType.MOCK))
                .thenReturn(provider);
        when(provider.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER))
                .thenReturn("acquired-data");

        // Act
        String result = service.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);

        // Assert
        assertThat(result).isEqualTo("acquired-data");

        verify(configuration).sourcesByCredentialConfigurationId();
        verify(providerRegistry).get(DataAcquisitionProperties.SourceType.MOCK);
        verify(provider).acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER);
        verifyNoMoreInteractions(configuration, providerRegistry, provider);
    }

    @Test
    void Acquire_CredentialConfigurationIdIsUnknown_ThrowsIllegalArgumentException() {
        // Arrange
        when(configuration.sourcesByCredentialConfigurationId())
                .thenReturn(Map.of());

        // Act & Assert
        assertThatThrownBy(() -> service.acquire(CREDENTIAL_CONFIGURATION_ID, SUBJECT_IDENTIFIER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown credentialConfigurationId: " + CREDENTIAL_CONFIGURATION_ID);

        verify(configuration).sourcesByCredentialConfigurationId();
        verifyNoInteractions(providerRegistry, provider);
        verifyNoMoreInteractions(configuration, providerRegistry, provider);
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