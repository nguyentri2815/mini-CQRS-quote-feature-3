package com.example.quote_service_eventstore.query.quote.application;

import com.example.quote_service_eventstore.domain.quote.model.QuoteStatus;
import com.example.quote_service_eventstore.shared.security.CurrentUser;
import com.example.quote_service_eventstore.shared.security.RoleConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuoteActionPolicy {

    public List<String> availableActions(
            QuoteStatus status,
            CurrentUser user
    ) {
        List<String> actions = new ArrayList<>();

        if (user.hasRole(RoleConstants.QUOTE_ADMIN)) {
            addAdminActions(status, actions);
            return actions;
        }

        if (status == QuoteStatus.DRAFT
                && user.hasRole(RoleConstants.QUOTE_SUBMITTER)) {
            actions.add("SUBMIT");
        }

        if (status == QuoteStatus.SUBMITTED
                && user.hasRole(RoleConstants.QUOTE_APPROVER)) {
            actions.add("APPROVE");
        }

        return actions;
    }

    private void addAdminActions(
            QuoteStatus status,
            List<String> actions
    ) {
        if (status == QuoteStatus.DRAFT) {
            actions.add("SUBMIT");
        }

        if (status == QuoteStatus.SUBMITTED) {
            actions.add("APPROVE");
        }
    }
}
