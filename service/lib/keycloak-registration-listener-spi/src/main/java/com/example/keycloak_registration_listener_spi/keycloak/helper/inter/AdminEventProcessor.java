package com.example.keycloak_registration_listener_spi.keycloak.helper.inter;

import org.keycloak.events.admin.AdminEvent;

public interface AdminEventProcessor {
    void processRegistrationEvent(AdminEvent event);
    void processUserBlockEvent(AdminEvent event);
    void processUserUnblockEvent(AdminEvent event);
}
