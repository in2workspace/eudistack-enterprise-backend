package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.controller;

import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.AuthenticSourceWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.altia.altia_eudistack_issuer_enterprise_backend.domain.util.EndpointConstants.AUTHENTIC_SOURCE_ISSUANCE_PATH;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("dev")
@RequestMapping(AUTHENTIC_SOURCE_ISSUANCE_PATH)
public class AuthenticSourceIssuanceController {

    private final AuthenticSourceWorkflow authenticSourceWorkflow;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void issuanceFromAuthenticSource() {
        log.info("Starting issuance process for authentic source credential");

        authenticSourceWorkflow.execute();

        log.info("Ending issuance process for authentic source credential");
    }
}

