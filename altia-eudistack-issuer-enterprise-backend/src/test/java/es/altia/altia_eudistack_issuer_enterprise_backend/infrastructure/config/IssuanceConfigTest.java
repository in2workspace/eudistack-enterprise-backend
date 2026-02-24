package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.client.CoreSigningConfigClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationRunner;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssuanceConfigTest {

    @Mock
    private CoreSigningConfigClient coreSigningConfigClient;
    @Mock
    private SignatureConfig signatureConfig;
    @InjectMocks
    private IssuanceConfig issuanceConfig;

    @BeforeEach
    void setUp() {
        issuanceConfig = new IssuanceConfig(coreSigningConfigClient, signatureConfig);
    }

    @Test
    void pushSigningConfigAtStartup_successOnFirstTry() throws Exception {
        when(signatureConfig.getProvider()).thenReturn("provider-ok");
        when(coreSigningConfigClient.pushSigningProvider("provider-ok")).thenReturn(Mono.empty());

        ApplicationRunner runner = issuanceConfig.pushSigningConfigAtStartup();
        runner.run(null);

        verify(coreSigningConfigClient, times(1)).pushSigningProvider("provider-ok");
    }
}
