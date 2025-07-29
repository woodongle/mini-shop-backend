package mini.minishop.dto.goods;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.Goods;

@Getter
@Setter
public class UpdateGoodsResponse {
    private Long goodsId;
    private String goodsName;
    private BigDecimal price;
    private int inventoryQuantity;
    private LocalDateTime modifiedDate;

    public UpdateGoodsResponse(Goods goods) {
        this.goodsId = goods.getId();
        this.goodsName = goods.getName();
        this.price = goods.getPrice();
        this.inventoryQuantity = goods.getInventoryQuantity();
        this.modifiedDate = goods.getModifiedDate();
    }
}
