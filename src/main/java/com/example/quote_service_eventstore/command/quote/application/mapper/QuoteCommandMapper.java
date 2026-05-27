package com.example.quote_service_eventstore.command.quote.application.mapper;

import com.example.quote_service_eventstore.domain.quote.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.command.quote.api.dto.QuoteCreateRequest;
import com.example.quote_service_eventstore.shared.security.CurrentUser;
import org.springframework.stereotype.Component;

@Component
public class QuoteCommandMapper {

    public CreateQuoteCommand toCreateCommand(
            QuoteCreateRequest request,
            CurrentUser currentUser
    ) {
        return new CreateQuoteCommand(
                request.getCustomerName(),
                request.getProductCode(),
                request.getPremium(),
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getTenantId(),
                currentUser.getOrganizationId()
        );
    }

    public SubmitQuoteCommand toSubmitCommand(
            String quoteId,
            CurrentUser currentUser
    ) {
        return new SubmitQuoteCommand(
                quoteId,
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getTenantId(),
                currentUser.getOrganizationId()
        );
    }

    public ApproveQuoteCommand toApproveCommand(
            String quoteId,
            CurrentUser currentUser
    ) {
        return new ApproveQuoteCommand(
                quoteId,
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getTenantId(),
                currentUser.getOrganizationId()
        );
    }
}
