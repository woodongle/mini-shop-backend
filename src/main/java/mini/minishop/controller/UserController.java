package mini.minishop.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.config.JwtTokenProvider;
import mini.minishop.domain.RefreshToken;
import mini.minishop.domain.User;
import mini.minishop.dto.user.CreateUserRequest;
import mini.minishop.dto.user.FindUserResponse;
import mini.minishop.dto.user.LoginRequest;
import mini.minishop.dto.user.TokenDto;
import mini.minishop.dto.user.TokenRefreshRequest;
import mini.minishop.exception.error.UserErrorCode;
import mini.minishop.service.RefreshTokenService;
import mini.minishop.service.UserService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            Long userId = userService.createUser(request);
            return new ResponseEntity<>("가입을 축하드립니다.", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FindUserResponse> findUser(@PathVariable Long userId) {
        Optional<FindUserResponse> findUser = userService.findUser(userId);

        return findUser.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(UserErrorCode.USER_NOT_FOUND.getStatus()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginRequest LoginRequest) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(LoginRequest.getEmail(), LoginRequest.getPassword())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(authentication);
        User user = (User) authentication.getPrincipal();

        refreshTokenService.saveOrUpdate(user.getId(), refreshTokenValue);

        return ResponseEntity.ok(new TokenDto(accessToken, refreshTokenValue));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        refreshTokenService.logout(user);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenRefreshRequest TokenRefreshRequest) {
        String requestRefreshToken = TokenRefreshRequest.getRefreshToken();
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

        return ResponseEntity.ok(new TokenDto(newAccessToken, newRefreshTokenValue));
    }
}
