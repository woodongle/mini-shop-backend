package mini.minishop.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderStatus;

@Getter
@Setter
public class FindOrderHistoryResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderedDate;
    private LocalDateTime canceledOrderDate;
    private List<OrderGoodsResponse> orderGoods;

    public FindOrderHistoryResponse(Order order) {
        this.orderId = order.getId();
        this.orderStatus = order.getStatus();
        this.orderedDate = order.getCreatedDate();
        this.canceledOrderDate = order.getCanceledDate();
        this.orderGoods = order.getOrderGoods().stream()
                .map(OrderGoodsResponse::new)
                .toList();
    }
}
