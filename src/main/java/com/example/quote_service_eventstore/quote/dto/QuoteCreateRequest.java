package com.example.quote_service_eventstore.quote.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class QuoteCreateRequest {

    @NotBlank(message = "customerName is required")
    private String customerName;

    @NotBlank(message = "productCode is required")
    private String productCode;

    @NotNull(message = "premium is required")
    @DecimalMin(value = "0.0", message = "premium must be greater than or equal to 0")
    private BigDecimal premium;

    public String getCustomerName() {
        return customerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }
}
