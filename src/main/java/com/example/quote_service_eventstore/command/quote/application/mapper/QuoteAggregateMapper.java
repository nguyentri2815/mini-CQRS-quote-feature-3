package com.example.quote_service_eventstore.command.quote.application.mapper;

import com.example.quote_service_eventstore.domain.quote.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.domain.quote.model.Quote;
import org.springframework.stereotype.Component;

@Component
public class QuoteAggregateMapper {

    public QuoteAggregate toAggregate(Quote quote) {
        return QuoteAggregate.restore(
                quote.getId(),
                quote.getCustomerName(),
                quote.getProductCode(),
                quote.getPremium(),
                quote.getStatus(),
                quote.getCreatedAt(),
                quote.getUpdatedAt()
        );
    }

    public Quote toModel(QuoteAggregate aggregate) {
        return new Quote(
                aggregate.getId(),
                aggregate.getCustomerName(),
                aggregate.getProductCode(),
                aggregate.getPremium(),
                aggregate.getStatus(),
                aggregate.getCreatedAt(),
                aggregate.getUpdatedAt()
        );
    }
}
