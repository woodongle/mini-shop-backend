package mini.minishop.api.controller.goods.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.api.service.goods.request.CreateGoodsServiceRequest;

@Getter
@Setter
public class CreateGoodsRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    @Positive(message = "재고 수량은 양수여야 합니다.")
    private int inventoryQuantity;

    @Builder
    public CreateGoodsRequest(String name, BigDecimal price, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }

    public CreateGoodsServiceRequest toServiceRequest() {
        return CreateGoodsServiceRequest.builder()
                .name(name)
                .price(price)
                .inventoryQuantity(inventoryQuantity)
                .build();
    }
}
