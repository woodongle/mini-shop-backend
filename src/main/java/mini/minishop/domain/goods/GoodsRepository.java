package mini.minishop.domain.goods;

import mini.minishop.api.service.goods.response.FindGoodsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query("""
            select new mini.minishop.api.service.goods.response.FindGoodsResponse(
                g.id,
                g.name,
                g.price,
                g.inventoryQuantity,
                g.modifiedDate
            )
            from Goods g
            where g.name like %:name%
            """)
    Page<FindGoodsResponse> findByNameContaining(@Param("name") String name,
                                                 Pageable pageable);
}
