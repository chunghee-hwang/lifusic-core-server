package com.chung.lifusic.core.service;

import com.chung.lifusic.core.common.enums.Role;
import dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 계정 서버에 요청하여 현재 로그인이 되어있는 지, 권한이 있는 지 체크하는 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final RestTemplate restTemplate;

    @Value("${host.server.account}")
    private String accountServerHost;
    public boolean checkAuthorization(HttpServletRequest request, Role requiredRole) {
        final UserDto userDto = getAuthenticatedUser(request);
        if (userDto == null) {
            return false;
        }
        String role = userDto.getRole();
        if (role == null) {
            return false;
        }
        return Role.valueOf(role.toUpperCase()) == requiredRole;
    }

    public UserDto getAuthenticatedUser(HttpServletRequest request) {
        final String AUTH_HEADER_KEY = "Authorization";
        final String authHeader = getAuthHeaderFromRequest(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER_KEY, authHeader);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserDto> responseEntity = restTemplate.exchange(accountServerHost + "/api/account/me", HttpMethod.GET, entity, UserDto.class);
            return responseEntity.getBody();
        } catch (Exception exception) {
            log.error("fail to fetch user data: {}", exception.getMessage());
            return null;
        }
    }

    private String getAuthHeaderFromRequest(HttpServletRequest request) {
        final String AUTH_HEADER_KEY = "Authorization";
        return request.getHeader(AUTH_HEADER_KEY);
    }
}
