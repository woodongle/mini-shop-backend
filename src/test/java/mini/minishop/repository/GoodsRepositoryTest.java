package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class GoodsRepositoryTest {

    @Autowired
    GoodsRepository goodsRepository;

    @Test
    @Transactional
    @Rollback(false)
    void saveGoods() {
        Goods goods = Goods.builder()
                .name("book")
                .price(new BigDecimal(1000))
                .inventoryQuantity(100)
                .build();

        Goods savedGoods = goodsRepository.save(goods);

        Assertions.assertThat(savedGoods.getName()).isEqualTo(goods.getName());
    }
}