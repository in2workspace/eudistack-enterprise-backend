package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = RemoteSignaturePropertiesTest.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableConfigurationProperties(RemoteSignatureProperties.class)
class RemoteSignaturePropertiesTest {

    @Autowired
    private RemoteSignatureProperties properties;

    @Test
    void BindRemoteSignatureProperties_WithConfiguredApplicationProperties_BindsSuccessfully() {
        assertThat(properties)
                .usingRecursiveComparison()
                .isEqualTo(new RemoteSignatureProperties(
                        "type",
                        "https://remote-signature.dummy.eudistack.net",
                        new RemoteSignatureProperties.Paths("/signature/sign"),
                        "client-id",
                        "client-secret",
                        "credential-id",
                        "credential-password",
                        "PT10M"
                ));
    }
}