//package com.example.bulletin.conf;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.stream.Stream;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//@Slf4j
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)
//            throws Exception {
//        http
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/*").permitAll()
//                        .requestMatchers("/manager.html").hasRole("MANAGER")
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer((oauth2) -> oauth2
//                        .jwt(Customizer.withDefaults())
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );;
//        return http.build();
//    }
//
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
//            var roles =jwt.getClaimAsStringList("spring_sec_roles");
//
//            return Stream.concat(authorities.stream(),
//                            roles.stream()
//                                    .filter(role -> role.startsWith("ROLE_"))
//                                    .map(SimpleGrantedAuthority::new)
//                                    .map(GrantedAuthority.class::cast))
//                    .toList();
//        });
//        return jwtAuthenticationConverter;
//    }
//
//}