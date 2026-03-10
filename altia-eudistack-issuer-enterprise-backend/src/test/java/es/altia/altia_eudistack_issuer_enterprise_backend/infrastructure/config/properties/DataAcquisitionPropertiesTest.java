package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = DataAcquisitionPropertiesTest.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@EnableConfigurationProperties(DataAcquisitionProperties.class)
class DataAcquisitionPropertiesTest {

    @Autowired
    private DataAcquisitionProperties properties;

    @Test
    void bindsFromTestApplicationYaml_withNestedRecords() {
        var actual = properties.sources().getFirst();
        var expected = DataAcquisitionPropertiesMother.fullSource();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }


}