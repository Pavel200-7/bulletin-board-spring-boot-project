package com.example.bulletin.application.statemachine.bulletin;

import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
//@EnableStateMachine(name = "BulletinStateMachine")
@EnableStateMachineFactory(name = "bulletinStateMachineFactory")
public class BulletinStateMachineConfig extends EnumStateMachineConfigurerAdapter<BulletinState, BulletinEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<BulletinState, BulletinEvent> states)
            throws Exception {
        states
                .withStates()
                    .initial(BulletinState.CREATED)
                    .state(BulletinState.ACTIVE)
                    .state(BulletinState.INACTIVE)
                    .end(BulletinState.COMPLETED)
                    .and()
                    .withStates()
                        .parent(BulletinState.INACTIVE)
                        .initial(BulletinState.CREATED)
                        .state(BulletinState.MODIFIABLE)
                        .state(BulletinState.APPROVED)
                    .and()
                    .withStates()
                        .parent(BulletinState.ACTIVE)
                        .state(BulletinState.PUBLISHED)
                        .state(BulletinState.CLOSED);
    }

    public void configure(StateMachineTransitionConfigurer<BulletinState, BulletinEvent> transition)
        throws Exception {
        transition
                .withExternal()
                    .source(BulletinState.CREATED).target(BulletinState.MODIFIABLE)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.MODIFIABLE).target(BulletinState.APPROVED)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.MODIFIABLE)
                    .event(BulletinEvent.REJECT)
                    .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.PUBLISHED)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.CLOSED)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.CLOSED).target(BulletinState.COMPLETED)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .event(BulletinEvent.EXPIRE)
                    .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .event(BulletinEvent.BLOCK);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BulletinState, BulletinEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);
    }

}
