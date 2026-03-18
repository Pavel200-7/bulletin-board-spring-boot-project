package com.example.bulletin.conf.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            log.info("→ {} {}", request.getMethod(), request.getRequestURI());

            // Логируем важные заголовки
            logHeaders(request);

            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("← {} {} - {} ({}ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }
    }

    private void logHeaders(HttpServletRequest request) {
        if (!log.isDebugEnabled()) return;

        StringBuilder headers = new StringBuilder("Headers: ");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            Collections.list(headerNames).forEach(headerName -> {
                String headerValue = maskSensitiveData(headerName, request.getHeader(headerName));
                headers.append(headerName).append("=").append(headerValue).append(", ");
            });
        }
        log.debug(headers.toString());
    }

    private String maskSensitiveData(String headerName, String headerValue) {
        if (headerValue == null) return null;

        String lowerHeader = headerName.toLowerCase();
        if (lowerHeader.contains("authorization") ||
                lowerHeader.contains("token") ||
                lowerHeader.contains("secret") ||
                lowerHeader.contains("password")) {
            return "***MASKED***";
        }
        return headerValue;
    }
}