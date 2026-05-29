package com.example.quote_service_eventstore.shared.observability;

import com.example.quote_service_eventstore.shared.observability.CorrelationIdProvider;
import com.example.quote_service_eventstore.shared.observability.ObservabilityConstants;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MdcCorrelationIdProvider implements CorrelationIdProvider {

    @Override
    public String getCorrelationId() {
        String correlationId = MDC.get(ObservabilityConstants.CORRELATION_ID_MDC_KEY);

        if (correlationId == null || correlationId.isBlank()) {
            return "unknown";
        }

        return correlationId;
    }
}
