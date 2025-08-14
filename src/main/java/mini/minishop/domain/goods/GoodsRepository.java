package mini.minishop.domain.goods;

import java.util.List;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    List<FindGoodsResponse> findByNameContaining(String name);
}
