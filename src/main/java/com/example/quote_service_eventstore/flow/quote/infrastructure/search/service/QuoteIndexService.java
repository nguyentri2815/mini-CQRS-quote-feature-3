package com.example.quote_service_eventstore.flow.quote.infrastructure.search.service;

import com.example.quote_service_eventstore.shared.exception.NotFoundException;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.state.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.readmodel.quote.search.document.QuoteDocument;
import com.example.quote_service_eventstore.readmodel.quote.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.readmodel.quote.search.repository.QuoteSearchRepository;
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
        log.info("[SYNC_ES] Sync started. quoteId={}", quoteId);

        QuoteStateEntity entity = quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Quote state not found for indexing: " + quoteId));

        log.info(
                "[SYNC_ES] Loaded quote_state. quoteId={}, status={}, version={}",
                entity.getId(),
                entity.getStatus(),
                entity.getLastProjectedVersion()
        );

        QuoteDocument document = quoteSearchMapper.toDocument(entity);

        log.info(
                "[SYNC_ES] Saving quote document. quoteId={}, status={}",
                document.getId(),
                document.getStatus()
        );

        quoteSearchRepository.save(document);

        log.info(
                "[SYNC_ES] Sync completed. quoteId={}, status={}",
                document.getId(),
                document.getStatus()
        );
    }

    public void deleteQuote(String quoteId) {
        log.info("[SYNC_ES] Delete started. quoteId={}", quoteId);

        quoteSearchRepository.deleteById(quoteId);

        log.info("[SYNC_ES] Delete completed. quoteId={}", quoteId);
    }
}
