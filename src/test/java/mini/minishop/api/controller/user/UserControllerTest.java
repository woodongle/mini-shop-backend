package mini.minishop.api.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import mini.minishop.api.controller.user.request.CreateUserRequest;
import mini.minishop.api.controller.user.request.LoginRequest;
import mini.minishop.domain.user.RefreshTokenRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.domain.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 입력으로 회원을 생성한다.")
    @Test
    void createUser() throws Exception {
        // given
        CreateUserRequest request = createUserRequest("user", "user@user.com", "user");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("가입을 축하드립니다. 로그인 화면으로 이동합니다."));
    }

    @DisplayName("사용자 입력에 문제가 있으면 예외가 발생한다.")
    @Test
    void createUserWithInvalidRequest() throws Exception {
        // given
        CreateUserRequest request = createUserRequest("", "user@user.com", "user");

        // when // then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.code").value("400"))
                .andExpect(jsonPath("$.data.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data.message").value("이름은 필수입니다."));
    }

    @DisplayName("사용자 ID로 사용자 정보를 조회한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void findUser() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        em.flush();
        em.clear();

        Long userId = user.getId();

        // when // then
        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("user"))
                .andExpect(jsonPath("$.data.userEmail").value("user@user.com"));
    }

    @DisplayName("사용자 입력으로 서비스에 로그인한다.")
    @Transactional
    @Test
    void login() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        em.flush();
        em.clear();

        LoginRequest request = new LoginRequest();
        request.setEmail("user@user.com");
        request.setPassword("user");

        // when // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("사용자 입력으로 서비스에 로그인한다.")
    @Transactional
    @Test
    void loginWithInvalidRequest() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        em.flush();
        em.clear();

        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("user");

        // when // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.code").value("400"))
                .andExpect(jsonPath("$.data.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data.message").value("이메일은 필수입니다."));
    }

    @DisplayName("로그인된 사용자가 로그아웃한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void logout() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        em.flush();
        em.clear();

        // when // then
        mockMvc.perform(post("/api/v1/users/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));
    }

    private CreateUserRequest createUserRequest(String name, String email, String password) {
        return CreateUserRequest.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();
    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();
    }
}