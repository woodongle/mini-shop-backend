package mini.minishop.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("이메일로 사용자 정보를 조회한다. 비어있지 않은 Optional 객체를 반환한다.")
    @Test
    void findByEmail() {
        // given
        User user1 = createUser("user1", "user1@user.com", "user1");
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        // when
        Optional<User> findUser = userRepository.findByEmail("user1@user.com");

        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get())
                .extracting("name", "email")
                .containsExactlyInAnyOrder("user1", "user1@user.com");
    }

    @DisplayName("존재하지 않는 이메일로 사용자 정보를 조회하는 경우, 비어있는 Optional 객체를 반환한다.")
    @Test
    void findByNonExistEmail() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByEmail("nonExistEmail");

        // then
        assertThat(findUser).isNotPresent();
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