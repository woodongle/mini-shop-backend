package mini.minishop.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderStatus;

@Getter
@Setter
public class CancelOrderResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderedDate;
    private LocalDateTime canceledDate;
    private List<OrderGoodsResponse> orderGoods;

    public CancelOrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderStatus = order.getStatus();
        this.orderedDate = order.getCreatedDate();
        this.canceledDate = order.getCanceledDate();
        this.orderGoods = order.getOrderGoods().stream()
                .map(OrderGoodsResponse::new)
                .toList();
    }

    @Builder
    public CancelOrderResponse(Long orderId, OrderStatus orderStatus, LocalDateTime orderedDate,
                               LocalDateTime canceledDate,
                               List<OrderGoodsResponse> orderGoods) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderedDate = orderedDate;
        this.canceledDate = canceledDate;
        this.orderGoods = orderGoods;
    }
}
