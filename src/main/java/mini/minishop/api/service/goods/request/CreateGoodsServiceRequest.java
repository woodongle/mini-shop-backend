package mini.minishop.api.service.goods.request;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.goods.Goods;

@Getter
@Setter
public class CreateGoodsServiceRequest {

    private String name;
    private BigDecimal price;
    private int inventoryQuantity;

    @Builder
    public CreateGoodsServiceRequest(String name, BigDecimal price, int inventoryQuantity) {
        this.name = name;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
    }

    public Goods toEntity() {
        return Goods.builder()
                .name(name)
                .price(price)
                .inventoryQuantity(inventoryQuantity)
                .build();
    }
}
