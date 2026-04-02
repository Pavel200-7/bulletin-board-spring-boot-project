//package com.example.chat.conf.security;
//
//import com.example.chat.conf.filter.WebSocketAuthInterceptor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.security.authorization.AuthorizationEventPublisher;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
//import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
//import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
//import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
//import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
//import java.util.List;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final ApplicationContext applicationContext;
//
//    @Bean
//    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
//        MessageMatcherDelegatingAuthorizationManager.Builder messages =
//                new MessageMatcherDelegatingAuthorizationManager.Builder();
//
//        messages
//                .nullDestMatcher().permitAll()
//                .simpDestMatchers("/app/**").authenticated()
//                .anyMessage().permitAll();
//
//        return messages.build();
//    }
//
//    public WebSocketSecurityConfig(ApplicationContext applicationContext, AuthorizationManager<Message<?>> authorizationManager) {
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
//    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(messageAuthorizationManager());
//        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(applicationContext);
//        authz.setAuthorizationEventPublisher(publisher);
//        registration.interceptors(new SecurityContextChannelInterceptor(), authz);
//    }
//
//}