package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import es.altia.altia_eudistack_issuer_enterprise_backend.AltiaEudistackIssuerEnterpriseBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = DataAcquisitionPropertiesTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class DataAcquisitionPropertiesTest {

    @Autowired
    private DataAcquisitionProperties properties;

    @Autowired
    private ApplicationContext context;

    @Test
    void bindsFromTestApplicationYaml_withNestedRecords() {
        var actual = properties.sources().getFirst();
        var expected = DataAcquisitionPropertiesMother.fullSource();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @EnableConfigurationProperties(DataAcquisitionProperties.class)
    static class TestApplication {
        // Intentionally minimal: no component scanning => no startup runners => no HTTP calls
    }


}