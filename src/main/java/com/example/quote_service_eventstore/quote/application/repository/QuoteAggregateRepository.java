package com.example.quote_service_eventstore.quote.application.repository;

import com.example.quote_service_eventstore.common.eventsource.AggregateCommandResult;
import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.domain.aggregate.QuoteAggregate;

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
