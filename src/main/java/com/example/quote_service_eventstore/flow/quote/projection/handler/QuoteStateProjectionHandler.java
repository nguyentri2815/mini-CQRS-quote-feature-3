package com.example.quote_service_eventstore.flow.quote.projection.handler;

import com.example.quote_service_eventstore.shared.exception.BusinessException;
import com.example.quote_service_eventstore.shared.exception.NotFoundException;
import com.example.quote_service_eventstore.domain.quote.event.DomainEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.domain.quote.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.state.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.domain.quote.model.QuoteStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuoteStateProjectionHandler {

    private static final Logger log = LoggerFactory.getLogger(QuoteStateProjectionHandler.class);

    private final QuoteStateRepository quoteStateRepository;

    public QuoteStateProjectionHandler(QuoteStateRepository quoteStateRepository) {
        this.quoteStateRepository = quoteStateRepository;
    }

    public void project(DomainEvent event, long aggregateVersion) {
        if (event instanceof QuoteCreatedEvent quoteCreatedEvent) {
            onQuoteCreated(quoteCreatedEvent, aggregateVersion);
            return;
        }

        if (event instanceof QuoteSubmittedEvent quoteSubmittedEvent) {
            onQuoteSubmitted(quoteSubmittedEvent, aggregateVersion);
            return;
        }

        if (event instanceof QuoteApprovedEvent quoteApprovedEvent) {
            onQuoteApproved(quoteApprovedEvent, aggregateVersion);
            return;
        }

        throw new IllegalArgumentException("Unsupported event: " + event.eventName());
    }

    private void onQuoteCreated(QuoteCreatedEvent event, long aggregateVersion) {
        if (aggregateVersion != 1) {
            throw new BusinessException(
                    "QuoteCreatedEvent must be version 1. Actual version: " + aggregateVersion
            );
        }

        if (quoteStateRepository.existsById(event.getQuoteId())) {
            log.warn(
                    "[PROJECTION] Duplicate QuoteCreatedEvent skipped. quoteId={}, version={}",
                    event.getQuoteId(),
                    aggregateVersion
            );
            return;
        }

        QuoteStateEntity entity = new QuoteStateEntity(
                event.getQuoteId(),
                event.getCustomerName(),
                event.getProductCode(),
                event.getPremium(),
                QuoteStatus.DRAFT,
                event.occurredAt(),
                event.occurredAt(),
                aggregateVersion
        );

        quoteStateRepository.save(entity);
    }

    private void onQuoteSubmitted(QuoteSubmittedEvent event, long aggregateVersion) {
        QuoteStateEntity entity = findQuoteStateOrThrow(event.getQuoteId());

        if (!canApply(entity, aggregateVersion)) {
            return;
        }

        entity.setStatus(QuoteStatus.SUBMITTED);
        entity.setUpdatedAt(event.occurredAt());
        entity.setLastProjectedVersion(aggregateVersion);

        quoteStateRepository.save(entity);
    }

    private void onQuoteApproved(QuoteApprovedEvent event, long aggregateVersion) {
        QuoteStateEntity entity = findQuoteStateOrThrow(event.getQuoteId());

        if (!canApply(entity, aggregateVersion)) {
            return;
        }

        entity.setStatus(QuoteStatus.APPROVED);
        entity.setUpdatedAt(event.occurredAt());
        entity.setLastProjectedVersion(aggregateVersion);

        quoteStateRepository.save(entity);
    }

    private boolean canApply(QuoteStateEntity entity, long aggregateVersion) {
        long lastProjectedVersion = entity.getLastProjectedVersion();

        if (aggregateVersion <= lastProjectedVersion) {
            log.warn(
                    "[PROJECTION] Duplicate/old event skipped. quoteId={}, eventVersion={}, lastProjectedVersion={}",
                    entity.getId(),
                    aggregateVersion,
                    lastProjectedVersion
            );
            return false;
        }

        if (aggregateVersion != lastProjectedVersion + 1) {
            throw new BusinessException(
                    "Out-of-order event. quoteId=" + entity.getId()
                            + ", eventVersion=" + aggregateVersion
                            + ", lastProjectedVersion=" + lastProjectedVersion
            );
        }

        return true;
    }

    private QuoteStateEntity findQuoteStateOrThrow(String quoteId) {
        return quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException(
                        "Quote state not found: " + quoteId
                ));
    }
}

