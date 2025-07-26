package mini.minishop.service;

import lombok.RequiredArgsConstructor;
import mini.minishop.domain.RefreshToken;
import mini.minishop.domain.User;
import mini.minishop.error.UserErrorCode;
import mini.minishop.repository.RefreshTokenRepository;
import mini.minishop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public void saveOrUpdate(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(UserErrorCode.USER_NOT_FOUND.getMessage()));

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(token),
                        () -> refreshTokenRepository.save(new RefreshToken(user, token))
                );
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));
    }
}
