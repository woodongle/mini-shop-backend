package mini.minishop.api.service.auth;

import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.user.RefreshToken;
import mini.minishop.domain.user.RefreshTokenRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.user.UserErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void saveOrUpdate(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.updateToken(token),
                        () -> refreshTokenRepository.save(new RefreshToken(user, token))
                );
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));
    }

    public void logout(String userEmail) {
        Long currentUserId = userService.findUser(userEmail).getId();
        refreshTokenRepository.deleteByUserId(currentUserId);
    }
}
