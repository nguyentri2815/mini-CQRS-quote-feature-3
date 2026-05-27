package com.example.quote_service_eventstore.query.quote.dto;

import java.math.BigDecimal;
import java.util.List;

public class QuoteDetailResponse {

    private String id;
    private String customerName;
    private String productCode;
    private BigDecimal premium;
    private String status;
    private List<String> availableActions;

    public QuoteDetailResponse(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status,
            List<String> availableActions
    ) {
        this.id = id;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.status = status;
        this.availableActions = availableActions;
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

    public List<String> getAvailableActions() {
        return availableActions;
    }
}
