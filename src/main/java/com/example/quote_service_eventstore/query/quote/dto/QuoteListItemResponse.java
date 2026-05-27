package com.example.quote_service_eventstore.query.quote.dto;

import java.math.BigDecimal;

public class QuoteListItemResponse {

    private String id;
    private String customerName;
    private String productCode;
    private BigDecimal premium;
    private String status;

    public QuoteListItemResponse(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status
    ) {
        this.id = id;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}
