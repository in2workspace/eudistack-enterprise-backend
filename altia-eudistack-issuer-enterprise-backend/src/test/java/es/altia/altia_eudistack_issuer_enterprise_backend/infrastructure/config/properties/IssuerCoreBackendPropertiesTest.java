package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class IssuerCoreBackendPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withUserConfiguration(TestConfig.class);

    @Test
    void shouldBindIssuerCoreBackendPropertiesSuccessfully() {
        contextRunner
                .withPropertyValues("app.issuer-core-backend.url=https://issuer-core.example.com")
                .run(context -> {
                    assertThat(context).hasNotFailed();

                    IssuerCoreBackendProperties properties = context.getBean(IssuerCoreBackendProperties.class);

                    assertThat(properties.url()).isEqualTo("https://issuer-core.example.com");
                });
    }

    @Test
    void shouldFailWhenUrlIsBlank() {
        contextRunner
                .withPropertyValues("app.issuer-core-backend.url=")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("url")
                            .hasStackTraceContaining("must not be blank");
                });
    }

    @Test
    void shouldFailWhenUrlIsNotValid() {
        contextRunner
                .withPropertyValues("app.issuer-core-backend.url=not-a-valid-url")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("url")
                            .hasStackTraceContaining("must be a valid URL");
                });
    }

    @Configuration
    @EnableConfigurationProperties(IssuerCoreBackendProperties.class)
    static class TestConfig {
    }
}