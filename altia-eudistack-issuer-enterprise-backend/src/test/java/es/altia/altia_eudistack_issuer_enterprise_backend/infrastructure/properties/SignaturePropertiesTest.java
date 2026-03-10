package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class SignaturePropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    @Test
    void bindSignatureProperties_ValidProperties_BindsSuccessfully() {
        // Arrange
        // Act & Assert
        contextRunner
                .withPropertyValues(
                        "app.signing.provider=remote-signature",
                        "app.signing.core-url=https://core.example.com"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();

                    SignatureProperties properties = context.getBean(SignatureProperties.class);

                    assertThat(properties.provider()).isEqualTo("remote-signature");
                    assertThat(properties.coreUrl()).isEqualTo("https://core.example.com");
                });
    }

    @Test
    void bindSignatureProperties_BlankProvider_ContextFails() {
        // Arrange
        // Act & Assert
        contextRunner
                .withPropertyValues(
                        "app.signing.provider=",
                        "app.signing.core-url=https://core.example.com"
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("provider")
                            .hasStackTraceContaining("must not be blank");
                });
    }

    @Test
    void bindSignatureProperties_BlankCoreUrl_ContextFails() {
        // Arrange
        // Act & Assert
        contextRunner
                .withPropertyValues(
                        "app.signing.provider=remote-signature",
                        "app.signing.core-url="
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("coreUrl")
                            .hasStackTraceContaining("must not be blank");
                });
    }

    @Test
    void bindSignatureProperties_InvalidCoreUrl_ContextFails() {
        // Arrange
        // Act & Assert
        contextRunner
                .withPropertyValues(
                        "app.signing.provider=remote-signature",
                        "app.signing.core-url=not-a-valid-url"
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("coreUrl")
                            .hasStackTraceContaining("must be a valid URL");
                });
    }

    @Configuration
    @EnableConfigurationProperties(SignatureProperties.class)
    static class TestConfig {
    }
}