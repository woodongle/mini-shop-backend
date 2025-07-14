package mini.minishop.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.User;
import mini.minishop.dto.user.CreateUserRequest;
import mini.minishop.dto.user.FindUserResponse;
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
    public Long join(CreateUserRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .build();

        validateDuplicateEmail(user);
        userRepository.save(user);

        return user.getId();
    }

    private void validateDuplicateEmail(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 이메일입니다.");
                });
    }

    public Optional<FindUserResponse> findUser(Long userId) {
        return userRepository.findById(userId)
                .map(FindUserResponse::new);
    }

}
