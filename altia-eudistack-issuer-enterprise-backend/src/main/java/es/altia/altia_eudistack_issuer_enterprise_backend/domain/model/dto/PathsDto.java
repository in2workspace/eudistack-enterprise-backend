package es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto;

import jakarta.validation.constraints.NotBlank;

public record PathsDto(@NotBlank String signPath) {}
