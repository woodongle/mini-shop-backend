package mini.minishop.api.service.order.response;

import java.math.BigDecimal;
import lombok.Getter;
import mini.minishop.domain.ordergoods.OrderGoods;

@Getter
public class OrderGoodsResponse {
    private String goodsName;
    private int quantity;
    private BigDecimal paymentAmount;

    public OrderGoodsResponse(OrderGoods orderGoods) {
        this.goodsName = orderGoods.getGoods().getName();
        this.quantity = orderGoods.getQuantity();
        this.paymentAmount = orderGoods.getPaymentAmount();
    }
}
