package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SecurityChainLoggingFilterTest {

    private SecurityChainLoggingFilter filter;
    private FilterChain filterChain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new SecurityChainLoggingFilter("test-chain");
        filterChain = mock(FilterChain.class);
        request = new MockHttpServletRequest("GET", "/procedures/acquire");
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldDelegateToFilterChain() throws ServletException, IOException{
        assertThatCode(() -> filter.doFilter(request, response, filterChain))
                .doesNotThrowAnyException();

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    void shouldPropagateServletExceptionFromFilterChain() throws Exception {
        ServletException exception = new ServletException("Filter chain failed");
        doThrow(exception).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isSameAs(exception);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    void shouldPropagateIOExceptionFromFilterChain() throws Exception {
        IOException exception = new IOException("I/O failure");
        doThrow(exception).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isSameAs(exception);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }
}