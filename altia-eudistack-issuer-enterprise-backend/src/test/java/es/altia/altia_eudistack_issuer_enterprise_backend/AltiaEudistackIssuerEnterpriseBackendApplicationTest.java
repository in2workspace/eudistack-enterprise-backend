package es.altia.altia_eudistack_issuer_enterprise_backend;

import es.altia.altia_eudistack_issuer_enterprise_backend.shared.infrastructure.config.TestAuditServiceConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditServiceConfiguration.class)
class AltiaEudistackIssuerEnterpriseBackendApplicationTest {

    @Test
    @SuppressWarnings("EmptyMethod")
    void contextLoads() {
    }

}

