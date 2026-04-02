package com.example.chat.conf.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        String command = accessor.getCommand() != null ? accessor.getCommand().name() : "UNKNOWN";
        log.info("WebSocket command: {}", command);

        if ("CONNECT".equals(command)) {
            String token = extractToken(accessor);
            if (token != null) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    String userId = jwt.getClaimAsString("sub");
                    String username = jwt.getClaimAsString("preferred_username");
                    List<String> roles = jwt.getClaimAsStringList("spring_sec_roles");

                    log.info("User: {}, roles: {}", username, roles);

                    // ✅ Преобразуем List<String> в List<GrantedAuthority>
                    List<GrantedAuthority> authorities = roles != null ?
                            roles.stream()
                                    .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                                    .collect(Collectors.toList()) :
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                    // ✅ Создаем JwtAuthenticationToken с правильными параметрами
                    JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities, username);

                    // Сохраняем в атрибуты сессии
                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null) {
                        sessionAttributes.put("authentication", auth);
                        log.info("✅ Authentication saved to session");
                    }

                    accessor.setUser(auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception e) {
                    log.error("❌ Failed to authenticate: {}", e.getMessage(), e);
                }
            }
        }

        // Восстанавливаем аутентификацию для последующих сообщений
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey("authentication")) {
            Authentication auth = (Authentication) sessionAttributes.get("authentication");
            accessor.setUser(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // Из заголовка Authorization
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Из query параметра (для SockJS)
        String query = accessor.getFirstNativeHeader("query");
        if (query != null && query.contains("access_token=")) {
            return query.split("access_token=")[1].split("&")[0];
        }

        return null;
    }

}