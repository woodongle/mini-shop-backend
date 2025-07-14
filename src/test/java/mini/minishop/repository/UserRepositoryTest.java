package mini.minishop.repository;

import jakarta.transaction.Transactional;
import mini.minishop.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testUser() {
        User user = User.builder()
                .name("a")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();

        userRepository.save(user);
    }
}