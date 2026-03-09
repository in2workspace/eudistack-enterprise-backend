package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.properties;

import java.time.Duration;

final class DataAcquisitionPropertiesMother {

    private DataAcquisitionPropertiesMother() {
        // Utility class
    }

    static DataAcquisitionProperties.Source fullSource() {
        return new DataAcquisitionProperties.Source(
                "LEARCredentialEmployee",
                DataAcquisitionProperties.SourceType.MOCK,
                "ldaps://ldap.acme.com:636",
                "ou=employees,dc=acme,dc=com",
                "cn=issuer,dc=acme,dc=com",
                "${LDAP_PASSWORD}",
                "employeeNumber",
                new DataAcquisitionProperties.Mapping("givenName", "sn", "mail", "employeeNumber"),
                Duration.ofSeconds(30),
                new DataAcquisitionProperties.Retry(3, Duration.ofSeconds(1))
        );
    }

    static DataAcquisitionProperties.Source minimalSourceWithNulls() {
        return new DataAcquisitionProperties.Source(
                "LEARCredentialEmployee",
                DataAcquisitionProperties.SourceType.MOCK,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}