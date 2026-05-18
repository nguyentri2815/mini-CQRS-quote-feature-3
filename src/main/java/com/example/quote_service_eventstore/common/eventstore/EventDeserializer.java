package com.example.quote_service_eventstore.common.eventstore;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class EventDeserializer {

    private final ObjectMapper objectMapper;

    public EventDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DomainEvent deserialize(EventStoreRecord record) {
        try {
            return switch (record.getEventType()) {
                case "QuoteCreatedEvent" ->
                        objectMapper.readValue(record.getPayload(), QuoteCreatedEvent.class);

                case "QuoteSubmittedEvent" ->
                        objectMapper.readValue(record.getPayload(), QuoteSubmittedEvent.class);

                case "QuoteApprovedEvent" ->
                        objectMapper.readValue(record.getPayload(), QuoteApprovedEvent.class);

                default ->
                        throw new IllegalArgumentException(
                                "Unsupported event type: " + record.getEventType()
                        );
            };
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Failed to deserialize event. eventType=" + record.getEventType(),
                    exception
            );
        }
    }
}
