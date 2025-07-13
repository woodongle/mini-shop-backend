package mini.minishop.repository;

import mini.minishop.domain.OrderGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderGoodsRepository extends JpaRepository<OrderGoods, Long> {
}
