package com.example.keycloak_registration_listener_spi.keycloak.helper.impl;

import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.AdminEventMatcher;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

@Slf4j
public class AdminEventMatcherImpl implements AdminEventMatcher {

    @Override
    public boolean isRegistrationEvent(AdminEvent event) {
        return event.getOperationType().equals(OperationType.CREATE) &&
                event.getResourceType().equals(ResourceType.USER);
    }

    @Override
    public boolean isBlockEvent(AdminEvent event) {
        return event.getOperationType().equals(OperationType.UPDATE) &&
                event.getResourceType().equals(ResourceType.USER) &&
                isUserDisabled(event);
    }

    @Override
    public boolean isUnblockEvent(AdminEvent event) {
        return event.getOperationType().equals(OperationType.UPDATE) &&
                event.getResourceType().equals(ResourceType.USER) &&
                !isUserDisabled(event);
    }

    private boolean isUserDisabled(AdminEvent event) {
        if (event.getRepresentation() != null) {
            return event.getRepresentation().contains("\"enabled\":false") ||
                    event.getRepresentation().contains("\"enabled\" : false") ||
                    event.getRepresentation().contains("enabled\":false");
        }
        return false;
    }

}
