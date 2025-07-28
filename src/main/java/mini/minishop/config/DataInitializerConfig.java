package mini.minishop.config;

import java.math.BigDecimal;
import mini.minishop.domain.Goods;
import mini.minishop.domain.User;
import mini.minishop.domain.UserRole;
import mini.minishop.repository.GoodsRepository;
import mini.minishop.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializerConfig {

    @Bean
    @Profile("dev")
    @Order(1)
    public ApplicationRunner userInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User user1 = User.builder()
                    .name("test1")
                    .email("test1@test.com")
                    .password(passwordEncoder.encode("test1"))
                    .role(UserRole.USER)
                    .build();

            User user2 = User.builder()
                    .name("test2")
                    .email("test2@test.com")
                    .password(passwordEncoder.encode("test2"))
                    .role(UserRole.USER)
                    .build();

            userRepository.save(user1);
            userRepository.save(user2);
        };
    }

    @Bean
    @Profile("dev")
    @Order(2)
    public ApplicationRunner GoodsInitializer(UserRepository userRepository, GoodsRepository goodsRepository) {
        return args -> {
            User user1 = userRepository.findByEmail("test1@test.com")
                    .orElseThrow(() -> new IllegalStateException("초기화 데이터에 user1이 존재하지 않습니다."));
            User user2 = userRepository.findByEmail("test2@test.com")
                    .orElseThrow(() -> new IllegalStateException("초기화 데이터에 user2가 존재하지 않습니다."));

            Goods goods1 = Goods.builder()
                    .user(user1)
                    .name("goods1")
                    .price(new BigDecimal("1000.00"))
                    .inventoryQuantity(100)
                    .build();

            Goods goods2 = Goods.builder()
                    .user(user2)
                    .name("goods2")
                    .price(new BigDecimal("2000.00"))
                    .inventoryQuantity(200)
                    .build();

            goodsRepository.save(goods1);
            goodsRepository.save(goods2);
        };
    }
}
