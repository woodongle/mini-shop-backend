package mini.minishop.domain.ordergoods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderGoodsRepository extends JpaRepository<OrderGoods, Long> {
}
