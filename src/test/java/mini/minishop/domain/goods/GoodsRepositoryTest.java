package mini.minishop.domain.goods;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.math.BigDecimal;
import java.util.List;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class GoodsRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoodsRepository goodsRepository;

    @AfterEach
    void tearDown() {
        goodsRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("입력받은 값이 포함되어 있는 상품들을 조회한다.")
    @Test
    void findByNameContaining() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        BigDecimal goods1_price = new BigDecimal("1000.00");
        BigDecimal goods2_price = new BigDecimal("2000.00");
        int goods1_inventoryQuantity = 10;
        int goods2_inventoryQuantity = 20;
        Goods goods1 = createGoods("goods1", goods1_price, goods1_inventoryQuantity, user);
        Goods goods2 = createGoods("goods2", goods2_price, goods2_inventoryQuantity, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        // when
        List<FindGoodsResponse> foundGoods = goodsRepository.findByNameContaining("goods", null).getContent();

        // then
        assertThat(foundGoods).hasSize(2);
        assertThat(foundGoods)
                .extracting("name", "price", "inventoryQuantity")
                .containsExactlyInAnyOrder(
                        tuple("goods1", goods1_price, goods1_inventoryQuantity),
                        tuple("goods2", goods2_price, goods2_inventoryQuantity)
                );
    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
    }

    private Goods createGoods(String name, BigDecimal price, int inventoryQuantity, User user) {
        return Goods.builder()
                .name(name)
                .price(price)
                .inventoryQuantity(inventoryQuantity)
                .user(user)
                .build();
    }
}