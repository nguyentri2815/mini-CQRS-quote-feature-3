package com.example.quote_service_eventstore.quote.api;

import com.example.quote_service_eventstore.quote.dto.QuoteCreateRequest;
import com.example.quote_service_eventstore.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    @PostMapping
    public QuoteResponse create(@Valid @RequestBody QuoteCreateRequest request) {
        String quoteId = UUID.randomUUID().toString();

        return new QuoteResponse(
                quoteId,
                "DRAFT"
        );
    }

    @PostMapping("/{id}/submit")
    public QuoteResponse submit(@PathVariable String id) {
        return new QuoteResponse(
                id,
                "SUBMITTED"
        );
    }

    @PostMapping("/{id}/approve")
    public QuoteResponse approve(@PathVariable String id) {
        return new QuoteResponse(
                id,
                "APPROVED"
        );
    }

    @GetMapping("/{id}")
    public QuoteDetailResponse detail(@PathVariable String id) {
        return new QuoteDetailResponse(
                id,
                "Nguyen Van A",
                "MOTOR",
                new BigDecimal("1200000"),
                "DRAFT",
                List.of("SUBMIT")
        );
    }

    @GetMapping
    public List<QuoteListItemResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return List.of(
                new QuoteListItemResponse(
                        "quote-001",
                        "Nguyen Van A",
                        "MOTOR",
                        new BigDecimal("1200000"),
                        "DRAFT"
                ),
                new QuoteListItemResponse(
                        "quote-002",
                        "Tran Thi B",
                        "HEALTH",
                        new BigDecimal("2500000"),
                        "SUBMITTED"
                )
        );
    }
}
