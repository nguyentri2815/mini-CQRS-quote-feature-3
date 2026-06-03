package com.example.quote_service_eventstore.admin.quote.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteStateDebugResponse {

    private final String id;
    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;
    private final String status;
    private final long lastProjectedVersion;
    private final String tenantId;
    private final String organizationId;
    private final String createdBy;
    private final String submittedBy;
    private final String approvedBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QuoteStateDebugResponse(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status,
            long lastProjectedVersion,
            String tenantId,
            String organizationId,
            String createdBy,
            String submittedBy,
            String approvedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.status = status;
        this.lastProjectedVersion = lastProjectedVersion;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
        this.createdBy = createdBy;
        this.submittedBy = submittedBy;
        this.approvedBy = approvedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public String getStatus() {
        return status;
    }

    public long getLastProjectedVersion() {
        return lastProjectedVersion;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
