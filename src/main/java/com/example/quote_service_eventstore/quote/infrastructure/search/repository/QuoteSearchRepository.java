package com.example.quote_service_eventstore.quote.infrastructure.search.repository;

import com.example.quote_service_eventstore.quote.infrastructure.search.document.QuoteDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface QuoteSearchRepository extends ElasticsearchRepository<QuoteDocument, String> {

    List<QuoteDocument> findByStatus(String status);

    List<QuoteDocument> findByProductCode(String productCode);

    List<QuoteDocument> findByCustomerNameContaining(String keyword);
}
