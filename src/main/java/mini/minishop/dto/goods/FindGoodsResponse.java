package mini.minishop.dto.goods;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.Goods;

@Getter
@Setter
public class FindGoodsResponse {
    private String name;
    private BigDecimal price;
    private int inventoryQuantity;
    private LocalDateTime modifiedDate;

    public static FindGoodsResponse of(Goods goods) {
        return new FindGoodsResponse(goods.getName(), goods.getPrice(), goods.getInventoryQuantity(),
                goods.getModifiedDate());
    }

    private FindGoodsResponse(String name, BigDecimal price, int inventoryQuantity, LocalDateTime modifiedDate) {
        this.name = name;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.modifiedDate = modifiedDate;
    }
}
