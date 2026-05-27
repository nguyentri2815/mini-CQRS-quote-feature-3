package com.example.quote_service_eventstore.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class FakeAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        CurrentUser currentUser = new CurrentUser(
                getHeaderOrDefault(request, "X-User-Id", "u-demo"),
                getHeaderOrDefault(request, "X-Username", "demo-user"),
                getHeaderOrDefault(request, "X-Tenant-Id", "tenant-demo"),
                getHeaderOrDefault(request, "X-Organization-Id", "org-demo"),
                parseRoles(getHeaderOrDefault(request, "X-Roles", "QUOTE_CREATOR,QUOTE_SUBMITTER,QUOTE_APPROVER"))
        );

        request.setAttribute(
                RequestHeaderCurrentUserContext.CURRENT_USER_ATTRIBUTE,
                currentUser
        );

        filterChain.doFilter(request, response);
    }

    private String getHeaderOrDefault(
            HttpServletRequest request,
            String headerName,
            String defaultValue
    ) {
        String value = request.getHeader(headerName);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }

    private List<String> parseRoles(String rolesHeader) {
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .toList();
    }
}
