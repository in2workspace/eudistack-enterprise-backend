package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SecurityChainLoggingFilterTest {

    private final SecurityChainLoggingFilter filter = new SecurityChainLoggingFilter("test-chain");

    @Mock
    private FilterChain filterChain;

    @Test
    void DoFilter_FilterChainSucceeds_DelegatesToFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatCode(() -> filter.doFilter(request, response, filterChain))
                .doesNotThrowAnyException();

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    void DoFilter_FilterChainThrowsServletException_PropagatesServletException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletException exception = new ServletException("Filter chain failed");

        doThrow(exception).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isSameAs(exception);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }

    @Test
    void DoFilter_FilterChainThrowsIOException_PropagatesIOException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/procedures/acquire");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IOException exception = new IOException("I/O failure");

        doThrow(exception).when(filterChain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
                .isSameAs(exception);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(filterChain);
    }
}