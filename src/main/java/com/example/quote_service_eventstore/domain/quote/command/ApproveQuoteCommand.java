package com.example.quote_service_eventstore.domain.quote.command;

public class ApproveQuoteCommand {

    private final String quoteId;
    private final String approvedBy;
    private final String approvedByName;
    private final String tenantId;
    private final String organizationId;

    public ApproveQuoteCommand(
            String quoteId,
            String approvedBy,
            String approvedByName,
            String tenantId,
            String organizationId
    ) {
        this.quoteId = quoteId;
        this.approvedBy = approvedBy;
        this.approvedByName = approvedByName;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
