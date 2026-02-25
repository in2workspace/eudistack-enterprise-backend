package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
public class OutgoingRequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution) throws IOException {

        long start = System.nanoTime();

        try {
            ClientHttpResponse response = execution.execute(request, body);
            long durationMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

            log.debug("Outgoing HTTP call -> method={}, uri={}, status={}, duration={}ms",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    durationMs
            );

            return response;

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;

            log.error("Outgoing HTTP call FAILED: method={}, uri={}, duration={}ms, error={}",
                    request.getMethod(),
                    request.getURI(),
                    duration,
                    ex.getMessage(),
                    ex
            );

            throw ex;
        }
    }
}