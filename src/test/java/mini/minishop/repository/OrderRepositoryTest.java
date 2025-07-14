package mini.minishop.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import mini.minishop.domain.Delivery;
import mini.minishop.domain.DeliveryStatus;
import mini.minishop.domain.Order;
import mini.minishop.domain.OrderStatus;
import mini.minishop.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    @Transactional
    @Rollback(false)
    void saveOrder() {
        User user = new User();
        user.setName("a");
        user.setEmail("aaa@aaa.com");
        user.setPassword("aaa");

        User savedUser = userRepository.save(user);

        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.BEFORE_DELIVERY);
        delivery.setAddress("aaa");

        Delivery savedDelivery = deliveryRepository.save(delivery);

        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED_ORDER);
        order.setCanceledDate(LocalDateTime.now());
        order.setUser(savedUser);
        order.setDelivery(savedDelivery);

        orderRepository.save(order);
    }
}