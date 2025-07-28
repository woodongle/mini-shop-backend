package mini.minishop.repository;

import java.util.List;
import mini.minishop.domain.Goods;
import mini.minishop.dto.goods.FindGoodsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    List<FindGoodsResponse> findByNameContaining(String name);
}
