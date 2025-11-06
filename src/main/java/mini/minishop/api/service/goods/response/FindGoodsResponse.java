package mini.minishop.api.service.goods.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.goods.Goods;

@Getter
@Setter
public class FindGoodsResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int inventoryQuantity;
    private LocalDateTime modifiedDate;

    public static FindGoodsResponse of(Goods goods) {
        return new FindGoodsResponse(goods.getId(), goods.getName(), goods.getPrice(), goods.getInventoryQuantity(),
                goods.getModifiedDate());
    }

    @Builder
    public FindGoodsResponse(Long id, String name, BigDecimal price, int inventoryQuantity,
                             LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.modifiedDate = modifiedDate;
    }
}
