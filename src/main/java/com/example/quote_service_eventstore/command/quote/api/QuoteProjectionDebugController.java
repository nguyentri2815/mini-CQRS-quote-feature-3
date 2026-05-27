package com.example.quote_service_eventstore.command.quote.api;

import com.example.quote_service_eventstore.flow.quote.projection.QuoteProjectionRebuildService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug/projections")
public class QuoteProjectionDebugController {

    private final QuoteProjectionRebuildService quoteProjectionRebuildService;

    public QuoteProjectionDebugController(
            QuoteProjectionRebuildService quoteProjectionRebuildService
    ) {
        this.quoteProjectionRebuildService = quoteProjectionRebuildService;
    }

    @PostMapping("/quote-state/rebuild")
    public String rebuildQuoteState() {
        quoteProjectionRebuildService.rebuildAll();
        return "quote_state projection rebuilt";
    }
}
