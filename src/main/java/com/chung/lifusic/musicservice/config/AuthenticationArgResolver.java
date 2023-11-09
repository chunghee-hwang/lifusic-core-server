package com.chung.lifusic.musicservice.config;

import com.chung.lifusic.musicservice.common.annotations.AuthenticatedUser;
import com.chung.lifusic.musicservice.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthenticationArgResolver implements HandlerMethodArgumentResolver {
    private final AuthorizationService authorizationService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AuthenticatedUser authenticatedUser = parameter.getParameterAnnotation(AuthenticatedUser.class);
        if (authenticatedUser == null) {
            return WebArgumentResolver.UNRESOLVED;
        }
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (httpServletRequest == null) {
            return null;
        } else {
            return authorizationService.getAuthenticatedUser(httpServletRequest);
        }
    }
}
