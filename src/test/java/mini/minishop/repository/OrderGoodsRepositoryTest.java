package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import mini.minishop.domain.Delivery;
import mini.minishop.domain.DeliveryStatus;
import mini.minishop.domain.Goods;
import mini.minishop.domain.Order;
import mini.minishop.domain.OrderGoods;
import mini.minishop.domain.OrderStatus;
import mini.minishop.domain.User;
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

        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setQuantity(35);
        orderGoods.setPaymentAmount(new BigDecimal(35000));
        orderGoods.setOrder(savedOrder);
        orderGoods.setGoods(savedGoods);

        orderGoodsRepository.save(orderGoods);
    }
}