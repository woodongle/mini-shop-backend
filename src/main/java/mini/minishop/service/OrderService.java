package mini.minishop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.Delivery;
import mini.minishop.domain.DeliveryStatus;
import mini.minishop.domain.Goods;
import mini.minishop.domain.Order;
import mini.minishop.domain.OrderGoods;
import mini.minishop.domain.OrderStatus;
import mini.minishop.domain.User;
import mini.minishop.dto.order.CreateOrderRequest;
import mini.minishop.dto.order.FindOrderHistoryResponse;
import mini.minishop.repository.DeliveryRepository;
import mini.minishop.repository.GoodsRepository;
import mini.minishop.repository.OrderGoodsRepository;
import mini.minishop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final GoodsRepository goodsRepository;
    private final OrderGoodsRepository orderGoodsRepository;

    @Transactional
    public Long createOrder(CreateOrderRequest request, User user, Long goodsId) {
        Delivery delivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_DELIVERY)
                .address(request.getAddress())
                .build();
        deliveryRepository.save(delivery);

        Order order = Order.builder()
                .status(OrderStatus.COMPLETED_ORDER)
                .user(user)
                .delivery(delivery)
                .build();
        Order savedOrder = orderRepository.save(order);

        Goods findGoods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 상품입니다."));

        OrderGoods orderGoods = OrderGoods.builder()
                .order(savedOrder)
                .goods(findGoods)
                .quantity(request.getOrderGoodsQuantity())
                .build();
        orderGoodsRepository.save(orderGoods);

        return order.getId();
    }

    public List<FindOrderHistoryResponse> findOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findOrderHistoryByUserId(userId);

        return orders.stream()
                .map(FindOrderHistoryResponse::new)
                .toList();
    }
}
