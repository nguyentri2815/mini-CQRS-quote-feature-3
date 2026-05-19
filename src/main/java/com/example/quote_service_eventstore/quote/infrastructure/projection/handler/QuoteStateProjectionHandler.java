package com.example.quote_service_eventstore.quote.infrastructure.projection.handler;

import com.example.quote_service_eventstore.common.exception.NotFoundException;
import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.infrastructure.projection.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.quote.infrastructure.projection.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.quote.model.QuoteStatus;
import org.springframework.stereotype.Component;

@Component
public class QuoteStateProjectionHandler {

    private final QuoteStateRepository quoteStateRepository;

    public QuoteStateProjectionHandler(QuoteStateRepository quoteStateRepository) {
        this.quoteStateRepository = quoteStateRepository;
    }

    public void project(DomainEvent event) {
        if (event instanceof QuoteCreatedEvent quoteCreatedEvent) {
            onQuoteCreated(quoteCreatedEvent);
            return;
        }

        if (event instanceof QuoteSubmittedEvent quoteSubmittedEvent) {
            onQuoteSubmitted(quoteSubmittedEvent);
            return;
        }

        if (event instanceof QuoteApprovedEvent quoteApprovedEvent) {
            onQuoteApproved(quoteApprovedEvent);
            return;
        }

        throw new IllegalArgumentException("Unsupported event: " + event.eventName());
    }

    private void onQuoteCreated(QuoteCreatedEvent event) {
        if (quoteStateRepository.existsById(event.getQuoteId())) {
            return;
        }

        QuoteStateEntity entity = new QuoteStateEntity(
                event.getQuoteId(),
                event.getCustomerName(),
                event.getProductCode(),
                event.getPremium(),
                QuoteStatus.DRAFT,
                event.occurredAt(),
                event.occurredAt()
        );

        quoteStateRepository.save(entity);
    }

    private void onQuoteSubmitted(QuoteSubmittedEvent event) {
        QuoteStateEntity entity = findQuoteStateOrThrow(event.getQuoteId());

        entity.setStatus(QuoteStatus.SUBMITTED);
        entity.setUpdatedAt(event.occurredAt());

        quoteStateRepository.save(entity);
    }

    private void onQuoteApproved(QuoteApprovedEvent event) {
        QuoteStateEntity entity = findQuoteStateOrThrow(event.getQuoteId());

        entity.setStatus(QuoteStatus.APPROVED);
        entity.setUpdatedAt(event.occurredAt());

        quoteStateRepository.save(entity);
    }

    private QuoteStateEntity findQuoteStateOrThrow(String quoteId) {
        return quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException(
                        "Quote state not found: " + quoteId
                ));
    }
}
