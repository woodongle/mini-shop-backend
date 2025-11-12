package mini.minishop.api.controller.user;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.ApiResponse;
import mini.minishop.api.controller.user.request.CreateUserRequest;
import mini.minishop.api.controller.user.request.LoginRequest;
import mini.minishop.api.controller.user.request.TokenRefreshRequest;
import mini.minishop.api.service.auth.AuthService;
import mini.minishop.api.service.auth.response.TokenResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @ResponseStatus(CREATED)
    @PostMapping("/signup")
    public ApiResponse<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = userService.createUser(request.toServiceRequest());

        return ApiResponse.created("가입을 축하드립니다. 로그인 화면으로 이동합니다.", null);
    }

    @GetMapping("/{userId}")
    public ApiResponse<FindUserResponse> findUser(@PathVariable Long userId) {
        FindUserResponse response = userService.findUser(userId);

        return ApiResponse.ok(response);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest.toServiceRequest());
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        authService.logout(userEmail);

        return ApiResponse.ok("로그아웃 되었습니다.", null);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return authService.refresh(tokenRefreshRequest);
    }
}
