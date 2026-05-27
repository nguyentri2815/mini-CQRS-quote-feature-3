package com.example.quote_service_eventstore.command.quote.application.mapper;

import com.example.quote_service_eventstore.domain.quote.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.command.quote.api.dto.QuoteCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class QuoteCommandMapper {

    public CreateQuoteCommand toCreateCommand(
            QuoteCreateRequest request,
            String currentUser
    ) {
        return new CreateQuoteCommand(
                request.getCustomerName(),
                request.getProductCode(),
                request.getPremium(),
                currentUser
        );
    }

    public SubmitQuoteCommand toSubmitCommand(
            String quoteId,
            String currentUser
    ) {
        return new SubmitQuoteCommand(
                quoteId,
                currentUser
        );
    }

    public ApproveQuoteCommand toApproveCommand(
            String quoteId,
            String currentUser
    ) {
        return new ApproveQuoteCommand(
                quoteId,
                currentUser
        );
    }
}
