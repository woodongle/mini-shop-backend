package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        User user = new User();
        user.setName("a");
        user.setEmail("aaa@aaa.com");
        user.setPassword("aaa");
        user.setCreatedDate(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.BEFORE_DELIVERY);
        delivery.setAddress("aaa");

        Delivery savedDelivery = deliveryRepository.save(delivery);

        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED_ORDER);
        order.setCreatedDate(LocalDateTime.now());
        order.setCanceledDate(LocalDateTime.now());
        order.setUser(savedUser);
        order.setDelivery(savedDelivery);

        Order savedOrder = orderRepository.save(order);

        Goods goods = new Goods();
        goods.setName("book");
        goods.setPrice(new BigDecimal(1000));
        goods.setInventoryQuantity(35);
        goods.setCreatedDate(LocalDateTime.now());
        goods.setModifiedDate(LocalDateTime.now());

        Goods savedGoods = goodsRepository.save(goods);

        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setQuantity(35);
        orderGoods.setPaymentAmount(new BigDecimal(35000));
        orderGoods.setOrder(savedOrder);
        orderGoods.setGoods(savedGoods);

        orderGoodsRepository.save(orderGoods);
    }
}