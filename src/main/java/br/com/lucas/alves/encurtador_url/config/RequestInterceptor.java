package br.com.lucas.alves.encurtador_url.config;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestInterceptor extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(isHealthEndPoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }

    private boolean isHealthEndPoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.contains("/health"));
    }
}
