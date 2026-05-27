package com.example.quote_service_eventstore.query.quote.application;

import com.example.quote_service_eventstore.shared.dto.PageResult;
import com.example.quote_service_eventstore.query.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.flow.quote.infrastructure.search.document.QuoteDocument;
import com.example.quote_service_eventstore.flow.quote.infrastructure.search.mapper.QuoteSearchMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuoteSearchQueryService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final QuoteSearchMapper quoteSearchMapper;

    public QuoteSearchQueryService(
            ElasticsearchOperations elasticsearchOperations,
            QuoteSearchMapper quoteSearchMapper
    ) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.quoteSearchMapper = quoteSearchMapper;
    }

    public PageResult<QuoteListItemResponse> search(QuoteSearchCriteria criteria) {
        int page = normalizePage(criteria.getPage());
        int size = normalizeSize(criteria.getSize());

        NativeQuery query = buildQuery(criteria, page, size);

        SearchHits<QuoteDocument> hits = elasticsearchOperations.search(
                query,
                QuoteDocument.class
        );

        List<QuoteListItemResponse> items = hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(quoteSearchMapper::toListItemResponse)
                .toList();

        long totalElements = hits.getTotalHits();
        int totalPages = calculateTotalPages(totalElements, size);

        return new PageResult<>(
                items,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    private NativeQuery buildQuery(
            QuoteSearchCriteria criteria,
            int page,
            int size
    ) {
        return NativeQuery.builder()
                .withQuery(q -> q.bool(bool -> {
                    if (hasText(criteria.getKeyword())) {
                        bool.must(must -> must.bool(keywordBool -> keywordBool
                                .should(s -> s.match(match -> match
                                        .field("customerName")
                                        .query(criteria.getKeyword())
                                ))
                                .should(s -> s.term(term -> term
                                        .field("productCode")
                                        .value(criteria.getKeyword())
                                ))
                                .should(s -> s.term(term -> term
                                        .field("id")
                                        .value(criteria.getKeyword())
                                ))
                        ));
                    }

                    if (hasText(criteria.getStatus())) {
                        bool.filter(filter -> filter.term(term -> term
                                .field("status")
                                .value(criteria.getStatus())
                        ));
                    }

                    if (hasText(criteria.getProductCode())) {
                        bool.filter(filter -> filter.term(term -> term
                                .field("productCode")
                                .value(criteria.getProductCode())
                        ));
                    }

                    return bool;
                }))
                .withPageable(
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "createdAt")
                        )
                )
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return 10;
        }

        return Math.min(size, 100);
    }

    private int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }

        return (int) Math.ceil((double) totalElements / size);
    }
}
