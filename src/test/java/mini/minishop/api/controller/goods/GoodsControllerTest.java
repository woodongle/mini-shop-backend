package mini.minishop.api.controller.goods;

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
import mini.minishop.api.controller.goods.request.CreateGoodsRequest;
import mini.minishop.api.controller.goods.request.UpdateGoodsRequest;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
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
class GoodsControllerTest {

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        goodsRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }


    @DisplayName("사용자 입력으로 상품을 등록한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void createGoods() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        CreateGoodsRequest request = CreateGoodsRequest.builder()
                .name("goods")
                .price(new BigDecimal("1000.00"))
                .inventoryQuantity(100)
                .build();

        // when // then
        mockMvc.perform(post("/api/v1/goods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("상품 등록이 완료되었습니다."));
    }

    @DisplayName("사용자 입력에 문제가 있으면 예외가 발생한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void createGoodsWithInvalidRequest() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        CreateGoodsRequest request = CreateGoodsRequest.builder()
                .name("")
                .price(new BigDecimal("1000.00"))
                .inventoryQuantity(100)
                .build();

        // when // then
        mockMvc.perform(post("/api/v1/goods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.code").value("400"))
                .andExpect(jsonPath("$.data.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data.message").value("상품명은 필수입니다."));
    }

    @DisplayName("모든 상품을 조회한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void findGoods() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods1 = createGoods("goods1", new BigDecimal("1000.00"), 10, user);
        Goods goods2 = createGoods("goods2", new BigDecimal("2000.00"), 20, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        // when // then
        mockMvc.perform(get("/api/v1/goods"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("goods1"))
                .andExpect(jsonPath("$[0].price").value(1000.00))
                .andExpect(jsonPath("$[0].inventoryQuantity").value(10))
                .andExpect(jsonPath("$[1].name").value("goods2"))
                .andExpect(jsonPath("$[1].price").value(2000.00))
                .andExpect(jsonPath("$[1].inventoryQuantity").value(20));
    }

    @DisplayName("상품 ID로 상품 정보를 조회한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void findGoodsWithValidGoodsId() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        Long goodsId = goods.getId();

        // when // then
        mockMvc.perform(get("/api/v1/goods/{goodsId}", goodsId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("goods"))
                .andExpect(jsonPath("$.price").value(1000.00))
                .andExpect(jsonPath("$.inventoryQuantity").value(10));
    }

    @DisplayName("상품 이름으로 상품들을 조회한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void searchGoodsByName() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods1 = createGoods("goods1", new BigDecimal("1000.00"), 10, user);
        Goods goods2 = createGoods("goods2", new BigDecimal("2000.00"), 20, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        // when // then
        mockMvc.perform(get("/api/v1/goods")
                        .param("name", "goods")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("goods1"))
                .andExpect(jsonPath("$[0].price").value(1000.00))
                .andExpect(jsonPath("$[0].inventoryQuantity").value(10))
                .andExpect(jsonPath("$[1].name").value("goods2"))
                .andExpect(jsonPath("$[1].price").value(2000.00))
                .andExpect(jsonPath("$[1].inventoryQuantity").value(20));
    }

    @DisplayName("사용자 입력으로 상품을 수정한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void updateGoods() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        UpdateGoodsRequest request = UpdateGoodsRequest.builder()
                .goodsName("newGoodsName")
                .price(new BigDecimal("2000.00"))
                .inventoryQuantity(20)
                .build();

        Long goodsId = goods.getId();

        // when // then
        mockMvc.perform(patch("/api/v1/goods/{goodsId}", goodsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goodsName").value("newGoodsName"))
                .andExpect(jsonPath("$.price").value(2000.00))
                .andExpect(jsonPath("$.inventoryQuantity").value(20));
    }

    @DisplayName("사용자 입력에 문제가 있으면 예외가 발생한다.")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Transactional
    @Test
    void updateGoodsWithInvalidRequest() throws Exception {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        UpdateGoodsRequest request = UpdateGoodsRequest.builder()
                .goodsName("")
                .price(new BigDecimal("2000.00"))
                .inventoryQuantity(20)
                .build();

        Long goodsId = goods.getId();

        // when // then
        mockMvc.perform(patch("/api/v1/goods/{goodsId}", goodsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.code").value("400"))
                .andExpect(jsonPath("$.data.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.data.message").value("상품명은 필수입니다."));
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