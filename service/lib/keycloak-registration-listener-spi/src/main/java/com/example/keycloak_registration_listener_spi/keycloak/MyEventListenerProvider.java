package com.example.keycloak_registration_listener_spi.keycloak;

import com.example.keycloak_registration_listener_spi.keycloak.helper.impl.AdminEventMatcherImpl;
import com.example.keycloak_registration_listener_spi.keycloak.helper.impl.UserEventMatcherImpl;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.AdminEventMatcher;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.AdminEventProcessor;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.UserEventMatcher;
import com.example.keycloak_registration_listener_spi.keycloak.helper.inter.UserEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

@Slf4j
public class MyEventListenerProvider implements EventListenerProvider {

    private final UserEventMatcher userEventMatcher;
    private final AdminEventMatcher adminEventMatcher;
    private final UserEventProcessor userEventProcessor;
    private final AdminEventProcessor adminEventProcessor;

    public MyEventListenerProvider(
            UserEventProcessor userEventProcessor,
            AdminEventProcessor adminEventProcessor) {
        this.adminEventMatcher = new AdminEventMatcherImpl();
        this.userEventMatcher = new UserEventMatcherImpl();
        this.userEventProcessor = userEventProcessor;
        this.adminEventProcessor = adminEventProcessor;
    }

    @Override
    public void onEvent(Event event) {
        if (userEventMatcher.isRegistrationEvent(event)) {
            userEventProcessor.processRegistrationEvent(event);
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean b) {
        if (adminEventMatcher.isRegistrationEvent(event)) {
            adminEventProcessor.processRegistrationEvent(event);
        } else if (adminEventMatcher.isBlockEvent(event)) {
            adminEventProcessor.processUserBlockEvent(event);
        } else if (adminEventMatcher.isUnblockEvent(event)) {
            adminEventProcessor.processUserUnblockEvent(event);
        }
    }

    @Override
    public void close() {
        log.info("EventListener закрыт");
    }

}
