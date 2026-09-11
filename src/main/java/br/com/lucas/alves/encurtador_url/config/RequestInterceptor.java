package br.com.lucas.alves.encurtador_url.config;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(isHealthEndPoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String traceId = request.getHeader(TRACE_ID);
        if(traceId == null || traceId.isBlank()) 
            traceId = java.util.UUID.randomUUID().toString();
        
        MDC.put(TRACE_ID, traceId);
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    private boolean isHealthEndPoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.contains("/health"));
    }
}
