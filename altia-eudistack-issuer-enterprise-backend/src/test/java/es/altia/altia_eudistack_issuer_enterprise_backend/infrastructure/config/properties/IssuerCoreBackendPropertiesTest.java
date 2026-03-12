package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = IssuerCoreBackendPropertiesTest.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableConfigurationProperties(IssuerCoreBackendProperties.class)
class IssuerCoreBackendPropertiesTest {

    @Autowired
    private IssuerCoreBackendProperties properties;

    @Test
    void BindIssuerCoreBackendProperties_WithConfiguredApplicationProperties_BindsSuccessfully() {
        assertThat(properties.url()).isEqualTo("https://issuer.red.eudistack.net");
    }

}