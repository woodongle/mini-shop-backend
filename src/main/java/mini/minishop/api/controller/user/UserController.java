package mini.minishop.api.controller.user;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.ApiResponse;
import mini.minishop.api.controller.user.request.CreateUserRequest;
import mini.minishop.api.controller.user.request.LoginRequest;
import mini.minishop.api.controller.user.request.TokenRefreshRequest;
import mini.minishop.api.service.user.RefreshTokenService;
import mini.minishop.api.service.user.UserService;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.api.service.user.response.TokenResponse;
import mini.minishop.config.JwtTokenProvider;
import mini.minishop.domain.user.RefreshToken;
import mini.minishop.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = userService.createUser(request.toServiceRequest());

        return ResponseEntity.status(CREATED).body(ApiResponse.created("가입을 축하드립니다. 로그인 화면으로 이동합니다.", null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<FindUserResponse>> findUser(@PathVariable Long userId) {
        FindUserResponse response = userService.findUser(userId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);
        User user = (User) authentication.getPrincipal();

        refreshTokenService.saveOrUpdate(user.getId(), refreshTokenValue);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshTokenValue));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        refreshTokenService.logout(user);

        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다.", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();
        jwtTokenProvider.validateToken(requestRefreshToken);

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);

        String email = jwtTokenProvider.getEmailFromToken(refreshToken.getToken());

        User findUser = userService.findUser(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(findUser.getEmail(), "",
                        findUser.getAuthorities()),
                null,
                findUser.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);

        refreshToken.updateToken(newRefreshTokenValue);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshTokenValue));
    }
}
