package com.example.quote_service_eventstore.admin.quote.api;

import com.example.quote_service_eventstore.admin.quote.application.QuoteDebugService;
import com.example.quote_service_eventstore.admin.quote.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug/quotes")
public class QuoteDebugController {

    private final QuoteDebugService quoteDebugService;

    public QuoteDebugController(QuoteDebugService quoteDebugService) {
        this.quoteDebugService = quoteDebugService;
    }

    @GetMapping("/{quoteId}/event-timeline")
    public List<EventTimelineItem> eventTimeline(@PathVariable String quoteId) {
        return quoteDebugService.eventTimeline(quoteId);
    }

    @GetMapping("/{quoteId}/outbox")
    public List<OutboxDebugItem> outbox(@PathVariable String quoteId) {
        return quoteDebugService.outbox(quoteId);
    }

    @GetMapping("/{quoteId}/processed-messages")
    public List<ProcessedMessageDebugItem> processedMessages(@PathVariable String quoteId) {
        return quoteDebugService.processedMessages(quoteId);
    }

    @GetMapping("/{quoteId}/state")
    public QuoteStateDebugResponse state(@PathVariable String quoteId) {
        return quoteDebugService.quoteState(quoteId);
    }

    @GetMapping("/{quoteId}/es-document")
    public EsDocumentDebugResponse esDocument(@PathVariable String quoteId) {
        return quoteDebugService.esDocument(quoteId);
    }

    @GetMapping("/{quoteId}/diagnostic")
    public QuoteDiagnosticResponse diagnostic(@PathVariable String quoteId) {
        return quoteDebugService.diagnostic(quoteId);
    }
}
