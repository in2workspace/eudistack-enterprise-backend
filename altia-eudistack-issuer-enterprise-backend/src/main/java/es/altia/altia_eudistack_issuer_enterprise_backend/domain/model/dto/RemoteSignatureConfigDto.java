package es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RemoteSignatureConfigDto(
        @NotBlank String type,
        @NotBlank String url,
        @NotNull PathsDto paths,
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String credentialId,
        @NotBlank String credentialPassword,
        String certificateInfoCacheTtl
) {}

