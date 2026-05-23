package com.example.quote_service_eventstore.quote.infrastructure.search.service;

import com.example.quote_service_eventstore.common.exception.NotFoundException;
import com.example.quote_service_eventstore.quote.infrastructure.projection.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.quote.infrastructure.projection.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.quote.infrastructure.search.document.QuoteDocument;
import com.example.quote_service_eventstore.quote.infrastructure.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.quote.infrastructure.search.repository.QuoteSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteIndexService {

    private static final Logger log = LoggerFactory.getLogger(QuoteIndexService.class);

    private final QuoteStateRepository quoteStateRepository;
    private final QuoteSearchRepository quoteSearchRepository;
    private final QuoteSearchMapper quoteSearchMapper;

    public QuoteIndexService(
            QuoteStateRepository quoteStateRepository,
            QuoteSearchRepository quoteSearchRepository,
            QuoteSearchMapper quoteSearchMapper
    ) {
        this.quoteStateRepository = quoteStateRepository;
        this.quoteSearchRepository = quoteSearchRepository;
        this.quoteSearchMapper = quoteSearchMapper;
    }

    public void syncQuote(String quoteId) {
        QuoteStateEntity entity = quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Quote state not found for indexing: " + quoteId));

        QuoteDocument document = quoteSearchMapper.toDocument(entity);

        quoteSearchRepository.save(document);

        log.info(
                "[SYNC_ES] Synced quote to Elasticsearch. quoteId={}, status={}",
                document.getId(),
                document.getStatus()
        );
    }

    public void deleteQuote(String quoteId) {
        quoteSearchRepository.deleteById(quoteId);

        log.info("[SYNC_ES] Deleted quote from Elasticsearch. quoteId={}", quoteId);
    }
}
