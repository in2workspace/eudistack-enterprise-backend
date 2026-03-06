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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    void shouldPushSigningConfigAtStartupWithCertificateInfoCacheTtl() throws Exception {
        given(signatureConfig.getProvider()).willReturn("csc-sign-hash");
        given(remoteSignatureConfig.getRemoteSignatureType()).willReturn("cloud");
        given(remoteSignatureConfig.getRemoteSignatureDomain()).willReturn("https://qtsp.example.com");
        given(remoteSignatureConfig.getRemoteSignatureSignPath()).willReturn("/csc/v2/signatures/signHash");
        given(remoteSignatureConfig.getRemoteSignatureClientId()).willReturn("clientId");
        given(remoteSignatureConfig.getRemoteSignatureClientSecret()).willReturn("clientSecret");
        given(remoteSignatureConfig.getRemoteSignatureCredentialId()).willReturn("cred-id");
        given(remoteSignatureConfig.getRemoteSignatureCredentialPassword()).willReturn("cred-pwd");
        given(remoteSignatureConfig.getCertificateInfoCacheTtl()).willReturn(Duration.ofMinutes(10));

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();

        assertThatCode(() -> runner.run(null))
                .doesNotThrowAnyException();

        ArgumentCaptor<SigningConfigPushRequest> captor = ArgumentCaptor.forClass(SigningConfigPushRequest.class);
        verify(signingConfigHttpClient, times(1)).executeSigningConfigRequest(captor.capture());

        SigningConfigPushRequest request = captor.getValue();

        assertThat(request).isNotNull();
        assertThat(request.provider()).isEqualTo("csc-sign-hash");
        assertThat(request.remoteSignature()).isNotNull();
        assertThat(request.remoteSignature().type()).isEqualTo("cloud");
        assertThat(request.remoteSignature().url()).isEqualTo("https://qtsp.example.com");
        assertThat(request.remoteSignature().signPath()).isEqualTo("/csc/v2/signatures/signHash");
        assertThat(request.remoteSignature().clientId()).isEqualTo("clientId");
        assertThat(request.remoteSignature().clientSecret()).isEqualTo("clientSecret");
        assertThat(request.remoteSignature().credentialId()).isEqualTo("cred-id");
        assertThat(request.remoteSignature().credentialPassword()).isEqualTo("cred-pwd");
        assertThat(request.remoteSignature().certificateInfoCacheTtl()).isEqualTo("PT10M");

        verify(signatureConfig).getProvider();
        verify(remoteSignatureConfig).getRemoteSignatureType();
        verify(remoteSignatureConfig).getRemoteSignatureDomain();
        verify(remoteSignatureConfig).getRemoteSignatureSignPath();
        verify(remoteSignatureConfig).getRemoteSignatureClientId();
        verify(remoteSignatureConfig).getRemoteSignatureClientSecret();
        verify(remoteSignatureConfig).getRemoteSignatureCredentialId();
        verify(remoteSignatureConfig).getRemoteSignatureCredentialPassword();
        verify(remoteSignatureConfig, times(2)).getCertificateInfoCacheTtl();
        verifyNoMoreInteractions(signingConfigHttpClient, signatureConfig, remoteSignatureConfig);
    }

    @Test
    void shouldPushSigningConfigAtStartupWithNullCertificateInfoCacheTtl() throws Exception {
        given(signatureConfig.getProvider()).willReturn("csc-sign-hash");
        given(remoteSignatureConfig.getRemoteSignatureType()).willReturn("cloud");
        given(remoteSignatureConfig.getRemoteSignatureDomain()).willReturn("https://qtsp.example.com");
        given(remoteSignatureConfig.getRemoteSignatureSignPath()).willReturn("/csc/v2/signatures/signHash");
        given(remoteSignatureConfig.getRemoteSignatureClientId()).willReturn("clientId");
        given(remoteSignatureConfig.getRemoteSignatureClientSecret()).willReturn("clientSecret");
        given(remoteSignatureConfig.getRemoteSignatureCredentialId()).willReturn("cred-id");
        given(remoteSignatureConfig.getRemoteSignatureCredentialPassword()).willReturn("cred-pwd");
        given(remoteSignatureConfig.getCertificateInfoCacheTtl()).willReturn(null);

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();

        assertThatCode(() -> runner.run(null))
                .doesNotThrowAnyException();

        ArgumentCaptor<SigningConfigPushRequest> captor = ArgumentCaptor.forClass(SigningConfigPushRequest.class);
        verify(signingConfigHttpClient).executeSigningConfigRequest(captor.capture());

        SigningConfigPushRequest request = captor.getValue();

        assertThat(request).isNotNull();
        assertThat(request.provider()).isEqualTo("csc-sign-hash");
        assertThat(request.remoteSignature()).isNotNull();
        assertThat(request.remoteSignature().type()).isEqualTo("cloud");
        assertThat(request.remoteSignature().url()).isEqualTo("https://qtsp.example.com");
        assertThat(request.remoteSignature().signPath()).isEqualTo("/csc/v2/signatures/signHash");
        assertThat(request.remoteSignature().clientId()).isEqualTo("clientId");
        assertThat(request.remoteSignature().clientSecret()).isEqualTo("clientSecret");
        assertThat(request.remoteSignature().credentialId()).isEqualTo("cred-id");
        assertThat(request.remoteSignature().credentialPassword()).isEqualTo("cred-pwd");
        assertThat(request.remoteSignature().certificateInfoCacheTtl()).isNull();

        verify(signatureConfig).getProvider();
        verify(remoteSignatureConfig).getRemoteSignatureType();
        verify(remoteSignatureConfig).getRemoteSignatureDomain();
        verify(remoteSignatureConfig).getRemoteSignatureSignPath();
        verify(remoteSignatureConfig).getRemoteSignatureClientId();
        verify(remoteSignatureConfig).getRemoteSignatureClientSecret();
        verify(remoteSignatureConfig).getRemoteSignatureCredentialId();
        verify(remoteSignatureConfig).getRemoteSignatureCredentialPassword();
        verify(remoteSignatureConfig, times(1)).getCertificateInfoCacheTtl();
        verifyNoMoreInteractions(signingConfigHttpClient, signatureConfig, remoteSignatureConfig);
    }
}