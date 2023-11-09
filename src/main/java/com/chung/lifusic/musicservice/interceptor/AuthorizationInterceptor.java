package com.chung.lifusic.musicservice.interceptor;

import com.chung.lifusic.musicservice.common.annotations.AuthorizationValid;
import com.chung.lifusic.musicservice.common.enums.Role;
import com.chung.lifusic.musicservice.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final AuthorizationService authorizationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        if (!method.isAnnotationPresent(AuthorizationValid.class)) {
            return true;
        }

        AuthorizationValid annotation = method.getAnnotation(AuthorizationValid.class);
        Role requiredRole = annotation.role();
        boolean isAuthorized = authorizationService.checkAuthorization(request, requiredRole);

        if (isAuthorized) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Not Authenticated or Authorized");

        return false;
    }
}
