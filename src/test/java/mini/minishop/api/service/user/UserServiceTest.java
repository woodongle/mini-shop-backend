package mini.minishop.api.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.List;
import mini.minishop.api.service.user.request.CreateUserServiceRequest;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 요청 정보로 사용자 정보를 등록한다. 비밀번호는 인코딩 되어 저장되어야 한다.")
    @Test
    void createUser() {
        // given
        String username = "user";
        String userEmail = "user@user.com";
        String userPassword = "user";
        CreateUserServiceRequest request = CreateUserServiceRequest.builder()
                .name(username)
                .email(userEmail)
                .password(userPassword)
                .build();

        // when
        User createdUser = userService.createUser(request);

        // then
        assertThat(createdUser.getName()).isEqualTo(username);
        assertThat(createdUser.getEmail()).isEqualTo(userEmail);
        assertThat(passwordEncoder.matches(userPassword, createdUser.getPassword())).isTrue();
    }

    @DisplayName("사용자 요청 정보로 사용자 정보를 등록할 때, 이미 존재하는 이메일이 있을 경우 예외가 발생한다.")
    @Transactional
    @Test
    void createUserWithExistEmail() {
        // given
        String userEmail = "user@user.com";
        User user1 = createUser("user1", userEmail, "user1");
        userRepository.save(user1);

        em.flush();
        em.clear();

        CreateUserServiceRequest request = CreateUserServiceRequest.builder()
                .name("user2")
                .email(userEmail)
                .password("user2")
                .build();

        // when // then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @DisplayName("유저 ID로 유저 정보를 조회한다.")
    @Transactional
    @Test
    void findUserWithUserId() {
        // given
        String user1_name = "user1";
        String user1_Email = "user1@user.com";
        User user1 = createUser(user1_name, user1_Email, "user1");
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        em.flush();
        em.clear();

        // when
        FindUserResponse foundUser = userService.findUser(user1.getId());

        // then
        assertThat(foundUser.getUsername()).isEqualTo(user1_name);
        assertThat(foundUser.getUserEmail()).isEqualTo(user1_Email);
    }

    @DisplayName("존재하지 않는 유저 ID로 유저 정보를 조회할 경우, 예외가 발생한다.")
    @Transactional
    @Test
    void findUserWithoutUserId() {
        // given
        User user1 = createUser("user1", "user1@user.com", "user1");
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> {
            long nonExistUserId = 0L;
            userService.findUser(nonExistUserId);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("유저 email로 유저 정보를 조회한다.")
    @Transactional
    @Test
    void findUserWithUserEmail() {
        // given
        String user1_name = "user1";
        String user1_Email = "user1@user.com";
        String user1_Password = "user1";
        User user1 = createUser(user1_name, user1_Email, user1_Password);
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        em.flush();
        em.clear();

        // when
        User foundUser = userService.findUser(user1_Email);

        // then
        assertThat(foundUser.getName()).isEqualTo(user1_name);
        assertThat(foundUser.getEmail()).isEqualTo(user1_Email);
        assertThat(passwordEncoder.matches(user1_Password, foundUser.getPassword())).isTrue();
    }

    @DisplayName("존재하지 않는 유저 email로 유저 정보를 조회할 경우, 예외가 발생한다.")
    @Transactional
    @Test
    void findUserWithoutUserEmail() {
        // given
        User user1 = createUser("user1", "user1@user.com", "user1");
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> {
            String nonExistEmail = "nonExistEmail";
            userService.findUser(nonExistEmail);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
    }
}