package mini.minishop.controller;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.dto.user.CreateUserRequest;
import mini.minishop.dto.user.FindUserResponse;
import mini.minishop.error.UserErrorCode;
import mini.minishop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            Long userId = userService.join(request);
            return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FindUserResponse> getUserById(@PathVariable Long userId) {
        Optional<FindUserResponse> findUser = userService.findUser(userId);

        return findUser.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(UserErrorCode.USER_NOT_FOUND.getStatus()));
    }
}
