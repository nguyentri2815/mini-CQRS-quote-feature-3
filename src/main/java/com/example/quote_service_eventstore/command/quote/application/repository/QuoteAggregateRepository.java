package com.example.quote_service_eventstore.command.quote.application.repository;

import com.example.quote_service_eventstore.shared.eventsource.AggregateCommandResult;
import com.example.quote_service_eventstore.domain.quote.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.aggregate.QuoteAggregate;

public interface QuoteAggregateRepository {

    AggregateCommandResult<QuoteAggregate> create(CreateQuoteCommand command);

    AggregateCommandResult<QuoteAggregate> update(
            String quoteId,
            SubmitQuoteCommand command
    );

    AggregateCommandResult<QuoteAggregate> update(
            String quoteId,
            ApproveQuoteCommand command
    );
}
