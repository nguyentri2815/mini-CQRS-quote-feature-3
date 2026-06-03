package com.example.quote_service_eventstore.admin.quote.application;

import com.example.quote_service_eventstore.admin.quote.dto.*;
import com.example.quote_service_eventstore.command.quote.infrastructure.eventstore.EventStoreEntity;
import com.example.quote_service_eventstore.command.quote.infrastructure.eventstore.EventStoreJpaRepository;
import com.example.quote_service_eventstore.command.quote.infrastructure.outbox.OutboxEventEntity;
import com.example.quote_service_eventstore.command.quote.infrastructure.outbox.OutboxEventRepository;
import com.example.quote_service_eventstore.readmodel.quote.search.QuoteSearchIndexNames;
import com.example.quote_service_eventstore.readmodel.quote.search.document.QuoteDocument;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.state.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.shared.messaging.dedup.ProcessedMessageEntity;
import com.example.quote_service_eventstore.shared.messaging.dedup.ProcessedMessageRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuoteDebugService {

    private final EventStoreJpaRepository eventStoreJpaRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ProcessedMessageRepository processedMessageRepository;
    private final QuoteStateRepository quoteStateRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public QuoteDebugService(
            EventStoreJpaRepository eventStoreJpaRepository,
            OutboxEventRepository outboxEventRepository,
            ProcessedMessageRepository processedMessageRepository,
            QuoteStateRepository quoteStateRepository,
            ElasticsearchOperations elasticsearchOperations
    ) {
        this.eventStoreJpaRepository = eventStoreJpaRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.processedMessageRepository = processedMessageRepository;
        this.quoteStateRepository = quoteStateRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Transactional(readOnly = true)
    public List<EventTimelineItem> eventTimeline(String quoteId) {
        return eventStoreJpaRepository.findByAggregateIdOrderByVersionAsc(quoteId)
                .stream()
                .map(this::toEventTimelineItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OutboxDebugItem> outbox(String quoteId) {
        return outboxEventRepository.findByAggregateIdOrderByCreatedAtAsc(quoteId)
                .stream()
                .map(this::toOutboxDebugItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProcessedMessageDebugItem> processedMessages(String quoteId) {
        return processedMessageRepository.findByAggregateIdOrderByProcessedAtAsc(quoteId)
                .stream()
                .map(this::toProcessedMessageDebugItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteStateDebugResponse quoteState(String quoteId) {
        return quoteStateRepository.findById(quoteId)
                .map(this::toQuoteStateDebugResponse)
                .orElse(null);
    }

    public EsDocumentDebugResponse esDocument(String quoteId) {
        QuoteDocument document = elasticsearchOperations.get(
                quoteId,
                QuoteDocument.class,
                IndexCoordinates.of(QuoteSearchIndexNames.QUOTE_INDEX_ALIAS)
        );

        if (document == null) {
            return EsDocumentDebugResponse.missing(
                    QuoteSearchIndexNames.QUOTE_INDEX_ALIAS
            );
        }

        return EsDocumentDebugResponse.found(
                QuoteSearchIndexNames.QUOTE_INDEX_ALIAS,
                document.getId(),
                document.getCustomerName(),
                document.getProductCode(),
                document.getPremium(),
                document.getStatus()
        );
    }

    private EventTimelineItem toEventTimelineItem(EventStoreEntity entity) {
        return new EventTimelineItem(
                entity.getEventId(),
                entity.getAggregateId(),
                entity.getAggregateType(),
                entity.getEventType(),
                entity.getVersion(),
                entity.getCreatedAt()
        );
    }

    private OutboxDebugItem toOutboxDebugItem(OutboxEventEntity entity) {
        return new OutboxDebugItem(
                entity.getId(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getAggregateVersion(),
                entity.getStatus().name(),
                entity.getRetryCount(),
                entity.getLastError(),
                entity.getCorrelationId(),
                entity.getCreatedAt(),
                entity.getSentAt()
        );
    }

    private ProcessedMessageDebugItem toProcessedMessageDebugItem(
            ProcessedMessageEntity entity
    ) {
        return new ProcessedMessageDebugItem(
                entity.getMessageId(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getProcessedAt()
        );
    }

    private QuoteStateDebugResponse toQuoteStateDebugResponse(
            QuoteStateEntity entity
    ) {
        return new QuoteStateDebugResponse(
                entity.getId(),
                entity.getCustomerName(),
                entity.getProductCode(),
                entity.getPremium(),
                entity.getStatus().name(),
                entity.getLastProjectedVersion(),
                entity.getTenantId(),
                entity.getOrganizationId(),
                entity.getCreatedBy(),
                entity.getSubmittedBy(),
                entity.getApprovedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public QuoteDiagnosticResponse diagnostic(String quoteId) {
        List<EventTimelineItem> events = eventTimeline(quoteId);
        List<OutboxDebugItem> outboxEvents = outbox(quoteId);
        List<ProcessedMessageDebugItem> processedMessages = processedMessages(quoteId);
        QuoteStateDebugResponse state = quoteState(quoteId);
        EsDocumentDebugResponse es = esDocument(quoteId);

        String diagnosis = diagnose(
                events,
                outboxEvents,
                processedMessages,
                state,
                es
        );

        return new QuoteDiagnosticResponse(
                quoteId,
                events,
                outboxEvents,
                processedMessages,
                state,
                es,
                diagnosis
        );
    }

    private String diagnose(
            List<EventTimelineItem> events,
            List<OutboxDebugItem> outboxEvents,
            List<ProcessedMessageDebugItem> processedMessages,
            QuoteStateDebugResponse state,
            EsDocumentDebugResponse es
    ) {
        if (events.isEmpty()) {
            return "NO_EVENTS_FOUND: command side may not have created this quote or event_store is missing data.";
        }

        EventTimelineItem latestEvent = events.get(events.size() - 1);

        boolean hasOutboxForLatestEvent = outboxEvents.stream()
                .anyMatch(item -> item.getEventType().equals(latestEvent.getEventType())
                        && item.getAggregateVersion() == latestEvent.getVersion());

        if (!hasOutboxForLatestEvent) {
            return "EVENT_WITHOUT_OUTBOX: event_store has latest event but outbox event is missing.";
        }

        boolean latestOutboxSent = outboxEvents.stream()
                .filter(item -> item.getEventType().equals(latestEvent.getEventType())
                        && item.getAggregateVersion() == latestEvent.getVersion())
                .anyMatch(item -> "SENT".equals(item.getStatus()));

        if (!latestOutboxSent) {
            return "OUTBOX_NOT_SENT: outbox event exists but has not been published successfully.";
        }

        boolean processed = processedMessages.stream()
                .anyMatch(item -> item.getEventType().equals(latestEvent.getEventType()));

        if (!processed) {
            return "MESSAGE_NOT_PROCESSED: message was published but consumer has not processed it or it went to DLQ.";
        }

        if (state == null) {
            return "QUOTE_STATE_MISSING: consumer may have processed message but projection did not create quote_state.";
        }

        if (state.getLastProjectedVersion() < latestEvent.getVersion()) {
            return "PROJECTION_STALE: quote_state version is behind event_store latest version.";
        }

        if (!es.isExists()) {
            return "ES_DOCUMENT_MISSING: quote_state exists but Elasticsearch document is missing.";
        }

        if (!state.getStatus().equals(es.getStatus())) {
            return "ES_STALE: Elasticsearch status does not match quote_state.";
        }

        return "OK: event_store, outbox, consumer, quote_state and Elasticsearch look consistent.";
    }


}
