package es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.AuthenticSourceWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthenticSourceWorkflowImpl implements AuthenticSourceWorkflow {

    @Override
    public void execute() {
        log.error("AuthenticSourceWorkflow.execute is not yet implemented");
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "AuthenticSourceWorkflow.execute is not yet implemented",
                new UnsupportedOperationException());
    }
}
