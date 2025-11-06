package mini.minishop.api.service.goods.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.goods.Goods;

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

    @Builder
    public UpdateGoodsResponse(Long goodsId, String goodsName, BigDecimal price, int inventoryQuantity,
                               LocalDateTime modifiedDate) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.modifiedDate = modifiedDate;
    }
}
