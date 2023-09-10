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
    public boolean checkAuthorization(HttpServletRequest request) {
        final Role requiredRole = getRequiredRole(request.getRequestURI());
        final String AUTH_HEADER_KEY = "Authorization";
        final String authHeader = request.getHeader(AUTH_HEADER_KEY);
        final UserDto userDto = getAuthenticatedUser(authHeader);
        if (userDto == null) {
            return false;
        }
        String role = userDto.getRole();
        if (role == null) {
            return false;
        }
        return Role.valueOf(role.toUpperCase()) == requiredRole;
    }

    public UserDto getAuthenticatedUser(String authHeader) {
        final String AUTH_HEADER_KEY = "Authorization";
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

    // api uri가 어떻게 시작하느냐에 따라서 어떤 권한을 체크할 지 결정
    private Role getRequiredRole(String requestedURI) {
        final String ADMIN_URL = "/api/admin";
        //        final String CUSTOMER_URL = "/api/music";

        Role requiredRole;
        if (requestedURI.startsWith(ADMIN_URL)) {
            requiredRole = Role.ADMIN; // 아티스트 권한
        } else {
            requiredRole = Role.CUSTOMER; // 일반 사용자 권한
        }
        return requiredRole;
    }
}
