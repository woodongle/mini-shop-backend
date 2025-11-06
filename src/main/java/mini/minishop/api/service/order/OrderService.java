package mini.minishop.api.service.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.order.request.CreateOrderServiceRequest;
import mini.minishop.api.service.order.response.CancelOrderResponse;
import mini.minishop.api.service.order.response.CreateOrderResponse;
import mini.minishop.api.service.order.response.FindOrderHistoryResponse;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryStatus;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderRepository;
import mini.minishop.domain.order.OrderStatus;
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.user.User;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.goods.GoodsErrorCode;
import mini.minishop.exception.order.OrderErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final GoodsRepository goodsRepository;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderServiceRequest request, User user, Long goodsId) {
        Delivery delivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_DELIVERY)
                .address(request.getAddress())
                .build();

        Order order = Order.builder()
                .status(OrderStatus.COMPLETED_ORDER)
                .user(user)
                .delivery(delivery)
                .build();

        Goods findGoods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new BusinessException(GoodsErrorCode.GOODS_NOT_FOUND));

        OrderGoods orderGoods = OrderGoods.builder()
                .order(order)
                .goods(findGoods)
                .quantity(request.getOrderGoodsQuantity())
                .build();

        Order savedOrder = orderRepository.save(order);

        return new CreateOrderResponse(savedOrder);
    }

    public List<FindOrderHistoryResponse> findOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findOrderHistoryByUserId(userId);

        return orders.stream()
                .map(FindOrderHistoryResponse::new)
                .toList();
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Long orderId, Long userId) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (!userId.equals(findOrder.getUser().getId())) {
            throw new BusinessException(OrderErrorCode.NO_PERMISSION_MODIFY_ORDER);
        }

        findOrder.getOrderGoods()
                .forEach(og -> og.getGoods().addInventoryQuantity(og.getQuantity()));
        findOrder.cancel();

        return new CancelOrderResponse(findOrder);
    }
}
