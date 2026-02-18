package es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto;

import lombok.Builder;

@Builder
public record GlobalErrorMessage(String title, String message, String path) {

}