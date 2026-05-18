package com.example.quote_service_eventstore.quote.application.mapper;

import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.dto.QuoteCreateRequest;
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
