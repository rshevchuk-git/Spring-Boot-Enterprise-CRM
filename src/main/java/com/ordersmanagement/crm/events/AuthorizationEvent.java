package com.ordersmanagement.crm.events;

import com.ordersmanagement.crm.models.entities.UserDetailsImpl;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuthorizationEvent extends ApplicationEvent {
    public UserDetailsImpl currentUser;

    public AuthorizationEvent(Object source, UserDetailsImpl user) {
        super(source);
        this.currentUser = user;
    }
}
