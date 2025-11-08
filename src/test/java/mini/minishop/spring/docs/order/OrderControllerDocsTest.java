package mini.minishop.spring.docs.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import mini.minishop.api.controller.order.OrderController;
import mini.minishop.api.controller.order.request.CreateOrderRequest;
import mini.minishop.api.service.order.OrderService;
import mini.minishop.api.service.order.request.CreateOrderServiceRequest;
import mini.minishop.api.service.order.response.CreateOrderResponse;
import mini.minishop.api.service.order.response.OrderGoodsResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.config.JpaAuditingConfig;
import mini.minishop.domain.user.User;
import mini.minishop.spring.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(
        value = OrderController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JpaAuditingConfig.class
                )
        })
public class OrderControllerDocsTest extends RestDocsSupport {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserService userService;

    @DisplayName("상품을 주문하는 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void createOrder() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .address("주소")
                .orderGoodsQuantity(5)
                .build();

        OrderGoodsResponse goods1 = OrderGoodsResponse.builder()
                .goodsName("상품1")
                .quantity(2)
                .paymentAmount(new BigDecimal("2000.00"))
                .build();
        OrderGoodsResponse goods2 = OrderGoodsResponse.builder()
                .goodsName("상품2")
                .quantity(2)
                .paymentAmount(new BigDecimal("5000.00"))
                .build();
        CreateOrderResponse response = CreateOrderResponse.builder()
                .orderId(1L)
                .orderedDate(LocalDateTime.of(2000, 1, 1, 9, 0))
                .orderGoods(List.of(goods1, goods2))
                .build();

        User mockUser = mock(User.class);

        String mockUserEmail = "user@user.com";
        Long mockGoodsId = 1L;

        given(userService.findUser(mockUserEmail))
                .willReturn(mockUser);
        given(orderService.createOrder(any(CreateOrderServiceRequest.class), eq(mockUser), eq(mockGoodsId)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/order/{goodsId}", mockGoodsId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("상품 주문이 완료되었습니다."))
                .andDo(document("/order/order-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("address").type(STRING)
                                        .description("사용자 주소"),
                                fieldWithPath("orderGoodsQuantity").type(NUMBER)
                                        .description("주문 상품 수량")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("응답 상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("응답 메시지"),
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.orderId").type(NUMBER)
                                        .description("주문 ID"),
                                fieldWithPath("data.orderedDate").type(STRING)
                                        .description("주문 날짜"),
                                fieldWithPath("data.orderGoods").type(ARRAY)
                                        .description("주문 상품"),
                                fieldWithPath("data.orderGoods[].goodsName").type(STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.orderGoods[].quantity").type(NUMBER)
                                        .description("상품 수량"),
                                fieldWithPath("data.orderGoods[].paymentAmount").type(NUMBER)
                                        .description("총 상품 가격")
                        )
                ));

    }


}
