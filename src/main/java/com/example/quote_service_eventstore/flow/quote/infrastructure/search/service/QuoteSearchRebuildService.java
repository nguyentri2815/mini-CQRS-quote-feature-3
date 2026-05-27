package com.example.quote_service_eventstore.flow.quote.infrastructure.search.service;

import com.example.quote_service_eventstore.readmodel.quote.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.flow.quote.infrastructure.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.flow.quote.infrastructure.search.repository.QuoteSearchRepository;
import org.springframework.stereotype.Service;

@Service
public class QuoteSearchRebuildService {

    private final QuoteStateRepository quoteStateRepository;
    private final QuoteSearchRepository quoteSearchRepository;
    private final QuoteSearchMapper quoteSearchMapper;

    public QuoteSearchRebuildService(
            QuoteStateRepository quoteStateRepository,
            QuoteSearchRepository quoteSearchRepository,
            QuoteSearchMapper quoteSearchMapper
    ) {
        this.quoteStateRepository = quoteStateRepository;
        this.quoteSearchRepository = quoteSearchRepository;
        this.quoteSearchMapper = quoteSearchMapper;
    }

    public void rebuildAll() {
        quoteSearchRepository.deleteAll();

        for (QuoteStateEntity entity : quoteStateRepository.findAll()) {
            quoteSearchRepository.save(
                    quoteSearchMapper.toDocument(entity)
            );
        }
    }

    public void rebuildOne(String quoteId) {
        quoteSearchRepository.deleteById(quoteId);

        QuoteStateEntity entity = quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote state not found: " + quoteId));

        quoteSearchRepository.save(
                quoteSearchMapper.toDocument(entity)
        );
    }

}
