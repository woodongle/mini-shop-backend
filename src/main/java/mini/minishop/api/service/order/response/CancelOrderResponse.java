package mini.minishop.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderStatus;

@Getter
@Setter
public class CancelOrderResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderCreatedDate;
    private LocalDateTime orderCanceledDate;
    private List<OrderGoodsDto> orderGoods;

    public CancelOrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderStatus = order.getStatus();
        this.orderCreatedDate = order.getCreatedDate();
        this.orderCanceledDate = order.getCanceledDate();
        this.orderGoods = order.getOrderGoods().stream()
                .map(OrderGoodsDto::new)
                .toList();
    }
}
