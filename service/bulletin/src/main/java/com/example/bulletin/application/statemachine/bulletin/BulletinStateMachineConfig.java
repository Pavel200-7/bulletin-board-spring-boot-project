package com.example.bulletin.application.statemachine.bulletin;

import com.example.bulletin.application.statemachine.bulletin.action.BulletinActions;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuards;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory(name = "bulletinStateMachineFactory")
@RequiredArgsConstructor
public class BulletinStateMachineConfig extends EnumStateMachineConfigurerAdapter<BulletinState, BulletinEvent> {

    private final BulletinActions actions;
    private final BulletinGuards guards;

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
                        .initial(BulletinState.MODIFIABLE)
                        .state(BulletinState.MODIFIABLE)
                        .state(BulletinState.APPROVED)
                    .and()
                    .withStates()
                        .parent(BulletinState.ACTIVE)
                        .state(BulletinState.PUBLISHED);
    }

    public void configure(StateMachineTransitionConfigurer<BulletinState, BulletinEvent> transition)
        throws Exception {
        transition
                .withExternal()
                    .source(BulletinState.CREATED).target(BulletinState.MODIFIABLE)
                    .action(actions.setModifiableAction())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.MODIFIABLE).target(BulletinState.APPROVED)
                    .action(actions.setApprovedAction())
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .guard(guards.checkIfCanBeApprovedGuard())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.MODIFIABLE)
                    .action(actions.setModifiableAction())
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .event(BulletinEvent.REJECT)
                .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.PUBLISHED)
                    .action(actions.setPublishedAction())
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .guard(guards.checkIfUserCanBeABulletinPublisherGuard())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .action(actions.setCompletedAction())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.MODIFIABLE)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .action(actions.setModifiableAction())
                    .event(BulletinEvent.REJECT)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .action(actions.setCompletedAction())
                    .event(BulletinEvent.EXPIRE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .action(actions.setCompletedAction())
                    .guard(guards.checkIfUserIsAdminGuard())
                    .event(BulletinEvent.BLOCK);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BulletinState, BulletinEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(false);
    }

}
