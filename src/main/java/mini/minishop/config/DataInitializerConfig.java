package mini.minishop.config;

import java.math.BigDecimal;
import java.util.ArrayList;
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

            List<User> users = List.of(user1, user2, user3);
            List<Goods> goodsList = new ArrayList<>();

            for (int i = 1; i <= 1000; i++) {
                User selectedUser = users.get(i % 3); // 0,1,2 반복

                Goods goods = Goods.builder()
                        .user(selectedUser)
                        .name("테스트상품" + String.format("%04d", i))
                        .price(new BigDecimal(String.format("%d.%02d", 1000 + i, i % 100)))
                        .inventoryQuantity((100 - (i % 100)))  // 100 → 0 반복
                        .build();

                goodsList.add(goods);
            }

            goodsRepository.saveAll(goodsList);
        };
    }
}
