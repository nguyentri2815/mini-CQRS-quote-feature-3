package com.example.quote_service_eventstore.query.quote.application;

import com.example.quote_service_eventstore.shared.exception.NotFoundException;
import com.example.quote_service_eventstore.query.quote.dto.QuoteDetailResponse;
import com.example.quote_service_eventstore.readmodel.quote.entity.QuoteStateEntity;
import com.example.quote_service_eventstore.readmodel.quote.repository.QuoteStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuoteDetailQueryService {

    private final QuoteStateRepository quoteStateRepository;
    private final QuoteActionPolicy quoteActionPolicy;

    public QuoteDetailQueryService(
            QuoteStateRepository quoteStateRepository,
            QuoteActionPolicy quoteActionPolicy
    ) {
        this.quoteStateRepository = quoteStateRepository;
        this.quoteActionPolicy = quoteActionPolicy;
    }

    @Transactional(readOnly = true)
    public QuoteDetailResponse detail(String id) {
        QuoteStateEntity entity = quoteStateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quote not found: " + id));

        return new QuoteDetailResponse(
                entity.getId(),
                entity.getCustomerName(),
                entity.getProductCode(),
                entity.getPremium(),
                entity.getStatus().name(),
                quoteActionPolicy.availableActions(entity.getStatus())
        );
    }

//    @Transactional(readOnly = true)
//    public List<QuoteListItemResponse> list(
//            String keyword,
//            String status,
//            String productCode
//    ) {
//        return quoteStateRepository.findAllByOrderByCreatedAtDesc()
//                .stream()
//                .filter(entity -> matchKeyword(entity, keyword))
//                .filter(entity -> matchStatus(entity, status))
//                .filter(entity -> matchProductCode(entity, productCode))
//                .sorted(Comparator.comparing(QuoteStateEntity::getCreatedAt).reversed())
//                .map(this::toListItemResponse)
//                .toList();
//    }
//
//    private QuoteListItemResponse toListItemResponse(QuoteStateEntity entity) {
//        return new QuoteListItemResponse(
//                entity.getId(),
//                entity.getCustomerName(),
//                entity.getProductCode(),
//                entity.getPremium(),
//                entity.getStatus().name()
//        );
//    }
//
//    private boolean matchKeyword(QuoteStateEntity entity, String keyword) {
//        if (keyword == null || keyword.isBlank()) {
//            return true;
//        }
//
//        String lowerKeyword = keyword.toLowerCase();
//
//        return entity.getCustomerName().toLowerCase().contains(lowerKeyword)
//                || entity.getProductCode().toLowerCase().contains(lowerKeyword)
//                || entity.getId().toLowerCase().contains(lowerKeyword);
//    }
//
//    private boolean matchStatus(QuoteStateEntity entity, String status) {
//        if (status == null || status.isBlank()) {
//            return true;
//        }
//
//        return entity.getStatus().name().equalsIgnoreCase(status);
//    }
//
//    private boolean matchProductCode(QuoteStateEntity entity, String productCode) {
//        if (productCode == null || productCode.isBlank()) {
//            return true;
//        }
//
//        return entity.getProductCode().equalsIgnoreCase(productCode);
//    }
}
