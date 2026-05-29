package com.example.quote_service_eventstore.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ElasticsearchHttpClientConfig {

    @Bean("elasticsearchHttpRestClient")
    public RestClient elasticsearchRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:9200")
                .build();
    }
}
