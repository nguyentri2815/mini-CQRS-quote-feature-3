package com.example.quote_service_eventstore.quote.infrastructure.search.service;

import com.example.quote_service_eventstore.quote.infrastructure.projection.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.quote.infrastructure.projection.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.quote.infrastructure.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.quote.infrastructure.search.repository.QuoteSearchRepository;
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
}
