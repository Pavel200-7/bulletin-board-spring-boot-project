package com.example.keycloak_registration_listener_spi.keycloak.helper.impl;

import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.UserEventMatcher;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;

public class UserEventMatcherImpl implements UserEventMatcher {

    @Override
    public boolean isRegistrationEvent(Event event) {
        return event.getType().equals(EventType.REGISTER);
    }

}
