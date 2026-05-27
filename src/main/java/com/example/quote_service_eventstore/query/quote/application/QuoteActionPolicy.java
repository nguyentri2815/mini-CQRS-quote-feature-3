package com.example.quote_service_eventstore.query.quote.application;

import com.example.quote_service_eventstore.domain.quote.model.QuoteStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuoteActionPolicy {

    public List<String> availableActions(QuoteStatus status) {
        List<String> actions = new ArrayList<>();

        if (status == QuoteStatus.DRAFT) {
            actions.add("SUBMIT");
        }

        if (status == QuoteStatus.SUBMITTED) {
            actions.add("APPROVE");
        }

        return actions;
    }
}
