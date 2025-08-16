package mini.minishop.domain.order;

import static mini.minishop.domain.delivery.DeliveryStatus.BEFORE_DELIVERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryRepository;
import mini.minishop.domain.delivery.DeliveryStatus;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.ordergoods.OrderGoodsRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("사용자 ID로 주문 내역을 조회한다.")
    @Transactional
    @Test
    void findOrderHistoryByUserId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        Delivery delivery1 = createDelivery(BEFORE_DELIVERY, "서울시");
        Delivery delivery2 = createDelivery(BEFORE_DELIVERY, "서울시");
        deliveryRepository.saveAll(List.of(delivery1, delivery2));

        Order order1 = createOrder(user, delivery1);
        Order order2 = createOrder(user, delivery2);
        orderRepository.saveAll(List.of(order1, order2));

        OrderGoods orderGoods1 = createOrderGoods(2, order1, goods);
        OrderGoods orderGoods2 = createOrderGoods(3, order2, goods);
        orderGoodsRepository.saveAll(List.of(orderGoods1, orderGoods2));

        em.flush();
        em.clear();

        // when
        List<Order> foundOrders = orderRepository.findOrderHistoryByUserId(user.getId());

        // then
        assertThat(foundOrders).hasSize(2);
        assertThat(foundOrders)
                .extracting(Order::getUser)
                .allSatisfy(foundUser -> {
                    assertThat(foundUser.getName()).isEqualTo("user");
                    assertThat(foundUser.getEmail()).isEqualTo("user@user.com");
                });

        assertThat(foundOrders)
                .extracting(Order::getDelivery)
                .extracting("status", "address")
                .containsExactlyInAnyOrder(
                        tuple(BEFORE_DELIVERY, "서울시"),
                        tuple(BEFORE_DELIVERY, "서울시")
                );
    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
    }

    private Goods createGoods(String name, BigDecimal price, int inventoryQuantity, User user) {
        return Goods.builder()
                .name(name)
                .price(price)
                .inventoryQuantity(inventoryQuantity)
                .user(user)
                .build();
    }

    private Delivery createDelivery(DeliveryStatus status, String address) {
        return Delivery.builder()
                .status(status)
                .address(address)
                .build();
    }

    private Order createOrder(User user, Delivery delivery) {
        return Order.builder()
                .status(OrderStatus.COMPLETED_ORDER)
                .user(user)
                .delivery(delivery)
                .build();
    }

    private OrderGoods createOrderGoods(int quantity, Order order, Goods goods) {
        return OrderGoods.builder()
                .quantity(quantity)
                .order(order)
                .goods(goods)
                .build();
    }
}