package mini.minishop.config;

import java.math.BigDecimal;
import java.util.List;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.domain.user.UserRole;
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

            User user3 = User.builder()
                    .name("test3")
                    .email("test3@test.com")
                    .password(passwordEncoder.encode("test3"))
                    .role(UserRole.USER)
                    .build();

            userRepository.saveAll(List.of(user1, user2, user3));
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
            User user3 = userRepository.findByEmail("test3@test.com")
                    .orElseThrow(() -> new IllegalStateException("초기화 데이터에 user3가 존재하지 않습니다."));

            Goods goods1 = Goods.builder()
                    .user(user1)
                    .name("goods1")
                    .price(new BigDecimal("1000.00"))
                    .inventoryQuantity(100)
                    .build();

            Goods goods2 = Goods.builder()
                    .user(user1)
                    .name("goods2")
                    .price(new BigDecimal("2000.00"))
                    .inventoryQuantity(200)
                    .build();

            Goods goods3 = Goods.builder()
                    .user(user2)
                    .name("goods3")
                    .price(new BigDecimal("3000.00"))
                    .inventoryQuantity(300)
                    .build();

            Goods goods4 = Goods.builder()
                    .user(user2)
                    .name("goods4")
                    .price(new BigDecimal("4000.00"))
                    .inventoryQuantity(400)
                    .build();

            Goods goods5 = Goods.builder()
                    .user(user3)
                    .name("goods5")
                    .price(new BigDecimal("5000.00"))
                    .inventoryQuantity(500)
                    .build();

            Goods goods6 = Goods.builder()
                    .user(user3)
                    .name("goods6")
                    .price(new BigDecimal("6000.00"))
                    .inventoryQuantity(600)
                    .build();

            goodsRepository.saveAll(List.of(goods1, goods2, goods3, goods4, goods5, goods6));
        };
    }
}
