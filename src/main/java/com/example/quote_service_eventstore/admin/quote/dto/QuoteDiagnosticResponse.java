package com.example.quote_service_eventstore.admin.quote.dto;

import java.util.List;

public class QuoteDiagnosticResponse {

    private final String quoteId;
    private final List<EventTimelineItem> eventTimeline;
    private final List<OutboxDebugItem> outboxEvents;
    private final List<ProcessedMessageDebugItem> processedMessages;
    private final QuoteStateDebugResponse quoteState;
    private final EsDocumentDebugResponse esDocument;
    private final String diagnosis;

    public QuoteDiagnosticResponse(
            String quoteId,
            List<EventTimelineItem> eventTimeline,
            List<OutboxDebugItem> outboxEvents,
            List<ProcessedMessageDebugItem> processedMessages,
            QuoteStateDebugResponse quoteState,
            EsDocumentDebugResponse esDocument,
            String diagnosis
    ) {
        this.quoteId = quoteId;
        this.eventTimeline = eventTimeline;
        this.outboxEvents = outboxEvents;
        this.processedMessages = processedMessages;
        this.quoteState = quoteState;
        this.esDocument = esDocument;
        this.diagnosis = diagnosis;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public List<EventTimelineItem> getEventTimeline() {
        return eventTimeline;
    }

    public List<OutboxDebugItem> getOutboxEvents() {
        return outboxEvents;
    }

    public List<ProcessedMessageDebugItem> getProcessedMessages() {
        return processedMessages;
    }

    public QuoteStateDebugResponse getQuoteState() {
        return quoteState;
    }

    public EsDocumentDebugResponse getEsDocument() {
        return esDocument;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
}
