package com.example.quote_service_eventstore.domain.quote.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuoteCreatedEvent implements DomainEvent {

    private String quoteId;
    private String customerName;
    private String productCode;
    private BigDecimal premium;
    private String createdBy;
    private LocalDateTime occurredAt;

    public QuoteCreatedEvent() {
    }

    public QuoteCreatedEvent(
            String quoteId,
            String customerName,
            String productCode,
            BigDecimal premium,
            String createdBy,
            LocalDateTime occurredAt
    ) {
        this.quoteId = quoteId;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.createdBy = createdBy;
        this.occurredAt = occurredAt;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    @Override
    public String eventName() {
        return "QuoteCreatedEvent";
    }

    @Override
    public String aggregateId() {
        return quoteId;
    }

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}

