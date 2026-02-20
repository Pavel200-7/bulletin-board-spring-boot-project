package com.example.keycloak_registration_listener_spi.keycloak.helper.inter;

import org.keycloak.events.Event;

public interface UserEventMatcher {
    boolean isRegistrationEvent(Event event);
}
