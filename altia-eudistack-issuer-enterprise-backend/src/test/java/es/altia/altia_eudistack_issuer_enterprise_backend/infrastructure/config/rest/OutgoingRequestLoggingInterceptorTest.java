package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class OutgoingRequestLoggingInterceptorTest {

    private static final byte[] BODY = "{\"key\":\"value\"}".getBytes();

    private OutgoingRequestLoggingInterceptor interceptor;
    private ClientHttpRequestExecution execution;

    @BeforeEach
    void setUp() {
        interceptor = new OutgoingRequestLoggingInterceptor();
        execution = mock(ClientHttpRequestExecution.class);
    }

    @Test
    void shouldExecuteRequestAndReturnResponse() throws IOException {
        HttpRequest request = buildRequest();
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        when(execution.execute(request, BODY)).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        ClientHttpResponse result = interceptor.intercept(request, BODY, execution);

        assertThat(result).isSameAs(response);

        verify(execution).execute(request, BODY);
        verify(response).getStatusCode();
        verifyNoMoreInteractions(execution, response);
    }

    @Test
    void shouldRethrowExceptionWhenExecutionFails() throws IOException {
        HttpRequest request = buildRequest();
        IOException exception = new IOException("Connection failed");

        when(execution.execute(request, BODY)).thenThrow(exception);

        assertThatThrownBy(() -> interceptor.intercept(request, BODY, execution))
                .isSameAs(exception);

        verify(execution).execute(request, BODY);
        verifyNoMoreInteractions(execution);
    }

    private HttpRequest buildRequest() {
        MockClientHttpRequest request = new MockClientHttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setURI(URI.create("https://example.com/api/resource"));
        return request;
    }
}