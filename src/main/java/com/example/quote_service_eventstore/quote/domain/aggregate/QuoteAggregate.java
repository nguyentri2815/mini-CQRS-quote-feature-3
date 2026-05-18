package com.example.quote_service_eventstore.quote.domain.aggregate;

import com.example.quote_service_eventstore.common.exception.BusinessException;
import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.model.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class QuoteAggregate {

    private String id;
    private String customerName;
    private String productCode;
    private BigDecimal premium;
    private QuoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuoteAggregate empty() {
        return new QuoteAggregate();
    }

    public static QuoteAggregate restore(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            QuoteStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        QuoteAggregate aggregate = new QuoteAggregate();
        aggregate.id = id;
        aggregate.customerName = customerName;
        aggregate.productCode = productCode;
        aggregate.premium = premium;
        aggregate.status = status;
        aggregate.createdAt = createdAt;
        aggregate.updatedAt = updatedAt;

        return aggregate;
    }

    public QuoteCreatedEvent create(CreateQuoteCommand command) {
        return new QuoteCreatedEvent(
                UUID.randomUUID().toString(),
                command.getCustomerName(),
                command.getProductCode(),
                command.getPremium(),
                command.getCreatedBy(),
                LocalDateTime.now()
        );
    }

    public QuoteSubmittedEvent submit(SubmitQuoteCommand command) {
        if (this.status != QuoteStatus.DRAFT) {
            throw new BusinessException(
                    "Only DRAFT quote can be submitted. Current status: " + this.status
            );
        }

        return new QuoteSubmittedEvent(
                this.id,
                command.getSubmittedBy(),
                LocalDateTime.now()
        );
    }

    public QuoteApprovedEvent approve(ApproveQuoteCommand command) {
        if (this.status != QuoteStatus.SUBMITTED) {
            throw new BusinessException(
                    "Only SUBMITTED quote can be approved. Current status: " + this.status
            );
        }

        return new QuoteApprovedEvent(
                this.id,
                command.getApprovedBy(),
                LocalDateTime.now()
        );
    }

    public void apply(QuoteCreatedEvent event) {
        this.id = event.getQuoteId();
        this.customerName = event.getCustomerName();
        this.productCode = event.getProductCode();
        this.premium = event.getPremium();
        this.status = QuoteStatus.DRAFT;
        this.createdAt = event.occurredAt();
        this.updatedAt = event.occurredAt();
    }

    public void apply(QuoteSubmittedEvent event) {
        this.status = QuoteStatus.SUBMITTED;
        this.updatedAt = event.occurredAt();
    }

    public void apply(QuoteApprovedEvent event) {
        this.status = QuoteStatus.APPROVED;
        this.updatedAt = event.occurredAt();
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

