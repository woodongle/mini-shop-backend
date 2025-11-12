package mini.minishop.api.service.goods;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import mini.minishop.api.service.goods.request.CreateGoodsServiceRequest;
import mini.minishop.api.service.goods.request.UpdateGoodsServiceRequest;
import mini.minishop.api.service.goods.response.CreateGoodsResponse;
import mini.minishop.api.service.goods.response.FindGoodsResponse;
import mini.minishop.api.service.goods.response.UpdateGoodsResponse;
import mini.minishop.domain.goods.Goods;
import mini.minishop.domain.goods.GoodsRepository;
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
class GoodsServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private EntityManager em;

    @AfterEach
    void tearDown() {
        goodsRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 요청 정보로 상품을 등록한다.")
    @Test
    void createGoods() {
        // given
        User user = createUser("user", "user@user.com", "user");
        User savedUser = userRepository.save(user);

        String goodsName = "goods";
        BigDecimal goodsPrice = new BigDecimal("1000.00");
        int goodsInventoryQuantity = 10;
        CreateGoodsServiceRequest request = CreateGoodsServiceRequest.builder().name(goodsName).price(goodsPrice)
                .inventoryQuantity(goodsInventoryQuantity).build();

        // when
        CreateGoodsResponse goodsResponse = goodsService.createGoods(request, savedUser.getEmail());

        // then
        assertThat(goodsResponse).extracting("goodsId", "goodsName", "price", "inventoryQuantity", "createDate")
                .containsExactlyInAnyOrder(goodsResponse.getGoodsId(), goodsName, goodsPrice, goodsInventoryQuantity,
                        goodsResponse.getCreateDate());
    }

    @DisplayName("상품 정보 응답을 리스트로 조회한다.")
    @Transactional
    @Test
    void findGoods() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String goods1_name = "goods1";
        String goods2_name = "goods2";
        BigDecimal goods1_price = new BigDecimal("1000.00");
        BigDecimal goods2_price = new BigDecimal("2000.00");
        int goods1_inventoryQuantity = 10;
        int goods2_inventoryQuantity = 20;
        Goods goods1 = createGoods(goods1_name, goods1_price, goods1_inventoryQuantity, user);
        Goods goods2 = createGoods(goods2_name, goods2_price, goods2_inventoryQuantity, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        em.flush();
        em.clear();

        // when
        List<FindGoodsResponse> foundGoods = goodsService.findGoods();

        // then
        assertThat(foundGoods).hasSize(2);
        assertThat(foundGoods).extracting("name", "price", "inventoryQuantity")
                .containsExactlyInAnyOrder(tuple(goods1_name, goods1_price, goods1_inventoryQuantity),
                        tuple(goods2_name, goods2_price, goods2_inventoryQuantity));
    }

    @DisplayName("상품 ID로 상품 정보 응답을 조회한다.")
    @Transactional
    @Test
    void findGoodsWithGoodsId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String goods1_name = "goods1";
        String goods2_name = "goods2";
        BigDecimal goods1_price = new BigDecimal("1000.00");
        BigDecimal goods2_price = new BigDecimal("2000.00");
        int goods1_inventoryQuantity = 10;
        int goods2_inventoryQuantity = 20;
        Goods goods1 = createGoods(goods1_name, goods1_price, goods1_inventoryQuantity, user);
        Goods goods2 = createGoods(goods2_name, goods2_price, goods2_inventoryQuantity, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        em.flush();
        em.clear();

        // when
        FindGoodsResponse foundGoods = goodsService.findGoodsByGoodsId(goods1.getId());

        // then
        assertThat(foundGoods).extracting("name", "price", "inventoryQuantity")
                .containsExactlyInAnyOrder(goods1_name, goods1_price, goods1_inventoryQuantity);
    }

    @DisplayName("존재하지 않는 상품 ID로 상품 정보 응답을 조회할 경우, 예외가 발생한다.")
    @Transactional
    @Test
    void findGoodsWithoutGoodsId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        em.flush();
        em.clear();

        // when // then
        assertThatThrownBy(() -> {
            long nonExistGoodsId = 0L;
            goodsService.findGoodsByGoodsId(nonExistGoodsId);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @DisplayName("사용자 요청 정보가 상품 이름에 포함되어 있는 상품 정보 응답을 조회한 후 리스트로 반환한다.")
    @Transactional
    @Test
    void searchGoodsByName() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        String goods1_name = "goods1";
        String goods2_name = "goods2";
        BigDecimal goods1_price = new BigDecimal("1000.00");
        BigDecimal goods2_price = new BigDecimal("2000.00");
        int goods1_inventoryQuantity = 10;
        int goods2_inventoryQuantity = 20;
        Goods goods1 = createGoods(goods1_name, goods1_price, goods1_inventoryQuantity, user);
        Goods goods2 = createGoods(goods2_name, goods2_price, goods2_inventoryQuantity, user);
        goodsRepository.saveAll(List.of(goods1, goods2));

        em.flush();
        em.clear();

        // when
        List<FindGoodsResponse> foundGoods = goodsService.searchGoodsByName("goods");

        // then
        assertThat(foundGoods).hasSize(2);
        assertThat(foundGoods)
                .extracting("name", "price", "inventoryQuantity")
                .containsExactlyInAnyOrder(
                        tuple(goods1_name, goods1_price, goods1_inventoryQuantity),
                        tuple(goods2_name, goods2_price, goods2_inventoryQuantity));
    }

    @DisplayName("사용자 요청 정보로 상품을 업데이트한다.")
    @Test
    void updateGoods() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        String newGoodsName = "newGoodsName";
        BigDecimal newGoodsPrice = new BigDecimal("2000.00");
        int newGoodsInventoryQuantity = 20;
        UpdateGoodsServiceRequest request = UpdateGoodsServiceRequest.builder()
                .goodsName(newGoodsName)
                .price(newGoodsPrice)
                .inventoryQuantity(newGoodsInventoryQuantity)
                .build();

        // when
        UpdateGoodsResponse response = goodsService.updateGoods(goods.getId(), user.getEmail(), request);

        // then
        assertThat(response)
                .extracting("goodsName", "price", "inventoryQuantity")
                .containsExactlyInAnyOrder(newGoodsName, newGoodsPrice, newGoodsInventoryQuantity);
    }

    @DisplayName("존재하지 않는 상품 ID로 상품을 업데이트할 경우, 예외가 발생한다.")
    @Test
    void updateGoodsWithoutUserId() {
        // given
        User user = createUser("user", "user@user.com", "user");
        userRepository.save(user);

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user);
        goodsRepository.save(goods);

        UpdateGoodsServiceRequest request = UpdateGoodsServiceRequest.builder()
                .goodsName("newGoodsName")
                .price(new BigDecimal("2000.00"))
                .inventoryQuantity(20)
                .build();

        // whe // then
        assertThatThrownBy(() -> {
            long nonExistGoodsId = 0L;
            goodsService.updateGoods(nonExistGoodsId, user.getEmail(), request);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @DisplayName("허가되지 않은 유저 ID로 상품을 업데이트할 경우, 예외가 발생한다.")
    @Test
    void updateGoodsUnauthorizedUserId() {
        // given
        User user1 = createUser("user1", "user1@user.com", "user1");
        User user2 = createUser("user2", "user2@user.com", "user2");
        userRepository.saveAll(List.of(user1, user2));

        Goods goods = createGoods("goods", new BigDecimal("1000.00"), 10, user1);
        goodsRepository.save(goods);

        UpdateGoodsServiceRequest request = UpdateGoodsServiceRequest.builder()
                .goodsName("newGoodsName")
                .price(new BigDecimal("2000.00"))
                .inventoryQuantity(20)
                .build();

        // when // then
        assertThatThrownBy(() -> {
            goodsService.updateGoods(goods.getId(), user2.getEmail(), request);
        })
                .isInstanceOf(BusinessException.class)
                .hasMessage("상품을 수정할 권한이 없습니다.");

    }

    private User createUser(String name, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder().name(name).email(email).password(encodedPassword).build();
    }

    private Goods createGoods(String name, BigDecimal price, int inventoryQuantity, User user) {
        return Goods.builder().name(name).price(price).inventoryQuantity(inventoryQuantity).user(user).build();
    }
}