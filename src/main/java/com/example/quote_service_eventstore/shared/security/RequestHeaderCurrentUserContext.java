package com.example.quote_service_eventstore.shared.security;

import com.example.quote_service_eventstore.shared.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestHeaderCurrentUserContext implements CurrentUserContext {

    public static final String CURRENT_USER_ATTRIBUTE = "CURRENT_USER";

    private final HttpServletRequest request;

    public RequestHeaderCurrentUserContext(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public CurrentUser getCurrentUser() {
        Object currentUser = request.getAttribute(CURRENT_USER_ATTRIBUTE);

        if (currentUser instanceof CurrentUser user) {
            return user;
        }

        throw new BusinessException("Current user is missing from request context");
    }
}
