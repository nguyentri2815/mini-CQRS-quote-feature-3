package com.example.quote_service_eventstore.quote.api;

import com.example.quote_service_eventstore.quote.dto.QuoteCreateRequest;
import com.example.quote_service_eventstore.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteResponse;
import com.example.quote_service_eventstore.quote.service.QuoteInMemoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteInMemoryService quoteInMemoryService;

    public QuoteController(QuoteInMemoryService quoteInMemoryService) {
        this.quoteInMemoryService = quoteInMemoryService;
    }

    @PostMapping
    public QuoteResponse create(@Valid @RequestBody QuoteCreateRequest request) {
        return quoteInMemoryService.create(request);
    }

    @PostMapping("/{id}/submit")
    public QuoteResponse submit(@PathVariable String id) {
        return quoteInMemoryService.submit(id);
    }

    @PostMapping("/{id}/approve")
    public QuoteResponse approve(@PathVariable String id) {
        return quoteInMemoryService.approve(id);
    }

    @GetMapping("/{id}")
    public QuoteDetailResponse detail(@PathVariable String id) {
        return quoteInMemoryService.detail(id);
    }

    @GetMapping
    public List<QuoteListItemResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productCode
    ) {
        return quoteInMemoryService.list(keyword, status, productCode);
    }
}

