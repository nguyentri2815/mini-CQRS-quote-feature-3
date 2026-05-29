package com.example.quote_service_eventstore.shared.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);

        MDC.put(
                ObservabilityConstants.CORRELATION_ID_MDC_KEY,
                correlationId
        );

        response.setHeader(
                ObservabilityConstants.CORRELATION_ID_HEADER,
                correlationId
        );

        try {
            log.info(
                    "[HTTP] Incoming request. method={}, uri={}, correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    correlationId
            );

            filterChain.doFilter(request, response);

            log.info(
                    "[HTTP] Completed request. method={}, uri={}, status={}, correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    correlationId
            );
        } finally {
            MDC.remove(ObservabilityConstants.CORRELATION_ID_MDC_KEY);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String headerValue = request.getHeader(
                ObservabilityConstants.CORRELATION_ID_HEADER
        );

        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue;
        }

        return UUID.randomUUID().toString();
    }
}
