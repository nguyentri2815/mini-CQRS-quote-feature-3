package com.example.quote_service_eventstore.kafka;

import com.example.quote_service_eventstore.command.quote.application.repository.QuoteAggregateRepository;
import com.example.quote_service_eventstore.command.quote.infrastructure.eventstore.EventStoreJpaRepository;
import com.example.quote_service_eventstore.command.quote.infrastructure.outbox.OutboxEventRepository;
import com.example.quote_service_eventstore.command.quote.infrastructure.outbox.OutboxMessagePublisher;
import com.example.quote_service_eventstore.domain.quote.aggregate.QuoteAggregate;
import com.example.quote_service_eventstore.domain.quote.command.CreateQuoteCommand;
import com.example.quote_service_eventstore.domain.quote.command.SubmitQuoteCommand;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.state.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.shared.eventsource.AggregateCommandResult;
import com.example.quote_service_eventstore.shared.messaging.DomainEventMessage;
import com.example.quote_service_eventstore.shared.messaging.dedup.ProcessedMessageRepository;
import com.example.quote_service_eventstore.shared.messaging.kafka.QuoteKafkaTopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class QuoteKafkaIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("quote_kafka_test_db")
            .withUsername("quote_test")
            .withPassword("quote_test");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "quote-flow-service-test");
    }

    @Autowired
    private QuoteAggregateRepository quoteAggregateRepository;

    @Autowired
    private OutboxMessagePublisher outboxMessagePublisher;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private EventStoreJpaRepository eventStoreJpaRepository;

    @Autowired
    private QuoteStateRepository quoteStateRepository;

    @Autowired
    private ProcessedMessageRepository processedMessageRepository;

//    @MockBean
//    private com.example.quote_service_eventstore.flow.quote.infrastructure.search.QuoteIndexService quoteIndexService;

    @MockBean
    private com.example.quote_service_eventstore.flow.quote.infrastructure.notification.QuoteNotificationService quoteNotificationService;

    @MockBean
    private com.example.quote_service_eventstore.flow.quote.infrastructure.allocation.QuoteAllocationGateway quoteAllocationGateway;

    @BeforeEach
    void setUp() {
        processedMessageRepository.deleteAll();
        quoteStateRepository.deleteAll();
        outboxEventRepository.deleteAll();
        eventStoreJpaRepository.deleteAll();
    }

    @Test
    void publishKafkaAndConsumeCreatedEvent_shouldCreateQuoteStateVersion1() {
        AggregateCommandResult<QuoteAggregate> created =
                quoteAggregateRepository.create(createCommand());

        String quoteId = created.getAggregateId();

        outboxMessagePublisher.publishPendingEvents();

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    QuoteStateEntity state = quoteStateRepository.findById(quoteId)
                            .orElseThrow();

                    assertThat(state.getStatus().name()).isEqualTo("DRAFT");
                    assertThat(state.getLastProjectedVersion()).isEqualTo(1L);
                    assertThat(state.getCustomerName()).isEqualTo("Nguyen Kafka Test");
                });

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertThat(processedMessageRepository.count()).isEqualTo(1L);
                });
    }

    @Test
    void publishKafkaAndConsumeSubmittedEvent_shouldUpdateQuoteStateVersion2() {
        AggregateCommandResult<QuoteAggregate> created =
                quoteAggregateRepository.create(createCommand());

        String quoteId = created.getAggregateId();

        outboxMessagePublisher.publishPendingEvents();

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    QuoteStateEntity state = quoteStateRepository.findById(quoteId)
                            .orElseThrow();

                    assertThat(state.getStatus().name()).isEqualTo("DRAFT");
                    assertThat(state.getLastProjectedVersion()).isEqualTo(1L);
                });

        quoteAggregateRepository.update(
                quoteId,
                submitCommand(quoteId)
        );

        outboxMessagePublisher.publishPendingEvents();

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    QuoteStateEntity state = quoteStateRepository.findById(quoteId)
                            .orElseThrow();

                    assertThat(state.getStatus().name()).isEqualTo("SUBMITTED");
                    assertThat(state.getLastProjectedVersion()).isEqualTo(2L);
                    assertThat(state.getSubmittedBy()).isEqualTo("u200");
                });

        await().atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertThat(processedMessageRepository.count()).isEqualTo(2L);
                });
    }

    @Test
    void consumerFailure_shouldRetryAndPublishToDlt() {
        DomainEventMessage poisonMessage = new DomainEventMessage(
                "poison-001",
                "FAIL_TEST",
                "Quote",
                "QuoteSubmittedEvent",
                "{}",
                2L,
                "FORCE_DLT",
                LocalDateTime.now()
        );

        org.springframework.kafka.core.KafkaTemplate<String, DomainEventMessage> kafkaTemplate =
                getKafkaTemplateFromContext();

        kafkaTemplate.send(
                QuoteKafkaTopicNames.QUOTE_EVENTS,
                poisonMessage.getAggregateId(),
                poisonMessage
        );

        Consumer<String, DomainEventMessage> dltConsumer = createDltConsumer();

        dltConsumer.subscribe(java.util.List.of(QuoteKafkaTopicNames.QUOTE_EVENTS_DLT));

        await().atMost(20, SECONDS)
                .untilAsserted(() -> {
                    ConsumerRecord<String, DomainEventMessage> record =
                            pollOneRecord(dltConsumer);

                    assertThat(record).isNotNull();
                    assertThat(record.key()).isEqualTo("FAIL_TEST");
                    assertThat(record.value().getEventId()).isEqualTo("poison-001");
                    assertThat(record.value().getCorrelationId()).isEqualTo("FORCE_DLT");
                });

        dltConsumer.close();
    }

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    private org.springframework.kafka.core.KafkaTemplate<String, DomainEventMessage>
    getKafkaTemplateFromContext() {
        return applicationContext.getBean(org.springframework.kafka.core.KafkaTemplate.class);
    }

    private Consumer<String, DomainEventMessage> createDltConsumer() {
        JsonDeserializer<DomainEventMessage> valueDeserializer =
                new JsonDeserializer<>(DomainEventMessage.class);

        valueDeserializer.addTrustedPackages(
                "com.example.quoteservice.shared.messaging"
        );

        Map<String, Object> props = Map.of(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers(),
                org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG,
                "quote-dlt-test-consumer-" + System.currentTimeMillis(),
                org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest",
                org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class,
                org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        ).createConsumer();
    }

    private ConsumerRecord<String, DomainEventMessage> pollOneRecord(
            Consumer<String, DomainEventMessage> consumer
    ) {
        var records = consumer.poll(Duration.ofMillis(500));

        if (records.isEmpty()) {
            return null;
        }

        return records.iterator().next();
    }

    private CreateQuoteCommand createCommand() {
        return new CreateQuoteCommand(
                "Nguyen Kafka Test",
                "MOTOR",
                BigDecimal.valueOf(1200000),
                "u100",
                "Creator User",
                "tenant-a",
                "org-hcm"
        );
    }

    private SubmitQuoteCommand submitCommand(String quoteId) {
        return new SubmitQuoteCommand(
                quoteId,
                "u200",
                "Submit User",
                "tenant-a",
                "org-hcm"
        );
    }
}
