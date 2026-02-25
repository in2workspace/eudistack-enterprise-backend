package es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SigningConfigPushRequest(
        @NotBlank String provider,
        @NotNull @Valid RemoteSignatureConfigDto remoteSignature
) {}