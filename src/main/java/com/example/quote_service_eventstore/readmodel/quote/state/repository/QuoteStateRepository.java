package com.example.quote_service_eventstore.readmodel.quote.state.repository;

import com.example.quote_service_eventstore.readmodel.quote.state.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.domain.quote.model.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteStateRepository extends JpaRepository<QuoteStateEntity, String> {

    List<QuoteStateEntity> findByStatusOrderByCreatedAtDesc(QuoteStatus status);

    List<QuoteStateEntity> findByProductCodeIgnoreCaseOrderByCreatedAtDesc(String productCode);

    List<QuoteStateEntity> findAllByOrderByCreatedAtDesc();
}
