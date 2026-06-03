package com.example.quote_service_eventstore.admin.quote.dto;

import java.math.BigDecimal;

public class EsDocumentDebugResponse {

    private final boolean exists;
    private final String indexAlias;
    private final String id;
    private final String customerName;
    private final String productCode;
    private final BigDecimal premium;
    private final String status;

    private EsDocumentDebugResponse(
            boolean exists,
            String indexAlias,
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status
    ) {
        this.exists = exists;
        this.indexAlias = indexAlias;
        this.id = id;
        this.customerName = customerName;
        this.productCode = productCode;
        this.premium = premium;
        this.status = status;
    }

    public static EsDocumentDebugResponse missing(String indexAlias) {
        return new EsDocumentDebugResponse(
                false,
                indexAlias,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static EsDocumentDebugResponse found(
            String indexAlias,
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status
    ) {
        return new EsDocumentDebugResponse(
                true,
                indexAlias,
                id,
                customerName,
                productCode,
                premium,
                status
        );
    }

    public boolean isExists() {
        return exists;
    }

    public String getIndexAlias() {
        return indexAlias;
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
