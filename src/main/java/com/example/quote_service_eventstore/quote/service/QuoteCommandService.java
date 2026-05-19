package com.example.quote_service_eventstore.quote.service;

import com.example.quote_service_eventstore.common.eventbus.DomainEventPublisher;
import com.example.quote_service_eventstore.common.eventstore.EventStore;
import com.example.quote_service_eventstore.common.exception.BusinessException;
import com.example.quote_service_eventstore.common.exception.NotFoundException;
import com.example.quote_service_eventstore.common.outbox.OutboxEventStore;
import com.example.quote_service_eventstore.quote.application.command.ApproveQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.quote.application.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.quote.application.mapper.QuoteAggregateMapper;
import com.example.quote_service_eventstore.quote.domain.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.quote.domain.event.QuoteApprovedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteCreatedEvent;
import com.example.quote_service_eventstore.quote.domain.event.QuoteSubmittedEvent;
import com.example.quote_service_eventstore.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.quote.dto.QuoteResponse;
import com.example.quote_service_eventstore.quote.infrastructure.projection.handler.QuoteStateProjectionHandler;
import com.example.quote_service_eventstore.quote.model.Quote;
import com.example.quote_service_eventstore.quote.model.QuoteStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuoteCommandService {

    private static final String QUOTE_AGGREGATE_TYPE = "Quote";

    private final EventStore eventStore;
    private final QuoteAggregateLoader quoteAggregateLoader;
//    private final QuoteStateProjectionHandler quoteStateProjectionHandler;
//    private final DomainEventPublisher eventPublisher;
    private final OutboxEventStore outboxEventStore;

    public QuoteCommandService(
            EventStore eventStore,
            QuoteAggregateLoader quoteAggregateLoader,
//            QuoteStateProjectionHandler quoteStateProjectionHandler
//            DomainEventPublisher eventPublisher
            OutboxEventStore outboxEventStore
    ) {
        this.eventStore = eventStore;
        this.quoteAggregateLoader = quoteAggregateLoader;
//        this.quoteStateProjectionHandler = quoteStateProjectionHandler;
//        this.eventPublisher = eventPublisher;
        this.outboxEventStore = outboxEventStore;
    }


    @Transactional
    public QuoteResponse create(CreateQuoteCommand command) {
        QuoteAggregate aggregate = QuoteAggregate.empty();

        QuoteCreatedEvent event = aggregate.create(command);

        eventStore.append(QUOTE_AGGREGATE_TYPE, event);

//        eventPublisher.publish(event);
        outboxEventStore.save(event);

        aggregate.apply(event);

        return new QuoteResponse(
                aggregate.getId(),
                aggregate.getStatus().name()
        );
    }

    @Transactional
    public QuoteResponse submit(SubmitQuoteCommand command) {
        QuoteAggregate aggregate = quoteAggregateLoader.load(command.getQuoteId());

        QuoteSubmittedEvent event = aggregate.submit(command);

        eventStore.append(QUOTE_AGGREGATE_TYPE, event);
        outboxEventStore.save(event);

        aggregate.apply(event);

//        eventPublisher.publish(event);

        return new QuoteResponse(
                aggregate.getId(),
                aggregate.getStatus().name()
        );
    }

    @Transactional
    public QuoteResponse approve(ApproveQuoteCommand command) {
        QuoteAggregate aggregate = quoteAggregateLoader.load(command.getQuoteId());

        QuoteApprovedEvent event = aggregate.approve(command);

        eventStore.append(QUOTE_AGGREGATE_TYPE, event);
        outboxEventStore.save(event);

        aggregate.apply(event);

//        eventPublisher.publish(event);

        return new QuoteResponse(
                aggregate.getId(),
                aggregate.getStatus().name()
        );
    }

//    public QuoteDetailResponse detail(String id) {
//        Quote quote = findQuoteOrThrow(id);
//
//        return new QuoteDetailResponse(
//                quote.getId(),
//                quote.getCustomerName(),
//                quote.getProductCode(),
//                quote.getPremium(),
//                quote.getStatus().name(),
//                availableActions(quote.getStatus())
//        );
//    }
//
//    public List<QuoteListItemResponse> list(
//            String keyword,
//            String status,
//            String productCode
//    ) {
//        return quoteStore.values()
//                .stream()
//                .filter(quote -> matchKeyword(quote, keyword))
//                .filter(quote -> matchStatus(quote, status))
//                .filter(quote -> matchProductCode(quote, productCode))
//                .sorted(Comparator.comparing(Quote::getCreatedAt).reversed())
//                .map(this::toListItemResponse)
//                .toList();
//    }
//
//    private Quote findQuoteOrThrow(String id) {
//        Quote quote = quoteStore.get(id);
//
//        if (quote == null) {
//            throw new NotFoundException("Quote not found: " + id);
//        }
//
//        return quote;
//    }

    private QuoteResponse toResponse(Quote quote) {
        return new QuoteResponse(
                quote.getId(),
                quote.getStatus().name()
        );
    }

    private QuoteListItemResponse toListItemResponse(Quote quote) {
        return new QuoteListItemResponse(
                quote.getId(),
                quote.getCustomerName(),
                quote.getProductCode(),
                quote.getPremium(),
                quote.getStatus().name()
        );
    }

    private List<String> availableActions(QuoteStatus status) {
        List<String> actions = new ArrayList<>();

        if (status == QuoteStatus.DRAFT) {
            actions.add("SUBMIT");
        }

        if (status == QuoteStatus.SUBMITTED) {
            actions.add("APPROVE");
        }

        return actions;
    }

    private boolean matchKeyword(Quote quote, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase();

        return quote.getCustomerName().toLowerCase().contains(lowerKeyword)
                || quote.getProductCode().toLowerCase().contains(lowerKeyword)
                || quote.getId().toLowerCase().contains(lowerKeyword);
    }

    private boolean matchStatus(Quote quote, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }

        return quote.getStatus().name().equalsIgnoreCase(status);
    }

    private boolean matchProductCode(Quote quote, String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return true;
        }

        return quote.getProductCode().equalsIgnoreCase(productCode);
    }
}



