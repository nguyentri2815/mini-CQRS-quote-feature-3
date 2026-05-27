package com.example.quote_service_eventstore.domain.quote.command;

import java.math.BigDecimal;

public class CreateQuoteCommand {

    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;
    private final String createdBy;

    public CreateQuoteCommand(
            String customerName,
            String productCode,
            BigDecimal premium,
            String createdBy
    ) {
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.createdBy = createdBy;
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

    public String getCreatedBy() {
        return createdBy;
    }
}
