package mini.minishop.api.service.goods.request;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGoodsServiceRequest {

    private String goodsName;
    private BigDecimal price;
    private int inventoryQuantity;

    @Builder
    public UpdateGoodsServiceRequest(String goodsName, BigDecimal price, int inventoryQuantity) {
        this.goodsName = goodsName;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }
}
