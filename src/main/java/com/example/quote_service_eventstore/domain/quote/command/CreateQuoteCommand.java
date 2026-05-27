package com.example.quote_service_eventstore.domain.quote.command;

import java.math.BigDecimal;

public class CreateQuoteCommand {

    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;

    private final String createdBy;
    private final String createdByName;
    private final String tenantId;
    private final String organizationId;

    public CreateQuoteCommand(
            String customerName,
            String productCode,
            BigDecimal premium,
            String createdBy,
            String createdByName,
            String tenantId,
            String organizationId
    ) {
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
