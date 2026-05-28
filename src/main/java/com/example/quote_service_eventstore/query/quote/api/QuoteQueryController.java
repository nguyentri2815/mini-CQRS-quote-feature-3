package com.example.quote_service_eventstore.query.quote.api;

import com.example.quote_service_eventstore.query.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.query.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.query.quote.application.QuoteDetailQueryService;
import com.example.quote_service_eventstore.query.quote.application.QuoteSearchCriteria;
import com.example.quote_service_eventstore.query.quote.application.QuoteSearchQueryService;
import com.example.quote_service_eventstore.shared.dto.PageResult;
import com.example.quote_service_eventstore.shared.security.CurrentUser;
import com.example.quote_service_eventstore.shared.security.CurrentUserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteQueryController {

    private final QuoteDetailQueryService quoteDetailQueryService;
    private final QuoteSearchQueryService quoteSearchQueryService;
    private final CurrentUserContext currentUserContext;

    public QuoteQueryController(
            QuoteDetailQueryService quoteDetailQueryService,
            QuoteSearchQueryService quoteSearchQueryService,
            CurrentUserContext currentUserContext
    ) {
        this.quoteDetailQueryService = quoteDetailQueryService;
        this.quoteSearchQueryService = quoteSearchQueryService;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping("/{id}")
    public QuoteDetailResponse detail(@PathVariable String id) {
        CurrentUser currentUser = currentUserContext.getCurrentUser();
        return quoteDetailQueryService.detail(id, currentUser);
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