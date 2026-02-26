package es.altia.altia_eudistack_issuer_enterprise_backend.domain.exception;

import java.io.Serial;

public class IssuanceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public IssuanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

