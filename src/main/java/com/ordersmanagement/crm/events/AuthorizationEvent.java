package com.ordersmanagement.crm.events;

import com.ordersmanagement.crm.services.UserDetailsImpl;
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
