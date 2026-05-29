package com.example.quote_service_eventstore.flow.quote.infrastructure.search;

import com.example.quote_service_eventstore.readmodel.quote.search.QuoteSearchIndexNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class QuoteIndexAdminService {

    private static final Logger log = LoggerFactory.getLogger(QuoteIndexAdminService.class);

    private final RestClient elasticsearchRestClient;

    public QuoteIndexAdminService(RestClient elasticsearchRestClient) {
        this.elasticsearchRestClient = elasticsearchRestClient;
    }

    public boolean indexExists(String indexName) {
        try {
            elasticsearchRestClient.head()
                    .uri("/{indexName}", indexName)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public void createIndexIfNotExists(String indexName) {
        if (indexExists(indexName)) {
            log.info("[ES_INDEX] Index already exists. index={}", indexName);
            return;
        }

        Map<String, Object> body = Map.of(
                "mappings", Map.of(
                        "properties", Map.of(
                                "id", Map.of("type", "keyword"),
                                "customerName", Map.of("type", "text"),
                                "productCode", Map.of("type", "keyword"),
                                "premium", Map.of("type", "double"),
                                "status", Map.of("type", "keyword"),
                                "createdAt", Map.of("type", "date", "format", "strict_date_optional_time||yyyy-MM-dd'T'HH:mm:ss"),
                                "updatedAt", Map.of("type", "date", "format", "strict_date_optional_time||yyyy-MM-dd'T'HH:mm:ss")
                        )
                )
        );

        elasticsearchRestClient.put()
                .uri("/{indexName}", indexName)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        log.info("[ES_INDEX] Created index. index={}", indexName);
    }

    public void switchAlias(String newIndexName) {
        String oldIndexName = findCurrentIndexByAlias();

        Map<String, Object> actions;

        if (oldIndexName == null) {
            actions = Map.of(
                    "actions", new Object[]{
                            Map.of("add", Map.of(
                                    "index", newIndexName,
                                    "alias", QuoteSearchIndexNames.QUOTE_INDEX_ALIAS
                            ))
                    }
            );
        } else {
            actions = Map.of(
                    "actions", new Object[]{
                            Map.of("remove", Map.of(
                                    "index", oldIndexName,
                                    "alias", QuoteSearchIndexNames.QUOTE_INDEX_ALIAS
                            )),
                            Map.of("add", Map.of(
                                    "index", newIndexName,
                                    "alias", QuoteSearchIndexNames.QUOTE_INDEX_ALIAS
                            ))
                    }
            );
        }

        elasticsearchRestClient.post()
                .uri("/_aliases")
                .body(actions)
                .retrieve()
                .toBodilessEntity();

        log.info(
                "[ES_INDEX] Switched alias. alias={}, oldIndex={}, newIndex={}",
                QuoteSearchIndexNames.QUOTE_INDEX_ALIAS,
                oldIndexName,
                newIndexName
        );
    }

    @SuppressWarnings("unchecked")
    public String findCurrentIndexByAlias() {
        try {
            Map<String, Object> response = elasticsearchRestClient.get()
                    .uri("/_alias/{alias}", QuoteSearchIndexNames.QUOTE_INDEX_ALIAS)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.isEmpty()) {
                return null;
            }

            return response.keySet()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception exception) {
            return null;
        }
    }

    public long nextIndexVersion() {
        String currentIndex = findCurrentIndexByAlias();

        if (currentIndex == null) {
            return 1L;
        }

        String prefix = QuoteSearchIndexNames.QUOTE_INDEX_PREFIX;

        if (!currentIndex.startsWith(prefix)) {
            return System.currentTimeMillis();
        }

        String versionText = currentIndex.substring(prefix.length());

        try {
            return Long.parseLong(versionText) + 1;
        } catch (NumberFormatException exception) {
            return System.currentTimeMillis();
        }
    }
}
