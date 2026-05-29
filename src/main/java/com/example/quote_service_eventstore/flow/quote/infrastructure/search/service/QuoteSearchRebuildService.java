package com.example.quote_service_eventstore.flow.quote.infrastructure.search.service;

import com.example.quote_service_eventstore.flow.quote.infrastructure.search.QuoteIndexAdminService;
import com.example.quote_service_eventstore.readmodel.quote.search.QuoteSearchIndexNames;
import com.example.quote_service_eventstore.readmodel.quote.search.document.QuoteDocument;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.state.repository.QuoteStateRepository;
import com.example.quote_service_eventstore.readmodel.quote.search.mapper.QuoteSearchMapper;
import com.example.quote_service_eventstore.readmodel.quote.search.repository.QuoteSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
public class QuoteSearchRebuildService {

    private static final Logger log = LoggerFactory.getLogger(QuoteSearchRebuildService.class);

    private final QuoteStateRepository quoteStateRepository;
    private final QuoteSearchMapper quoteSearchMapper;
    private final ElasticsearchOperations elasticsearchOperations;
    private final QuoteIndexAdminService quoteIndexAdminService;

    public QuoteSearchRebuildService(
            QuoteStateRepository quoteStateRepository,
            QuoteSearchMapper quoteSearchMapper,
            ElasticsearchOperations elasticsearchOperations,
            QuoteIndexAdminService quoteIndexAdminService
    ) {
        this.quoteStateRepository = quoteStateRepository;
        this.quoteSearchMapper = quoteSearchMapper;
        this.elasticsearchOperations = elasticsearchOperations;
        this.quoteIndexAdminService = quoteIndexAdminService;
    }

    public void rebuildAllWithAliasSwitch() {
        long nextVersion = quoteIndexAdminService.nextIndexVersion();
        String newIndexName = QuoteSearchIndexNames.physicalIndexName(nextVersion);

        log.info("[ES_REINDEX] Start rebuilding quote index. newIndex={}", newIndexName);

        quoteIndexAdminService.createIndexIfNotExists(newIndexName);

        long indexedCount = 0;

        for (QuoteStateEntity entity : quoteStateRepository.findAll()) {
            QuoteDocument document = quoteSearchMapper.toDocument(entity);

            elasticsearchOperations.save(
                    document,
                    IndexCoordinates.of(newIndexName)
            );

            indexedCount++;
        }

        quoteIndexAdminService.switchAlias(newIndexName);

        log.info(
                "[ES_REINDEX] Finished rebuilding quote index. newIndex={}, indexedCount={}",
                newIndexName,
                indexedCount
        );
    }

    public void rebuildAll() {
        rebuildAllWithAliasSwitch();
    }

    public void rebuildOne(String quoteId) {
        String currentIndexName = quoteIndexAdminService.findCurrentIndexByAlias();

        if (currentIndexName == null) {
            log.warn(
                    "[ES_REINDEX] No current quote index alias found. Rebuilding all before indexing one quote. quoteId={}",
                    quoteId
            );
            rebuildAllWithAliasSwitch();
            return;
        }

        QuoteStateEntity entity = quoteStateRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote state not found: " + quoteId));

        QuoteDocument document = quoteSearchMapper.toDocument(entity);

        elasticsearchOperations.save(
                document,
                IndexCoordinates.of(currentIndexName)
        );

        log.info(
                "[ES_REINDEX] Rebuilt one quote document. quoteId={}, index={}, status={}",
                quoteId,
                currentIndexName,
                document.getStatus()
        );
    }
}
