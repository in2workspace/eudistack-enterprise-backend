package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.RemoteSignatureConfigDto;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.SigningConfigPushRequest;
import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client.CoreSigningConfigClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuanceConfigTest {

    @Mock
    private CoreSigningConfigClient coreSigningConfigClient;
    @Mock
    private SignatureConfig signatureConfig;
    @Mock
    private RemoteSignatureConfig remoteSignatureConfig;

    @InjectMocks
    private IssuanceConfig issuanceConfig;

    @BeforeEach
    void setUp() {
        issuanceConfig = new IssuanceConfig(coreSigningConfigClient, signatureConfig, remoteSignatureConfig);
    }

    @Test
    void pushSigningConfigAtStartup_successOnFirstTry() throws Exception {

        when(signatureConfig.getProvider()).thenReturn("csc-sign-hash");

        when(remoteSignatureConfig.getRemoteSignatureType()).thenReturn("cloud");
        when(remoteSignatureConfig.getRemoteSignatureDomain()).thenReturn("https://qtsp.example.com");
        when(remoteSignatureConfig.getRemoteSignatureSignPath()).thenReturn("/csc/v2/signatures/signHash");
        when(remoteSignatureConfig.getRemoteSignatureClientId()).thenReturn("clientId");
        when(remoteSignatureConfig.getRemoteSignatureClientSecret()).thenReturn("clientSecret");
        when(remoteSignatureConfig.getRemoteSignatureCredentialId()).thenReturn("cred-id");
        when(remoteSignatureConfig.getRemoteSignatureCredentialPassword()).thenReturn("cred-pwd");
        when(remoteSignatureConfig.getCertificateInfoCacheTtl()).thenReturn(Duration.ofMinutes(10));

        doNothing().when(coreSigningConfigClient).pushSigningConfig(any(SigningConfigPushRequest.class));

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();
        runner.run(null);

        ArgumentCaptor<SigningConfigPushRequest> captor = ArgumentCaptor.forClass(SigningConfigPushRequest.class);
        verify(coreSigningConfigClient, times(1)).pushSigningConfig(captor.capture());

        SigningConfigPushRequest req = captor.getValue();
        assertNotNull(req);
        assertEquals("csc-sign-hash", req.provider());

        RemoteSignatureConfigDto remote = req.remoteSignature();
        assertNotNull(remote);

        assertEquals("cloud", remote.type());
        assertEquals("https://qtsp.example.com", remote.url());
        assertEquals("/csc/v2/signatures/signHash", remote.signPath());

        assertEquals("clientId", remote.clientId());
        assertEquals("clientSecret", remote.clientSecret());
        assertEquals("cred-id", remote.credentialId());
        assertEquals("cred-pwd", remote.credentialPassword());

        assertEquals("PT10M", remote.certificateInfoCacheTtl());
    }
}