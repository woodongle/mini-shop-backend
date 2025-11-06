package mini.minishop.spring.docs.goods;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import mini.minishop.api.controller.goods.GoodsController;
import mini.minishop.api.controller.goods.request.CreateGoodsRequest;
import mini.minishop.api.service.goods.GoodsService;
import mini.minishop.api.service.goods.request.CreateGoodsServiceRequest;
import mini.minishop.api.service.goods.response.CreateGoodsResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.config.JpaAuditingConfig;
import mini.minishop.domain.user.User;
import mini.minishop.domain.user.UserRole;
import mini.minishop.spring.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(
        value = GoodsController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JpaAuditingConfig.class
                )
        })
public class GoodsControllerDocsTest extends RestDocsSupport {

    @MockitoBean
    private GoodsService goodsService;

    @MockitoBean
    private UserService userService;

    @DisplayName("신규 상품을 등록하는 API")
    @WithMockUser(username = "user@user.com", roles = "USER")
    @Test
    void createGoods() throws Exception {
        User mockUser = User.builder()
                .email("user@user.com")
                .role(UserRole.USER)
                .build();

        given(userService.findUser(mockUser.getEmail())).willReturn(mockUser);

        CreateGoodsRequest request = CreateGoodsRequest.builder()
                .name("goods")
                .price(new BigDecimal("1000.00"))
                .inventoryQuantity(100)
                .build();

        given(goodsService.createGoods(any(CreateGoodsServiceRequest.class), eq(mockUser)))
                .willReturn(CreateGoodsResponse.builder()
                        .goodsId(1L)
                        .goodsName(request.getName())
                        .price(request.getPrice())
                        .inventoryQuantity(request.getInventoryQuantity())
                        .createDate(LocalDateTime.of(2000, 1, 1, 9, 0))
                        .build());

        mockMvc.perform(post("/api/v1/goods")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("상품 등록이 완료되었습니다."))
                .andDo(document("goods-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(STRING)
                                        .description("상품 이름"),
                                fieldWithPath("price").type(NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("inventoryQuantity").type(NUMBER)
                                        .description("상품 재고 수량")
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
                                fieldWithPath("data.goodsId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("data.goodsName").type(STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.price").type(NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.inventoryQuantity").type(NUMBER)
                                        .description("상품 재고 수량"),
                                fieldWithPath("data.createDate").type(STRING)
                                        .description("상품 재고 수량")
                        )
                ));

    }
}
