package com.example.keycloak_registration_listener_spi.keycloak.helper.inter;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

public interface UserEventProcessor {
    void processRegistrationEvent(Event event);
}
