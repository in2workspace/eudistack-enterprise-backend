package es.altia.altia_eudistack_issuer_enterprise_backend.application.workflow.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class AuthenticSourceWorkflowImplTest {

    @InjectMocks
    private AuthenticSourceWorkflowImpl authenticSourceWorkflow;

    @Test
    void execute_ShouldThrowResponseStatusExceptionWithNotImplementedStatus() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> authenticSourceWorkflow.execute())
                .satisfies(ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
                    assertThat(ex.getMessage()).contains("AuthenticSourceWorkflow.execute is not yet implemented");
                    assertThat(ex.getCause()).isInstanceOf(UnsupportedOperationException.class);
                });
    }
}




