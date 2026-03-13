package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.SigningConfigHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuanceConfigTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SigningConfigHttpClient signingConfigHttpClient;

    @Mock
    private SignatureConfig signatureConfig;

    @Mock
    private RemoteSignatureConfig remoteSignatureConfig;

    @InjectMocks
    private IssuanceConfig issuanceConfig;

    @Test
    void pushSigningConfigAtStartup_WithCertificateInfoCacheTtl_PushesRequestSuccessfully() {
        // Arrange
        mockCommonConfiguration();
        when(remoteSignatureConfig.getCertificateInfoCacheTtl()).thenReturn(Duration.ofMinutes(10));

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();

        // Act
        assertThatCode(() -> runner.run(mock(ApplicationArguments.class))).doesNotThrowAnyException();

        // Assert
        SigningConfigPushRequest request = captureSentRequest();
        assertCommonRequestFields(request);
        assertRemoteSignatureFields(request);
        assertThat(request.remoteSignature().certificateInfoCacheTtl()).isEqualTo("PT10M");
        verifyNoMoreInteractions(signingConfigHttpClient);
    }

    @Test
    void pushSigningConfigAtStartup_WithNullCertificateInfoCacheTtl_PushesRequestWithNullTtl() {
        // Arrange
        mockCommonConfiguration();
        when(remoteSignatureConfig.getCertificateInfoCacheTtl()).thenReturn(null);

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();

        // Act
        assertThatCode(() -> runner.run(mock(ApplicationArguments.class))).doesNotThrowAnyException();

        // Assert
        SigningConfigPushRequest request = captureSentRequest();
        assertCommonRequestFields(request);
        assertRemoteSignatureFields(request);
        assertThat(request.remoteSignature().certificateInfoCacheTtl()).isNull();
        verifyNoMoreInteractions(signingConfigHttpClient);
    }

    @Test
    void pushSigningConfigAtStartup_WhenPushFails_ClosesApplication() throws Exception {
        // Arrange
        mockCommonConfiguration();
        when(remoteSignatureConfig.getCertificateInfoCacheTtl()).thenReturn(Duration.ofMinutes(10));
        doThrow(new RuntimeException("Core unavailable"))
                .when(signingConfigHttpClient)
                .executeSigningConfigRequest(any(SigningConfigPushRequest.class));

        IssuanceConfig spyIssuanceConfig = spy(issuanceConfig);
        doNothing().when(spyIssuanceConfig).exitApplication(anyInt());

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock
                    .when(() -> SpringApplication.exit(eq(applicationContext), any(ExitCodeGenerator.class)))
                    .thenReturn(1);

            ApplicationRunner runner = spyIssuanceConfig.pushSigningConfigAtStartup();

            // Act
            assertThatCode(() -> runner.run(mock(ApplicationArguments.class))).doesNotThrowAnyException();

            // Assert
            verify(spyIssuanceConfig).exitApplication(1);
            springApplicationMock.verify(() ->
                    SpringApplication.exit(eq(applicationContext), any(ExitCodeGenerator.class)));
        }
    }

    private void mockCommonConfiguration() {
        when(signatureConfig.getProvider()).thenReturn("csc-sign-hash");
        when(remoteSignatureConfig.getRemoteSignatureType()).thenReturn("cloud");
        when(remoteSignatureConfig.getRemoteSignatureDomain()).thenReturn("https://qtsp.example.com");
        when(remoteSignatureConfig.getRemoteSignatureSignPath()).thenReturn("/csc/v2/signatures/signHash");
        when(remoteSignatureConfig.getRemoteSignatureClientId()).thenReturn("clientId");
        when(remoteSignatureConfig.getRemoteSignatureClientSecret()).thenReturn("clientSecret");
        when(remoteSignatureConfig.getRemoteSignatureCredentialId()).thenReturn("cred-id");
        when(remoteSignatureConfig.getRemoteSignatureCredentialPassword()).thenReturn("cred-pwd");
    }

    private SigningConfigPushRequest captureSentRequest() {
        ArgumentCaptor<SigningConfigPushRequest> captor = ArgumentCaptor.forClass(SigningConfigPushRequest.class);
        verify(signingConfigHttpClient).executeSigningConfigRequest(captor.capture());
        return captor.getValue();
    }

    private void assertCommonRequestFields(SigningConfigPushRequest request) {
        assertThat(request).isNotNull();
        assertThat(request.provider()).isEqualTo("csc-sign-hash");
        assertThat(request.remoteSignature()).isNotNull();
    }

    private void assertRemoteSignatureFields(SigningConfigPushRequest request) {
        assertThat(request.remoteSignature().type()).isEqualTo("cloud");
        assertThat(request.remoteSignature().url()).isEqualTo("https://qtsp.example.com");
        assertThat(request.remoteSignature().signPath()).isEqualTo("/csc/v2/signatures/signHash");
        assertThat(request.remoteSignature().clientId()).isEqualTo("clientId");
        assertThat(request.remoteSignature().clientSecret()).isEqualTo("clientSecret");
        assertThat(request.remoteSignature().credentialId()).isEqualTo("cred-id");
        assertThat(request.remoteSignature().credentialPassword()).isEqualTo("cred-pwd");
    }
}