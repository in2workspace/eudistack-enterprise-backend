package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception.DataAcquisitionProviderNotConfiguredException;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.GlobalErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAcquisitionProviderNotConfiguredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GlobalErrorMessage handleDataAcquisitionProviderNotConfiguredException(
            DataAcquisitionProviderNotConfiguredException exception,
            HttpServletRequest request) {
        log.error("DataAcquisitionProviderNotConfiguredException: {}", exception.getMessage());

        String path = String.valueOf(request.getRequestURI());
        return GlobalErrorMessage.builder()
                .title("DataAcquisitionProviderNotConfiguredException")
                .message(exception.getMessage())
                .path(path)
                .build();
    }
}
