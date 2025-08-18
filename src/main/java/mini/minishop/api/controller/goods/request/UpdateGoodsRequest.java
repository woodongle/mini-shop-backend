package mini.minishop.api.controller.goods.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGoodsRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String goodsName;

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    @Positive(message = "재고 수량은 양수여야 합니다.")
    private int inventoryQuantity;

    @Builder
    public UpdateGoodsRequest(String goodsName, BigDecimal price, int inventoryQuantity) {
        this.goodsName = goodsName;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }
}
