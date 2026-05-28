package com.example.quote_service_eventstore.command.quote.application.policy;

import com.example.quote_service_eventstore.shared.exception.ForbiddenException;
import com.example.quote_service_eventstore.shared.security.CurrentUser;
import com.example.quote_service_eventstore.shared.security.RoleConstants;
import org.springframework.stereotype.Component;

@Component
public class QuoteCommandPermissionPolicy {

    public void checkCanCreate(CurrentUser user) {
        if (isAdmin(user)) {
            return;
        }

        if (!user.hasRole(RoleConstants.QUOTE_CREATOR)) {
            throw new ForbiddenException("User does not have permission to create quote");
        }
    }

    public void checkCanSubmit(CurrentUser user) {
        if (isAdmin(user)) {
            return;
        }

        if (!user.hasRole(RoleConstants.QUOTE_SUBMITTER)) {
            throw new ForbiddenException("User does not have permission to submit quote");
        }
    }

    public void checkCanApprove(CurrentUser user) {
        if (isAdmin(user)) {
            return;
        }

        if (!user.hasRole(RoleConstants.QUOTE_APPROVER)) {
            throw new ForbiddenException("User does not have permission to approve quote");
        }
    }

    private boolean isAdmin(CurrentUser user) {
        return user.hasRole(RoleConstants.QUOTE_ADMIN);
    }
}
