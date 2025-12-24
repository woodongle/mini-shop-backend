package mini.minishop.api.service.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.goods.GoodsService;
import mini.minishop.api.service.order.response.CancelOrderResponse;
import mini.minishop.api.service.order.response.CreateOrderResponse;
import mini.minishop.api.service.order.response.FindOrderHistoryResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryStatus;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderRepository;
import mini.minishop.domain.order.OrderStatus;
import mini.minishop.domain.user.User;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.order.OrderErrorCode;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final GoodsService goodsService;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    @Transactional
    public CreateOrderResponse createOrderWithOptimisticLock(Long goodsId,
                                                             String address,
                                                             User currentUser,
                                                             int orderGoodsQuantity) {
        Goods findGoods = goodsService.findGoodsEntityByGoodsId(goodsId);

        Delivery delivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_DELIVERY)
                .address(address)
                .build();

        Order order = Order.builder()
                .status(OrderStatus.COMPLETED_ORDER)
                .user(currentUser)
                .delivery(delivery)
                .build();

        order.addOrderGoods(findGoods, orderGoodsQuantity);
        Order savedOrder = orderRepository.save(order);

        return new CreateOrderResponse(savedOrder);
    }

    @Recover
    public CreateOrderResponse recover(BusinessException e,
                                       Long goodsId,
                                       String address,
                                       User currentUser,
                                       int orderGoodsQuantity) {
        throw e;
    }

    @Recover
    public CreateOrderResponse recover(ObjectOptimisticLockingFailureException e,
                                       Long goodsId,
                                       String address,
                                       User currentUser,
                                       int orderGoodsQuantity) {
        throw new BusinessException(OrderErrorCode.OPTIMISTIC_LOCK_RETRY_FAILED);
    }

    public List<FindOrderHistoryResponse> findOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findOrderHistoryByUserId(userId);

        return orders.stream()
                .map(FindOrderHistoryResponse::new)
                .toList();
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Long orderId, String userEmail) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        Long currentUserId = userService.findUserByUserEmail(userEmail).getId();

        if (!currentUserId.equals(findOrder.getUser().getId())) {
            throw new BusinessException(OrderErrorCode.NO_PERMISSION_MODIFY_ORDER);
        }

        findOrder.getOrderGoods()
                .forEach(og -> og.getGoods().addInventoryQuantity(og.getQuantity()));
        findOrder.cancel();

        return new CancelOrderResponse(findOrder);
    }
}
