package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import org.springframework.web.bind.annotation.*;
import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.DataAcquisitionWorkflow;
import es.altia.altia_eudistack_issuer_enterprise_backend.domain.model.dto.DataAcquisitionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.DATA_ACQUISITION_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(DATA_ACQUISITION_PATH)
public class DataAcquisitionController {

    private final DataAcquisitionWorkflow dataAcquisitionWorkflow;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acquire(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @Valid @RequestBody DataAcquisitionRequest request) {
        log.info("Starting data acquisition: credentialConfigurationId={}, subjectIdentifier={}",
                request.credentialConfigurationId(),
                request.subjectIdentifier()
        );

        dataAcquisitionWorkflow.execute(
                bearerToken,
                request.credentialConfigurationId(),
                request.subjectIdentifier(),
                request.holderEmail()
        );

        log.info("Ending data acquisition from authentic source");
    }
}

