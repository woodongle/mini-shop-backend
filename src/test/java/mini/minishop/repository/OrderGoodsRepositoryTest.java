package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryRepository;
import mini.minishop.domain.delivery.DeliveryStatus;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderRepository;
import mini.minishop.domain.order.OrderStatus;
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.ordergoods.OrderGoodsRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class OrderGoodsRepositoryTest {

    @Autowired
    OrderGoodsRepository orderGoodsRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Test
    @Transactional
    @Rollback(false)
    void saveOrderGoods() {
        User user = User.builder()
                .name("a")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();

        User savedUser = userRepository.save(user);

        Delivery delivery = Delivery.builder()
                .status(DeliveryStatus.BEFORE_DELIVERY)
                .address("aaa")
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);

        Order order = Order.builder()
                .status(OrderStatus.COMPLETED_ORDER)
                .user(savedUser)
                .delivery(savedDelivery)
                .build();

        Order savedOrder = orderRepository.save(order);

        Goods goods = Goods.builder()
                .name("book")
                .price(new BigDecimal(1000))
                .inventoryQuantity(100)
                .build();

        Goods savedGoods = goodsRepository.save(goods);

        OrderGoods orderGoods = OrderGoods.builder()
                .order(savedOrder)
                .goods(savedGoods)
                .quantity(35)
                .build();

        orderGoodsRepository.save(orderGoods);
    }
}