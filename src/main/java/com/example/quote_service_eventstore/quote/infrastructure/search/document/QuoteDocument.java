package com.example.quote_service_eventstore.quote.infrastructure.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "quote_index")
public class QuoteDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String customerName;

    @Field(type = FieldType.Keyword)
    private String productCode;

    @Field(type = FieldType.Double)
    private BigDecimal premium;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;

    public QuoteDocument() {
    }

    public QuoteDocument(
            String id,
            String customerName,
            String productCode,
            BigDecimal premium,
            String status,
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

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
