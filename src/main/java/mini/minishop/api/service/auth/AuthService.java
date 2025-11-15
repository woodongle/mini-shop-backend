package mini.minishop.api.service.auth;

import lombok.RequiredArgsConstructor;
import mini.minishop.api.controller.user.request.TokenRefreshRequest;
import mini.minishop.api.service.auth.request.LoginServiceRequest;
import mini.minishop.api.service.auth.response.TokenResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.config.JwtTokenProvider;
import mini.minishop.domain.user.RefreshToken;
import mini.minishop.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenResponse login(LoginServiceRequest loginServiceRequest) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(loginServiceRequest.getEmail(),
                        loginServiceRequest.getPassword())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);
        User user = (User) authentication.getPrincipal();

        refreshTokenService.saveOrUpdate(user.getId(), refreshTokenValue);

        return new TokenResponse(accessToken, refreshTokenValue);
    }

    public void logout(String userEmail) {
        refreshTokenService.logout(userEmail);
    }

    public TokenResponse refresh(TokenRefreshRequest tokenRefreshRequest) {
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();
        jwtTokenProvider.validateToken(requestRefreshToken);

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);

        String email = jwtTokenProvider.getEmailFromToken(refreshToken.getToken());

        User findUser = userService.findUserByUserEmail(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(findUser.getEmail(), "",
                        findUser.getAuthorities()),
                null,
                findUser.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);

        refreshToken.updateToken(newRefreshTokenValue);

        return new TokenResponse(newAccessToken, newRefreshTokenValue);
    }
}
