package com.example.keycloak_registration_listener_spi.keycloak.helper.inter;

import org.keycloak.events.admin.AdminEvent;

public interface AdminEventMatcher {
    boolean isRegistrationEvent(AdminEvent event);
    boolean isBlockEvent(AdminEvent event);
    boolean isUnblockEvent(AdminEvent event);
}
