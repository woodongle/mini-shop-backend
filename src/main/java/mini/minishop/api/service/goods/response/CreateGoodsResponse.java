package mini.minishop.api.service.goods.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGoodsResponse {

    private Long goodsId;
    private String goodsName;
    private BigDecimal price;
    private int inventoryQuantity;
    private LocalDateTime createDate;

    @Builder
    public CreateGoodsResponse(Long goodsId, String goodsName, BigDecimal price, int inventoryQuantity,
                               LocalDateTime createDate) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.createDate = createDate;
    }
}
