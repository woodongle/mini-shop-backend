package mini.minishop.domain.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o "
            + "join fetch o.orderGoods og "
            + "join fetch og.goods "
            + "where o.user.id = :userId "
            + "order by o.createdDate ")
    List<Order> findOrderHistoryByUserId(@Param("userId") Long userId);
}
