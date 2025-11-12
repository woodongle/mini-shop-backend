package mini.minishop.api.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import mini.minishop.api.service.auth.RefreshTokenService;
import mini.minishop.domain.user.RefreshToken;
import mini.minishop.domain.user.RefreshTokenRepository;
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
class RefreshTokenServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 리프레시 토큰을 저장한다.")
    @Transactional
    @Test
    void saveNewRefreshToken() {
        // given
        User user = createUser("user", "user@email.com", "user");
        userRepository.save(user);

        String token = "newToken";

        // when
        refreshTokenService.saveOrUpdate(user.getId(), token);

        // then
        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId()).get();
        assertThat(savedToken.getToken()).isEqualTo(token);
    }

    @DisplayName("기존 리프레시 토큰을 업데이트한다.")
    @Transactional
    @Test
    void updateExistingRefreshToken() {
        // given
        User user = createUser("user", "user@email.com", "user");
        userRepository.save(user);
        refreshTokenRepository.save(new RefreshToken(user, "token"));

        em.flush();
        em.clear();

        String newToken = "newToken";

        // when
        refreshTokenService.saveOrUpdate(user.getId(), newToken);

        // then
        RefreshToken updatedToken = refreshTokenRepository.findByUserId(user.getId()).get();
        assertThat(updatedToken.getToken()).isEqualTo(newToken);
    }

    @DisplayName("존재하지 않는 사용자의 리프레시 토큰을 저장할 경우 예외가 발생한다.")
    @Transactional
    @Test
    void saveRefreshTokenWithNonExistUser() {
        // given
        Long nonExistUserId = 0L;
        String token = "newToken";

        // when // then
        assertThatThrownBy(() -> refreshTokenService.saveOrUpdate(nonExistUserId, token))
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @DisplayName("토큰으로 리프레시 토큰을 조회한다.")
    @Transactional
    @Test
    void findByToken() {
        // given
        User user = createUser("user", "user@email.com", "user");
        userRepository.save(user);
        String token = "token";
        refreshTokenRepository.save(new RefreshToken(user, token));

        // when
        RefreshToken foundToken = refreshTokenService.findByToken(token);

        // then
        assertThat(foundToken.getToken()).isEqualTo(token);
    }

    @DisplayName("존재하지 않는 토큰으로 조회시 예외가 발생한다.")
    @Transactional
    @Test
    void findByNonExistToken() {
        // given
        String nonExistToken = "nonExistToken";

        // when // then
        assertThatThrownBy(() -> refreshTokenService.findByToken(nonExistToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("토큰이 존재하지 않습니다.");
    }


    @DisplayName("로그아웃시 리프레시 토큰을 삭제한다.")
    @Transactional
    @Test
    void logout() {
        // given
        User user = createUser("user", "user@email.com", "user");
        userRepository.save(user);
        refreshTokenRepository.save(new RefreshToken(user, "token"));

        // when
        refreshTokenService.logout(user.getEmail());

        // then
        assertThat(refreshTokenRepository.findByUserId(user.getId())).isEmpty();
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