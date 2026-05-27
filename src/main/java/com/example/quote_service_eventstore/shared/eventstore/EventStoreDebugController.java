package com.example.quote_service_eventstore.shared.eventstore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug/events")
public class EventStoreDebugController {

    private final EventStore eventStore;

    public EventStoreDebugController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @GetMapping
    public List<EventStoreRecord> findAll() {
        return eventStore.findAll();
    }

    @GetMapping("/{aggregateId}")
    public List<EventStoreRecord> findByAggregateId(@PathVariable String aggregateId) {
        return eventStore.findByAggregateId(aggregateId);
    }
}
