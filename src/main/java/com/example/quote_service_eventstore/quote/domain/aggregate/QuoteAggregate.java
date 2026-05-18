package com.example.quote_service_eventstore.quote.domain.aggregate;

import com.example.quote_service_eventstore.common.exception.BusinessException;
import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
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

    public static QuoteAggregate create(CreateQuoteCommand command) {
        LocalDateTime now = LocalDateTime.now();

        QuoteAggregate aggregate = new QuoteAggregate();
        aggregate.id = UUID.randomUUID().toString();
        aggregate.customerName = command.getCustomerName();
        aggregate.productCode = command.getProductCode();
        aggregate.premium = command.getPremium();
        aggregate.status = QuoteStatus.DRAFT;
        aggregate.createdAt = now;
        aggregate.updatedAt = now;

        return aggregate;
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

    public void submit(SubmitQuoteCommand command) {
        if (this.status != QuoteStatus.DRAFT) {
            throw new BusinessException(
                    "Only DRAFT quote can be submitted. Current status: " + this.status
            );
        }

        this.status = QuoteStatus.SUBMITTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve(ApproveQuoteCommand command) {
        if (this.status != QuoteStatus.SUBMITTED) {
            throw new BusinessException(
                    "Only SUBMITTED quote can be approved. Current status: " + this.status
            );
        }

        this.status = QuoteStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
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
