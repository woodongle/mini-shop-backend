package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import mini.minishop.domain.Goods;
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
        Goods goods = new Goods();
        goods.setName("book");
        goods.setPrice(new BigDecimal(1000));
        goods.setInventoryQuantity(100);
        goods.setCreatedDate(LocalDateTime.now());
        goods.setModifiedDate(LocalDateTime.now());

        Goods savedGoods = goodsRepository.save(goods);

        Assertions.assertThat(savedGoods.getName()).isEqualTo(goods.getName());
    }
}