package com.example.quote_service_eventstore.quote.infrastructure.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteIndexService {

    private static final Logger log = LoggerFactory.getLogger(QuoteIndexService.class);

    public void syncQuote(String quoteId) {
        log.info("[SYNC_ES] Syncing quote to Elasticsearch. quoteId={}", quoteId);

        // Ngày 12 chỉ mô phỏng.
        // Ngày sau mới tạo QuoteDocument + ElasticsearchRepository.
    }
}
