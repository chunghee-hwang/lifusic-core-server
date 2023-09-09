package com.chung.lifusic.core.filter;

import com.chung.lifusic.core.service.AuthorizationService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Admin, Customer 권한이 있는 지 Account Server에 요청하여 확인
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {
    private final AuthorizationService authorizationService;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final boolean authenticated = authorizationService.checkAuthorization((HttpServletRequest) request);
        if (!authenticated) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
}
