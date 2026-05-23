package com.example.quote_service_eventstore.quote.api;

import com.example.quote_service_eventstore.quote.infrastructure.rebuild.QuoteReadModelRebuildService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug/read-models")
public class QuoteReadModelDebugController {

    private final QuoteReadModelRebuildService quoteReadModelRebuildService;

    public QuoteReadModelDebugController(
            QuoteReadModelRebuildService quoteReadModelRebuildService
    ) {
        this.quoteReadModelRebuildService = quoteReadModelRebuildService;
    }

    @PostMapping("/quotes/rebuild-all")
    public String rebuildAllQuoteReadModels() {
        quoteReadModelRebuildService.rebuildAllReadModels();

        return "quote read models rebuilt: event_store -> quote_state -> quote_index";
    }

    @PostMapping("/quotes/{quoteId}/rebuild")
    public String rebuildOneQuote(@PathVariable String quoteId) {
        quoteReadModelRebuildService.rebuildOneQuote(quoteId);

        return "quote read model rebuilt: " + quoteId;
    }

}
