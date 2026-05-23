package com.example.quote_service_eventstore.quote.api;

import com.example.quote_service_eventstore.quote.infrastructure.search.service.QuoteSearchRebuildService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug/search")
public class QuoteSearchDebugController {

    private final QuoteSearchRebuildService quoteSearchRebuildService;

    public QuoteSearchDebugController(
            QuoteSearchRebuildService quoteSearchRebuildService
    ) {
        this.quoteSearchRebuildService = quoteSearchRebuildService;
    }

    @PostMapping("/quotes/rebuild")
    public String rebuildQuoteIndex() {
        quoteSearchRebuildService.rebuildAll();
        return "quote_index rebuilt";
    }
}
