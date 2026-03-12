package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.rest.SigningConfigHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@ExtendWith(MockitoExtension.class)
class IssuanceConfigTest {

    @Mock
    private SigningConfigHttpClient signingConfigHttpClient;

    @Mock
    private SignatureConfig signatureConfig;

    @Mock
    private RemoteSignatureConfig remoteSignatureConfig;

    @InjectMocks
    private IssuanceConfig issuanceConfig;

    @Test
    void pushSigningConfigAtStartup_WithCertificateInfoCacheTtl_PushesRequestSuccessfully(){
        // Arrange
        mockCommonConfiguration();
        when(remoteSignatureConfig.getCertificateInfoCacheTtl()).thenReturn(Duration.ofMinutes(10));

        // Act
        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();
        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();

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

        // Act
        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();
        assertThatCode(() -> runner.run(null)).doesNotThrowAnyException();

        // Assert
        SigningConfigPushRequest request = captureSentRequest();
        assertCommonRequestFields(request);
        assertRemoteSignatureFields(request);
        assertThat(request.remoteSignature().certificateInfoCacheTtl()).isNull();
        verifyNoMoreInteractions(signingConfigHttpClient);
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