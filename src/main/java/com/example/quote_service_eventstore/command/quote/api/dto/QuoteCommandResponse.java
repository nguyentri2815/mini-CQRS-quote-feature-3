package com.example.quote_service_eventstore.command.quote.api.dto;

public class QuoteCommandResponse {

    private String id;
    private String status;

    public QuoteCommandResponse(String id, String status) {
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
