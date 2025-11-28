package mini.minishop.spring.docs.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mini.minishop.api.controller.user.UserController;
import mini.minishop.api.controller.user.request.CreateUserRequest;
import mini.minishop.api.controller.user.request.LoginRequest;
import mini.minishop.api.controller.user.request.TokenRefreshRequest;
import mini.minishop.api.service.auth.AuthService;
import mini.minishop.api.service.auth.RefreshTokenService;
import mini.minishop.api.service.auth.request.LoginServiceRequest;
import mini.minishop.api.service.auth.response.TokenResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.config.JpaAuditingConfig;
import mini.minishop.config.JwtTokenProvider;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRole;
import mini.minishop.spring.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(
        value = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JpaAuditingConfig.class
                )
        })
public class UserControllerDocsTest extends RestDocsSupport {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private AuthService authService;

    @DisplayName("회원을 등록하는 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void createUser() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("user")
                .email("user@user.com")
                .password("user")
                .build();

        given(userService.createUser(any()))
                .willReturn(User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .build());

        mockMvc.perform(post("/api/v1/users/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("가입을 축하드립니다. 로그인 화면으로 이동합니다."))
                .andDo(document("user/user-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("email").type(STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("password").type(STRING)
                                        .description("비밀번호")
                        ),
                        responseBody()
                ));
    }

    @DisplayName("특정 사용자를 조회하는 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void findUser() throws Exception {
        Long mockUserId = 1L;
        given(userService.findUser(mockUserId))
                .willReturn(FindUserResponse.builder()
                        .userId(mockUserId)
                        .username("user")
                        .userEmail("user@user.com")
                        .build());

        mockMvc.perform(get("/api/v1/users/{userId}", mockUserId)
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(document("user/user-find-by-user-id",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.userId").type(NUMBER)
                                        .description("사용자 ID"),
                                fieldWithPath("data.username").type(STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.userEmail").type(STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("code").type(NUMBER)
                                        .description("응답 코드")
                        )
                ));
    }

    @DisplayName("로그인 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void login() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("user@user.com")
                .password("user")
                .build();

        String accessToken = "test_access_token";
        String refreshToken = "test_refresh_token";

        given(authService.login(any(LoginServiceRequest.class)))
                .willReturn(new TokenResponse(accessToken, refreshToken));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user/user-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("password").type(STRING)
                                        .description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(STRING)
                                        .description("액세스 토큰"),
                                fieldWithPath("refreshToken").type(STRING)
                                        .description("리프레시 토큰")
                        )
                ));
    }

    @DisplayName("로그아웃 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void logout() throws Exception {
        User mockUser = User.builder()
                .email("user@user.com")
                .role(UserRole.USER)
                .build();

        given(userService.findUserByUserEmail(mockUser.getEmail()))
                .willReturn(mockUser);
        doNothing().when(refreshTokenService).logout(mockUser.getEmail());

        mockMvc.perform(post("/api/v1/users/logout")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."))
                .andDo(document("user/user-logout",
                        preprocessResponse(prettyPrint()),
                        responseBody()
                ));
    }

    @DisplayName("리프레쉬 토큰으로 액세스 토큰과 리프레쉬 토큰을 재발급하는 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void refresh() throws Exception {
        String requestRefreshToken = "request_refresh_token";
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";

        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(requestRefreshToken);

        given(authService.refresh(any(TokenRefreshRequest.class)))
                .willReturn(new TokenResponse(newAccessToken, newRefreshToken));

        mockMvc.perform(post("/api/v1/users/refresh")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user/user-refresh",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("refreshToken").type(STRING)
                                        .description("기존 리프레쉬 토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(STRING)
                                        .description("새로운 액세스 토큰"),
                                fieldWithPath("refreshToken").type(STRING)
                                        .description("새로운 리프레쉬 토큰")
                        )
                ));

    }


}
