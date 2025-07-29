package mini.minishop.dto.order;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import mini.minishop.domain.Order;
import mini.minishop.domain.OrderStatus;

@Getter
@Setter
public class FindOrderHistoryResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private List<OrderGoodsDto> orderGoods;

    public FindOrderHistoryResponse(Order order) {
        this.orderId = order.getId();
        this.orderStatus = order.getStatus();
        this.orderDate = order.getCreatedDate();
        this.orderGoods = order.getOrderGoods().stream()
                .map(OrderGoodsDto::new)
                .toList();
    }
}
