package mini.minishop.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.order.Order;

@Getter
@Setter
public class CreateOrderResponse {
    private Long orderId;
    private LocalDateTime orderedDate;
    private List<OrderGoodsResponse> orderGoods;

    public CreateOrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderedDate = order.getCreatedDate();
        this.orderGoods = order.getOrderGoods().stream()
                .map(OrderGoodsResponse::new)
                .toList();
    }

    @Builder
    public CreateOrderResponse(Long orderId, LocalDateTime orderedDate, List<OrderGoodsResponse> orderGoods) {
        this.orderId = orderId;
        this.orderedDate = orderedDate;
        this.orderGoods = orderGoods;
    }
}

