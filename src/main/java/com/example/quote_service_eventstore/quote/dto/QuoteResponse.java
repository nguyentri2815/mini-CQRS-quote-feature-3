package com.example.quote_service_eventstore.quote.dto;

public class QuoteResponse {

    private String id;
    private String status;

    public QuoteResponse(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
