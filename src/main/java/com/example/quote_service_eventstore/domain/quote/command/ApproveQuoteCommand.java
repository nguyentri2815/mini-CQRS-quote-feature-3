package com.example.quote_service_eventstore.domain.quote.command;

public class ApproveQuoteCommand {

    private final String quoteId;
    private final String approvedBy;

    public ApproveQuoteCommand(
            String quoteId,
            String approvedBy
    ) {
        this.quoteId = quoteId;
        this.approvedBy = approvedBy;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }
}
