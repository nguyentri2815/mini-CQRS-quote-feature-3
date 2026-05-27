package com.example.quote_service_eventstore.domain.quote.command;

public class SubmitQuoteCommand {

    private final String quoteId;
    private final String submittedBy;
    private final String submittedByName;
    private final String tenantId;
    private final String organizationId;

    public SubmitQuoteCommand(
            String quoteId,
            String submittedBy,
            String submittedByName,
            String tenantId,
            String organizationId
    ) {
        this.quoteId = quoteId;
        this.submittedBy = submittedBy;
        this.submittedByName = submittedByName;
        this.tenantId = tenantId;
        this.organizationId = organizationId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
