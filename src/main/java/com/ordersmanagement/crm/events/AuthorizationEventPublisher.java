package com.ordersmanagement.crm.events;

import com.ordersmanagement.crm.models.entities.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(final UserDetailsImpl user) {
        AuthorizationEvent customSpringEvent = new AuthorizationEvent(this, user);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}
