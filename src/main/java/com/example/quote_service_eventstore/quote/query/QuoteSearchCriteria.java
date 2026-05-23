package com.example.quote_service_eventstore.quote.query;

public class QuoteSearchCriteria {

    private final String keyword;
    private final String status;
    private final String productCode;
    private final int page;
    private final int size;

    public QuoteSearchCriteria(
            String keyword,
            String status,
            String productCode,
            int page,
            int size
    ) {
        this.keyword = keyword;
        this.status = status;
        this.productCode = productCode;
        this.page = page;
        this.size = size;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getStatus() {
        return status;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
