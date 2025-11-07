package mini.minishop.spring.docs.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import mini.minishop.api.service.user.RefreshTokenService;
import mini.minishop.api.service.user.UserService;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.config.JpaAuditingConfig;
import mini.minishop.config.JwtTokenProvider;
import mini.minishop.domain.user.User;
import mini.minishop.spring.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

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
                .andDo(document("user/user-find-only-one",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("응답 코드"),
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
                                        .description("사용자 이메일")
                        )
                ));
    }
}
