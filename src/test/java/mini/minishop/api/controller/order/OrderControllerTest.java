package mini.minishop.api.controller.order;

import static mini.minishop.domain.delivery.DeliveryStatus.BEFORE_DELIVERY;
import static mini.minishop.domain.order.OrderStatus.COMPLETED_ORDER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import mini.minishop.api.controller.order.request.CreateOrderRequest;
import mini.minishop.domain.delivery.Delivery;
import mini.minishop.domain.delivery.DeliveryRepository;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
import mini.minishop.domain.order.Order;
import mini.minishop.domain.order.OrderRepository;
import mini.minishop.domain.ordergoods.OrderGoods;
import mini.minishop.domain.ordergoods.OrderGoodsRepository;
import mini.minishop.domain.user.RefreshTokenRepository;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRepository;
import mini.minishop.domain.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OrderGoodsRepository orderGoodsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        orderGoodsRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        deliveryRepository.deleteAllInBatch();
        goodsRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 입력으로 주문을 생성한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void createOrder() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .address("서울시")
                .orderGoodsQuantity(2)
                .build();

        Long goodsId = goods.getId();

        // when // then
        mockMvc.perform(post("/api/v1/order/{goodsId}", goodsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("상품 주문이 완료되었습니다."));
    }

    @DisplayName("사용자 입력에 문제가 있으면 예외가 발생한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void createOrderWithInvalidRequest() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .address("서울시")
                .orderGoodsQuantity(0)
                .build();

        Long goodsId = goods.getId();

        // when // then
        mockMvc.perform(post("/api/v1/order/{goodsId}", goodsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.code").value("400"))
                .andExpect(jsonPath("$.data.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data.message").value("주문 상품 수량은 양수여야 합니다."));
    }

    @DisplayName("사용자 ID로 사용자가 지금까지 주문한 내역을 모두 조회한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void findOrderHistory() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        Delivery delivery1 = Delivery.builder()
                .status(BEFORE_DELIVERY)
                .address("서울시")
                .build();
        Delivery delivery2 = Delivery.builder()
                .status(BEFORE_DELIVERY)
                .address("서울시")
                .build();
        deliveryRepository.saveAll(List.of(delivery1, delivery2));

        Order order1 = Order.builder()
                .status(COMPLETED_ORDER)
                .user(user)
                .delivery(delivery1)
                .build();
        Order order2 = Order.builder()
                .status(COMPLETED_ORDER)
                .user(user)
                .delivery(delivery2)
                .build();
        orderRepository.saveAll(List.of(order1, order2));

        OrderGoods orderGoods1 = OrderGoods.builder()
                .quantity(2)
                .order(order1)
                .goods(goods)
                .build();
        OrderGoods orderGoods2 = OrderGoods.builder()
                .quantity(3)
                .order(order2)
                .goods(goods)
                .build();
        orderGoodsRepository.saveAll(List.of(orderGoods1, orderGoods2));

        em.flush();
        em.clear();

        Long userId = user.getId();

        // when // then
        mockMvc.perform(get("/api/v1/order/{userId}/orders", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].orderStatus").value("COMPLETED_ORDER"))
                .andExpect(jsonPath("$.data.[0].orderGoods[0].goodsName").value("goods"))
                .andExpect(jsonPath("$.data.[0].orderGoods[0].quantity").value(2))
                .andExpect(jsonPath("$.data.[0].orderGoods[0].paymentAmount").value(2000.00))
                .andExpect(jsonPath("$.data.[1].orderStatus").value("COMPLETED_ORDER"))
                .andExpect(jsonPath("$.data.[1].orderGoods[0].goodsName").value("goods"))
                .andExpect(jsonPath("$.data.[1].orderGoods[0].quantity").value(3))
                .andExpect(jsonPath("$.data.[1].orderGoods[0].paymentAmount").value(3000.00));
    }

    @DisplayName("주문 ID로 주문을 취소한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void cancelOrder() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        Delivery delivery1 = Delivery.builder()
                .status(BEFORE_DELIVERY)
                .address("서울시")
                .build();
        Delivery delivery2 = Delivery.builder()
                .status(BEFORE_DELIVERY)
                .address("서울시")
                .build();
        deliveryRepository.saveAll(List.of(delivery1, delivery2));

        Order order1 = Order.builder()
                .status(COMPLETED_ORDER)
                .user(user)
                .delivery(delivery1)
                .build();
        Order order2 = Order.builder()
                .status(COMPLETED_ORDER)
                .user(user)
                .delivery(delivery2)
                .build();
        orderRepository.saveAll(List.of(order1, order2));

        OrderGoods orderGoods1 = OrderGoods.builder()
                .quantity(2)
                .order(order1)
                .goods(goods)
                .build();
        OrderGoods orderGoods2 = OrderGoods.builder()
                .quantity(3)
                .order(order2)
                .goods(goods)
                .build();
        orderGoodsRepository.saveAll(List.of(orderGoods1, orderGoods2));

        em.flush();
        em.clear();

        Long order1Id = order1.getId();

        // when // then
        mockMvc.perform(patch("/api/v1/order/{orderId}/cancel", order1Id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.canceledDate").exists());
    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .role(UserRole.USER)
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
}