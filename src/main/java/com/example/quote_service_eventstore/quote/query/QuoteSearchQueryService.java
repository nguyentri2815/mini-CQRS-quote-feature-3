package com.example.quote_service_eventstore.quote.query;

import com.example.quote_service_eventstore.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.quote.infrastructure.search.document.QuoteDocument;
import com.example.quote_service_eventstore.quote.infrastructure.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.quote.infrastructure.search.repository.QuoteSearchRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class QuoteSearchQueryService {

    private final QuoteSearchRepository quoteSearchRepository;
    private final QuoteSearchMapper quoteSearchMapper;

    public QuoteSearchQueryService(
            QuoteSearchRepository quoteSearchRepository,
            QuoteSearchMapper quoteSearchMapper
    ) {
        this.quoteSearchRepository = quoteSearchRepository;
        this.quoteSearchMapper = quoteSearchMapper;
    }

    public List<QuoteListItemResponse> list(
            String keyword,
            String status,
            String productCode
    ) {
        return StreamSupport.stream(quoteSearchRepository.findAll().spliterator(), false)
                .filter(document -> matchKeyword(document, keyword))
                .filter(document -> matchStatus(document, status))
                .filter(document -> matchProductCode(document, productCode))
                .sorted(Comparator.comparing(QuoteDocument::getCreatedAt).reversed())
                .map(quoteSearchMapper::toListItemResponse)
                .toList();
    }

    private boolean matchKeyword(QuoteDocument document, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase();

        return document.getCustomerName().toLowerCase().contains(lowerKeyword)
                || document.getProductCode().toLowerCase().contains(lowerKeyword)
                || document.getId().toLowerCase().contains(lowerKeyword);
    }

    private boolean matchStatus(QuoteDocument document, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }

        return document.getStatus().equalsIgnoreCase(status);
    }

    private boolean matchProductCode(QuoteDocument document, String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return true;
        }

        return document.getProductCode().equalsIgnoreCase(productCode);
    }
}
