package com.example.bulletin.application.statemachine.bulletin;

import com.example.bulletin.application.statemachine.bulletin.action.BulletinActions;
import com.example.bulletin.application.statemachine.bulletin.guard.BulletinGuards;
import com.example.bulletin.domain.enums.bulletin.BulletinEvent;
import com.example.bulletin.domain.enums.bulletin.BulletinState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "bulletinStateMachineFactory")
@RequiredArgsConstructor
public class BulletinStateMachineConfig extends EnumStateMachineConfigurerAdapter<BulletinState, BulletinEvent> {

    private final BulletinGuards guards;
    private final BulletinActions actions;

    @Override
    public void configure(StateMachineStateConfigurer<BulletinState, BulletinEvent> states)
            throws Exception {
        states
                .withStates()
                    .initial(BulletinState.CREATED)
                    .state(BulletinState.MODIFIABLE)
                    .state(BulletinState.APPROVED)
                    .state(BulletinState.PUBLISHED)
                    .end(BulletinState.COMPLETED);
//                    .states(EnumSet.allOf(BulletinState.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BulletinState, BulletinEvent> transition)
        throws Exception {
        transition
                .withExternal()
                    .source(BulletinState.CREATED).target(BulletinState.MODIFIABLE)
                    .event(BulletinEvent.APPROVE)
                    .and()
                .withExternal()
                    .source(BulletinState.MODIFIABLE).target(BulletinState.APPROVED)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .guard(guards.checkIfCanBeApprovedGuard())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withInternal()
                    .source(BulletinState.MODIFIABLE)
                    .action(actions.updateAction())
                    .event(BulletinEvent.UPDATE)
                .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.MODIFIABLE)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .event(BulletinEvent.REJECT)
                .and()
                .withExternal()
                    .source(BulletinState.APPROVED).target(BulletinState.PUBLISHED)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .guard(guards.checkIfUserCanBeABulletinPublisherGuard())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .event(BulletinEvent.APPROVE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.MODIFIABLE)
                    .guard(guards.checkIfUserIsOwnerGuard())
                    .event(BulletinEvent.REJECT)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
                    .event(BulletinEvent.EXPIRE)
                .and()
                .withExternal()
                    .source(BulletinState.PUBLISHED).target(BulletinState.COMPLETED)
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
