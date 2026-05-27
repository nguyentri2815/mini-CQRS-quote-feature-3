package com.example.quote_service_eventstore.domain.quote.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Quote {

    private String id;

    private String customerName;

    private String productCode;

    private BigDecimal premium;

    private QuoteStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Quote(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            QuoteStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
