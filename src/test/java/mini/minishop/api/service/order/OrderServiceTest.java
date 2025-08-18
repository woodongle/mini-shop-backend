package mini.minishop.api.service.order;

import static mini.minishop.domain.delivery.DeliveryStatus.BEFORE_DELIVERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import mini.minishop.api.controller.order.request.CreateOrderRequest;
import mini.minishop.api.service.order.response.CancelOrderResponse;
import mini.minishop.api.service.order.response.FindOrderHistoryResponse;
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
import mini.minishop.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        orderGoodsRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
        goodsRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 요청 정보로 주문을 생성한다.")
    @Transactional
    @Test
    void createOrder() {
        // given
        String userName = "user";
        String userEmail = "user@user.com";
        User user = createUser(userName, userEmail, "user");
        userRepository.save(user);

        String goodsName = "goods";
        BigDecimal goodsPrice = new BigDecimal("1000.00");
        Goods goods = createGoods(goodsName, goodsPrice, 10, user);
        goodsRepository.save(goods);

        String deliveryAddress = "서울시";
        int orderGoodsQuantity = 2;
        CreateOrderRequest request = CreateOrderRequest.builder()
                .address(deliveryAddress)
                .orderGoodsQuantity(orderGoodsQuantity)
                .build();

        em.flush();
        em.clear();

        // when
        Order createdOrder = orderService.createOrder(request, user, goods.getId());

        // then
        assertThat(createdOrder.getStatus()).isEqualByComparingTo(OrderStatus.COMPLETED_ORDER);
        assertThat(createdOrder.getUser())
                .extracting("name", "email")
                .containsExactlyInAnyOrder(userName, userEmail);
        assertThat(createdOrder.getDelivery())
                .extracting("status", "address")
                .containsExactlyInAnyOrder(DeliveryStatus.BEFORE_DELIVERY, deliveryAddress);
        assertThat(createdOrder.getOrderGoods().getFirst())
                .extracting("quantity", "paymentAmount")
                .containsExactlyInAnyOrder(orderGoodsQuantity, goodsPrice.multiply(new BigDecimal(orderGoodsQuantity)));
    }

    @DisplayName("존재하지 않는 상품 ID로 주문을 생성할 경우, 예외가 발생한다.")
    @Test
    void createOrderWithoutGoodsId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        CreateOrderRequest request = CreateOrderRequest.builder()
                .address("서울시")
                .orderGoodsQuantity(2)
                .build();

        // when // then
        assertThatThrownBy(() -> orderService.createOrder(request, user, 0L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @DisplayName("사용자 ID로 주문 내역을 리스트로 반환한다.")
    @Transactional
    @Test
    void findOrderHistory() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String goodsName = "goods";
        BigDecimal goodsPrice = new BigDecimal("1000.00");
        int goodsInventoryQuantity = 10;
        Goods goods = createGoods(goodsName, goodsPrice, goodsInventoryQuantity, user);
        goodsRepository.save(goods);

        Delivery delivery1 = createDelivery(BEFORE_DELIVERY, "서울시");
        Delivery delivery2 = createDelivery(BEFORE_DELIVERY, "서울시");
        deliveryRepository.saveAll(List.of(delivery1, delivery2));

        Order order1 = createOrder(user, delivery1);
        Order order2 = createOrder(user, delivery2);
        orderRepository.saveAll(List.of(order1, order2));

        int orderGoods1_quantity = 2;
        int orderGoods2_quantity = 3;
        OrderGoods orderGoods1 = createOrderGoods(orderGoods1_quantity, order1, goods);
        OrderGoods orderGoods2 = createOrderGoods(orderGoods2_quantity, order2, goods);
        orderGoodsRepository.saveAll(List.of(orderGoods1, orderGoods2));

        em.flush();
        em.clear();

        // when
        List<FindOrderHistoryResponse> foundOrderHistory = orderService.findOrderHistory(user.getId());

        // then
        assertThat(foundOrderHistory)
                .extracting(FindOrderHistoryResponse::getOrderStatus)
                .containsExactlyInAnyOrder(
                        OrderStatus.COMPLETED_ORDER,
                        OrderStatus.COMPLETED_ORDER
                );
        assertThat(foundOrderHistory)
                .hasSize(2)
                .anySatisfy(response -> {
                    assertThat(response.getOrderGoods())
                            .extracting("goodsName", "quantity", "paymentAmount")
                            .containsExactlyInAnyOrder(
                                    tuple(goodsName, orderGoods1_quantity,
                                            goodsPrice.multiply(new BigDecimal(orderGoods1_quantity)))
                            );
                })
                .anySatisfy(response -> {
                    assertThat(response.getOrderGoods())
                            .extracting("goodsName", "quantity", "paymentAmount")
                            .containsExactlyInAnyOrder(
                                    tuple(goodsName, orderGoods2_quantity,
                                            goodsPrice.multiply(new BigDecimal(orderGoods2_quantity)))
                            );
                });
    }

    @DisplayName("사용자 요청 정보로 주문을 취소한다.")
    @Transactional
    @Test
    void cancelOrder() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String goodsName = "goods";
        BigDecimal goodsPrice = new BigDecimal("1000.00");
        int goodsInventoryQuantity = 10;
        Goods goods = createGoods(goodsName, goodsPrice, goodsInventoryQuantity, user);
        goodsRepository.save(goods);

        Delivery delivery1 = createDelivery(BEFORE_DELIVERY, "서울시");
        Delivery delivery2 = createDelivery(BEFORE_DELIVERY, "서울시");
        deliveryRepository.saveAll(List.of(delivery1, delivery2));

        Order order1 = createOrder(user, delivery1);
        Order order2 = createOrder(user, delivery2);
        orderRepository.saveAll(List.of(order1, order2));

        int orderGoods1_quantity = 2;
        int orderGoods2_Quantity = 3;
        OrderGoods orderGoods1 = createOrderGoods(orderGoods1_quantity, order1, goods);
        OrderGoods orderGoods2 = createOrderGoods(orderGoods2_Quantity, order2, goods);
        orderGoodsRepository.saveAll(List.of(orderGoods1, orderGoods2));

        int remainingGoodsInventoryQuantity = goodsInventoryQuantity - orderGoods2_Quantity;

        // when
        CancelOrderResponse cancelOrderResponse = orderService.cancelOrder(order1.getId(), user.getId());

        em.flush();
        em.clear();

        Goods foundGoods = goodsRepository.findById(goods.getId()).get();

        // then
        assertThat(cancelOrderResponse.getOrderStatus()).isEqualByComparingTo(OrderStatus.CANCELED_ORDER);
        assertThat(cancelOrderResponse.getOrderGoods())
                .extracting("goodsName", "quantity", "paymentAmount")
                .containsExactlyInAnyOrder(
                        tuple(goodsName, orderGoods1_quantity,
                                goodsPrice.multiply(new BigDecimal(orderGoods1_quantity)))
                );
        assertThat(foundGoods.getInventoryQuantity()).isEqualTo(remainingGoodsInventoryQuantity);
    }

    @DisplayName("존재하지 않는 주문 ID로 주문을 취소할 경우, 예외가 발생한다.")
    @Transactional
    @Test
    void cancelOrderWithoutOrderId() {
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

        // when // then
        assertThatThrownBy(() -> {
            Long nonExistOrderId = 0L;
            orderService.cancelOrder(nonExistOrderId, user.getId());
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 주문입니다.");
    }

    @DisplayName("권한이 없는 사용자 ID로 주문을 취소할 경우, 예외가 발생한다.")
    @Transactional
    @Test
    void cancelOrderUnauthorizedUserId() {
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

        // when // then
        assertThatThrownBy(() -> {
            Long unauthorizedUserId = 0L;
            orderService.cancelOrder(order1.getId(), unauthorizedUserId);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("주문을 취소할 권한이 없습니다.");
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