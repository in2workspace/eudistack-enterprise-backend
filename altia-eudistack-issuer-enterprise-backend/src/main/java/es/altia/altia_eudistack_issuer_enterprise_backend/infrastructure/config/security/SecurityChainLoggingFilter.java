package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class SecurityChainLoggingFilter extends OncePerRequestFilter {

    private final String chainName;

    public SecurityChainLoggingFilter(String chainName) {
        this.chainName = chainName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("SecurityFilterChain[{}] applied - {} {}",
                chainName,
                request.getMethod(),
                request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}
