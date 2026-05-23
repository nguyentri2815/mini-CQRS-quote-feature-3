package com.example.quote_service_eventstore.quote.infrastructure.rebuild;

import com.example.quote_service_eventstore.quote.infrastructure.projection.QuoteProjectionRebuildService;
import com.example.quote_service_eventstore.quote.infrastructure.search.service.QuoteSearchRebuildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteReadModelRebuildService {

    private static final Logger log = LoggerFactory.getLogger(QuoteReadModelRebuildService.class);

    private final QuoteProjectionRebuildService quoteProjectionRebuildService;
    private final QuoteSearchRebuildService quoteSearchRebuildService;

    public QuoteReadModelRebuildService(
            QuoteProjectionRebuildService quoteProjectionRebuildService,
            QuoteSearchRebuildService quoteSearchRebuildService
    ) {
        this.quoteProjectionRebuildService = quoteProjectionRebuildService;
        this.quoteSearchRebuildService = quoteSearchRebuildService;
    }

    public void rebuildAllReadModels() {
        log.info("[REBUILD] Start rebuilding quote_state from event_store");

        quoteProjectionRebuildService.rebuildAll();

        log.info("[REBUILD] Finished rebuilding quote_state");

        log.info("[REBUILD] Start rebuilding quote_index from quote_state");

        quoteSearchRebuildService.rebuildAll();

        log.info("[REBUILD] Finished rebuilding quote_index");
    }

    public void rebuildOneQuote(String quoteId) {
        log.info("[REBUILD] Start rebuilding quote read models. quoteId={}", quoteId);

        quoteProjectionRebuildService.rebuildOne(quoteId);
        quoteSearchRebuildService.rebuildOne(quoteId);

        log.info("[REBUILD] Finished rebuilding quote read models. quoteId={}", quoteId);
    }

}
