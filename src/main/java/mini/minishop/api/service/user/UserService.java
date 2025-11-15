package mini.minishop.api.service.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.user.request.CreateUserServiceRequest;
import mini.minishop.api.service.user.response.FindUserResponse;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
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
    public User createUser(CreateUserServiceRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encryptedPassword);

        validateDuplicateEmail(user);
        userRepository.save(user);

        return user;
    }

    private void validateDuplicateEmail(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new BusinessException(UserErrorCode.ALREADY_EXISTS_EMAIL);
                });
    }

    public FindUserResponse findUser(Long userId) {
        return FindUserResponse.of(findUserEntityByUserId(userId));
    }

    public User findUserEntityByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    public User findUserByUserEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        return findUser.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

}
