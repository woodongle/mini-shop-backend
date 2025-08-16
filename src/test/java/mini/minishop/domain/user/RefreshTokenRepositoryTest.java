package mini.minishop.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("리프레쉬 토큰 값으로 리프레쉬 토큰 정보를 조회한다. 비어있지 않은 Optional 객체를 반환한다.")
    @Test
    void findByToken() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String tokenValue = "tokenValue";
        RefreshToken refreshToken = new RefreshToken(user, tokenValue);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken(tokenValue);

        // then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo(tokenValue);
    }

    @DisplayName("존재하지 않는 리프레쉬 토큰 값으로 리프레쉬 토큰 정보를 조회할 경우, 비어있는 Optional 객체를 반환한다.")
    @Test
    void findByNonExistToken() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String tokenValue = "tokenValue";
        RefreshToken refreshToken = new RefreshToken(user, tokenValue);
        refreshTokenRepository.save(refreshToken);

        // when
        String nonExistTokenValue = "nonExistTokenValue";
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken(nonExistTokenValue);

        // then
        assertThat(foundToken).isNotPresent();
    }

    @DisplayName("사용자 ID로 리프레쉬 토큰 정보를 조회한다. 비어있지 않은 Optional 객체를 반환한다.")
    @Test
    void findByUserId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String tokenValue = "tokenValue";
        RefreshToken refreshToken = new RefreshToken(user, tokenValue);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByUserId(user.getId());

        // then
        assertThat(foundRefreshToken).isPresent();
        assertThat(foundRefreshToken.get().getToken()).isEqualTo(tokenValue);
    }

    @DisplayName("존재하지 않는 사용자 ID로 리프레쉬 토큰 정보를 조회할 경우, 비어있는 Optional 객체를 반환한다.")
    @Test
    void findByNonExistUserId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String tokenValue = "tokenValue";
        RefreshToken refreshToken = new RefreshToken(user, tokenValue);
        refreshTokenRepository.save(refreshToken);

        // when
        Long nonExistUserId = 2L;
        Optional<RefreshToken> foundToken = refreshTokenRepository.findByUserId(nonExistUserId);

        // then
        assertThat(foundToken).isNotPresent();
    }

    @DisplayName("사용자 ID로 리프레쉬 토큰 정보를 삭제한다.")
    @Transactional
    @Test
    void deleteByUserId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String tokenValue = "tokenValue";
        RefreshToken refreshToken = new RefreshToken(user, tokenValue);
        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteByUserId(user.getId());

        // then
        assertThat(refreshTokenRepository.findByUserId(user.getId())).isNotPresent();
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