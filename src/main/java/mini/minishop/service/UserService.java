package mini.minishop.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.User;
import mini.minishop.domain.UserRole;
import mini.minishop.dto.user.CreateUserRequest;
import mini.minishop.dto.user.FindUserResponse;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.user.UserErrorCode;
import mini.minishop.repository.UserRepository;
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

    public Optional<FindUserResponse> findUser(Long userId) {
        return userRepository.findById(userId)
                .map(FindUserResponse::of);
    }

    public User findUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        return findUser.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

}
