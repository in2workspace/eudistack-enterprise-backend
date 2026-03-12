package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = SignaturePropertiesTest.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableConfigurationProperties(SignatureProperties.class)
class SignaturePropertiesTest {

    @Autowired
    private SignatureProperties properties;

    @Test
    void BindSignatureProperties_WithConfiguredApplicationProperties_BindsSuccessfully() {
        assertThat(properties)
                .extracting(
                        SignatureProperties::provider,
                        SignatureProperties::coreUrl
                )
                .containsExactly(
                        "in-memory",
                        "https://issuer.dummy.eudistack.net"
                );
    }
}