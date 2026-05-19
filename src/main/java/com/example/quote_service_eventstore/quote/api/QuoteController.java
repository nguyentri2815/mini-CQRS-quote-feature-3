package com.example.quote_service_eventstore.quote.api;

import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.application.mapper.QuoteCommandMapper;
import com.example.quote_service_eventstore.quote.dto.QuoteCreateRequest;
import com.example.quote_service_eventstore.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteResponse;
import com.example.quote_service_eventstore.quote.query.QuoteQueryService;
import com.example.quote_service_eventstore.quote.service.QuoteCommandService;
import com.example.quote_service_eventstore.quote.service.QuoteInMemoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteCommandService quoteCommandService;
    private final QuoteCommandMapper quoteCommandMapper;
    private final QuoteQueryService quoteQueryService;

    public QuoteController(
            QuoteCommandService quoteCommandService,
            QuoteCommandMapper quoteCommandMapper,
            QuoteQueryService quoteQueryService
    ) {
        this.quoteCommandService = quoteCommandService;
        this.quoteCommandMapper = quoteCommandMapper;
        this.quoteQueryService = quoteQueryService;
    }

    @PostMapping
    public QuoteResponse create(@Valid @RequestBody QuoteCreateRequest request) {
        String currentUser = "demo-user";

        CreateQuoteCommand command = quoteCommandMapper.toCreateCommand(
                request,
                currentUser
        );

        return quoteCommandService.create(command);
    }

    @PostMapping("/{id}/submit")
    public QuoteResponse submit(@PathVariable String id) {
        String currentUser = "demo-user";

        SubmitQuoteCommand command = quoteCommandMapper.toSubmitCommand(
                id,
                currentUser
        );

        return quoteCommandService.submit(command);
    }

    @PostMapping("/{id}/approve")
    public QuoteResponse approve(@PathVariable String id) {
        String currentUser = "demo-user";

        ApproveQuoteCommand command = quoteCommandMapper.toApproveCommand(
                id,
                currentUser
        );

        return quoteCommandService.approve(command);
    }

    @GetMapping("/{id}")
    public QuoteDetailResponse detail(@PathVariable String id) {
        return quoteQueryService.detail(id);
    }

    @GetMapping
    public List<QuoteListItemResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productCode
    ) {
        return quoteQueryService.list(keyword, status, productCode);
    }
}


