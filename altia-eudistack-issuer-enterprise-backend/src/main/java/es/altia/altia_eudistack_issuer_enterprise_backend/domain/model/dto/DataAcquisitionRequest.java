package es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DataAcquisitionRequest(

        @NotBlank
        @JsonProperty(value = "credentialConfigurationId", required = true)
        String credentialConfigurationId,

        @NotBlank
        @JsonProperty(value = "subjectIdentifier", required = true)
        String subjectIdentifier,

        @JsonProperty(value = "holderEmail")
        String holderEmail) {
}
