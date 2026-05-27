package com.example.quote_service_eventstore.query.quote.api;

import com.example.quote_service_eventstore.query.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.query.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.query.quote.application.QuoteDetailQueryService;
import com.example.quote_service_eventstore.query.quote.application.QuoteSearchCriteria;
import com.example.quote_service_eventstore.query.quote.application.QuoteSearchQueryService;
import com.example.quote_service_eventstore.shared.dto.PageResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteQueryController {

    private final QuoteDetailQueryService quoteDetailQueryService;
    private final QuoteSearchQueryService quoteSearchQueryService;

    public QuoteQueryController(
            QuoteDetailQueryService quoteDetailQueryService,
            QuoteSearchQueryService quoteSearchQueryService
    ) {
        this.quoteDetailQueryService = quoteDetailQueryService;
        this.quoteSearchQueryService = quoteSearchQueryService;
    }

    @GetMapping("/{id}")
    public QuoteDetailResponse detail(@PathVariable String id) {
        return quoteDetailQueryService.detail(id);
    }

    @GetMapping
    public PageResult<QuoteListItemResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        QuoteSearchCriteria criteria = new QuoteSearchCriteria(
                keyword,
                status,
                productCode,
                page,
                size
        );

        return quoteSearchQueryService.search(criteria);
    }
}