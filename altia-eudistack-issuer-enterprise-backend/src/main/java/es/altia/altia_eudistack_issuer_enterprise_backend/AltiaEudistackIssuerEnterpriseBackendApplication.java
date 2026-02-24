package es.altia.altia_eudistack_issuer_enterprise_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AltiaEudistackIssuerEnterpriseBackendApplication {

    static void main(String[] args) {
        SpringApplication.run(AltiaEudistackIssuerEnterpriseBackendApplication.class, args);
    }

    private AltiaEudistackIssuerEnterpriseBackendApplication() {
        /* This utility class should not be instantiated */
    }

}
