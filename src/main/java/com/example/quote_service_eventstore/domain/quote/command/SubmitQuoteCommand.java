package com.example.quote_service_eventstore.domain.quote.command;

public class SubmitQuoteCommand {

    private final String quoteId;
    private final String submittedBy;

    public SubmitQuoteCommand(
            String quoteId,
            String submittedBy
    ) {
        this.quoteId = quoteId;
        this.submittedBy = submittedBy;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }
}
