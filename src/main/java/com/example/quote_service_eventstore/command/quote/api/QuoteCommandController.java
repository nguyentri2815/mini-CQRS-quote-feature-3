package com.example.quote_service_eventstore.command.quote.api;

import com.example.quote_service_eventstore.domain.quote.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.command.quote.application.mapper.QuoteCommandMapper;
import com.example.quote_service_eventstore.command.quote.api.dto.QuoteCreateRequest;
import com.example.quote_service_eventstore.command.quote.api.dto.QuoteCommandResponse;
import com.example.quote_service_eventstore.command.quote.application.service.QuoteCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuoteCommandController {

    private final QuoteCommandService quoteCommandService;
    private final QuoteCommandMapper quoteCommandMapper;

    public QuoteCommandController(
            QuoteCommandService quoteCommandService,
            QuoteCommandMapper quoteCommandMapper
    ) {
        this.quoteCommandService = quoteCommandService;
        this.quoteCommandMapper = quoteCommandMapper;
    }

    @PostMapping
    public QuoteCommandResponse create(@Valid @RequestBody QuoteCreateRequest request) {
        String currentUser = "demo-user";

        CreateQuoteCommand command = quoteCommandMapper.toCreateCommand(
                request,
                currentUser
        );

        return quoteCommandService.create(command);
    }

    @PostMapping("/{id}/submit")
    public QuoteCommandResponse submit(@PathVariable String id) {
        String currentUser = "demo-user";

        SubmitQuoteCommand command = quoteCommandMapper.toSubmitCommand(
                id,
                currentUser
        );

        return quoteCommandService.submit(command);
    }

    @PostMapping("/{id}/approve")
    public QuoteCommandResponse approve(@PathVariable String id) {
        String currentUser = "demo-user";

        ApproveQuoteCommand command = quoteCommandMapper.toApproveCommand(
                id,
                currentUser
        );

        return quoteCommandService.approve(command);
    }

}


