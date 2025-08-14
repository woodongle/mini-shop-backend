package mini.minishop.api.service.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.controller.user.request.CreateUserRequest;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.domain.user.UserRole;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.user.UserErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createUser(CreateUserRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .role(UserRole.USER)
                .build();

        validateDuplicateEmail(user);
        userRepository.save(user);

        return user.getId();
    }

    private void validateDuplicateEmail(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new BusinessException(UserErrorCode.ALREADY_EXISTS_EMAIL);
                });
    }

    public FindUserResponse findUser(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        return FindUserResponse.of(findUser);
    }

    public User findUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        return findUser.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

}
