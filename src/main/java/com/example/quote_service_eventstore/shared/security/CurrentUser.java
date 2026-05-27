package com.example.quote_service_eventstore.shared.security;

import java.util.List;

public class CurrentUser {

    private final String userId;
    private final String username;
    private final String tenantId;
    private final String organizationId;
    private final List<String> roles;

    public CurrentUser(
            String userId,
            String username,
            String tenantId,
            String organizationId,
            List<String> roles
    ) {
        this.userId = userId;
        this.username = username;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
