package com.example.quote_service_eventstore.common.eventbus;

import com.example.quote_service_eventstore.quote.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryDomainEventPublisher.class);

    private final List<DomainEventHandler<? extends DomainEvent>> handlers;

    public InMemoryDomainEventPublisher(
            List<DomainEventHandler<? extends DomainEvent>> handlers
    ) {
        this.handlers = handlers;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info(
                "[EVENT_PUBLISHER] Publishing event. eventName={}, aggregateId={}",
                event.eventName(),
                event.aggregateId()
        );

        for (DomainEventHandler<? extends DomainEvent> handler : handlers) {
            if (handler.eventType().equals(event.getClass())) {
                dispatch(handler, event);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> void dispatch(
            DomainEventHandler<? extends DomainEvent> handler,
            DomainEvent event
    ) {
        DomainEventHandler<T> typedHandler = (DomainEventHandler<T>) handler;
        typedHandler.handle((T) event);
    }
}
