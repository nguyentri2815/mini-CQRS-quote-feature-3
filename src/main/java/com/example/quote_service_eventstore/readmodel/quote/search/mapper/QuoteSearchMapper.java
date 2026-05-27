package com.example.quote_service_eventstore.readmodel.quote.search.mapper;

import com.example.quote_service_eventstore.query.quote.dto.QuoteListItemResponse;
import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.search.document.QuoteDocument;
import org.springframework.stereotype.Component;

@Component
public class QuoteSearchMapper {

    public QuoteDocument toDocument(QuoteStateEntity entity) {
        return new QuoteDocument(
                entity.getId(),
                entity.getCustomerName(),
                entity.getProductCode(),
                entity.getPremium(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public QuoteListItemResponse toListItemResponse(QuoteDocument document) {
        return new QuoteListItemResponse(
                document.getId(),
                document.getCustomerName(),
                document.getProductCode(),
                document.getPremium(),
                document.getStatus()
        );
    }
}
