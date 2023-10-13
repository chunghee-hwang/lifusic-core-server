package service;

import com.chung.lifusic.core.common.enums.Role;
import com.chung.lifusic.core.dto.UserDto;
import com.chung.lifusic.core.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("local")
public class AuthorizationServiceTest {
    @InjectMocks
    private AuthorizationService service;

    @Mock
    private RestTemplate restTemplate;

    @Value("${host.server.gateway}")
    private String GATEWAY_HOST;

    @DisplayName("인증하지 않은 유저는 정보 가져오기 실패")
    @Test
    public void failToGetAuthenticatedUser() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        // mocking
        when(restTemplate.exchange(
                Mockito.eq(GATEWAY_HOST + "/api/account/me"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(UserDto.class)
        )).thenThrow(RestClientException.class);

        // when
        UserDto userDto = service.getAuthenticatedUser(request);

        // then
        Assertions.assertNull(userDto);
    }

    @DisplayName("인증된 유저는 정보 가져오기 성공")
    @Test
    public void successToGetAuthenticatedUser() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        UserDto userDto = getUser("test@email.com", "admin");

        // mocking
        mockGetUserInfo(userDto);

        // when
        UserDto userDtoResponse = service.getAuthenticatedUser(request);

        // then
        Assertions.assertNotNull(userDtoResponse.getEmail());
    }

    @DisplayName("인증 되지 않은 유저는 인가 실패")
    @Test
    public void failToAuthorizationIfNotAuthenticated() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        Role role = Role.ADMIN;

        // mocking
        given(service.getAuthenticatedUser(request)).willReturn(null);

        // when
        boolean isAuthorized = service.checkAuthorization(request, role);

        // then
        Assertions.assertFalse(isAuthorized);
    }

    @DisplayName("유저 권한 정보가 userDto에 없으면 인가 실패")
    @Test
    public void failToAuthorizationIfRoleEmpty() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        UserDto userDto = getUser("test@email.com", null);

        // mocking
        mockGetUserInfo(userDto);

        // when
        boolean isAuthorized = service.checkAuthorization(request, Role.ADMIN);

        // then
        Assertions.assertFalse(isAuthorized);
    }

    @DisplayName("유저 권한이 요구 되는 권한과 다르면 인가 실패")
    @Test
    public void failToAuthorizationIfRoleDifferent() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        UserDto userDto = getUser("test@email.com", "customer");

        // mocking
        mockGetUserInfo(userDto);

        // when
        boolean isAuthorized = service.checkAuthorization(request, Role.ADMIN);

        // then
        Assertions.assertFalse(isAuthorized);
    }

    @DisplayName("유저 권한이 요구 되는 권한 같으면 인가 성공")
    @Test
    public void successToAuthorizationIfRoleSame() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        UserDto userDto = getUser("test@email.com", "admin");

        // mocking
        mockGetUserInfo(userDto);

        // when
        boolean isAuthorized = service.checkAuthorization(request, Role.ADMIN);

        // then
        Assertions.assertTrue(isAuthorized);
    }

    private UserDto getUser(String email, String role) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setId(1L);
        userDto.setName("test");
        userDto.setRole(role);
        return userDto;
    }

    private void mockGetUserInfo(UserDto userDto) {
        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);

        // mocking
        when(restTemplate.exchange(
                Mockito.eq(GATEWAY_HOST + "/api/account/me"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(UserDto.class)
        )).thenReturn(responseEntity);
    }
}
