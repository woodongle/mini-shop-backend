package mini.minishop.repository;

import jakarta.transaction.Transactional;
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

        orderRepository.save(order);
    }
}