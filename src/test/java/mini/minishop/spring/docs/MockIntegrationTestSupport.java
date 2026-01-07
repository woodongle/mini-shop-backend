package mini.minishop.spring.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import mini.minishop.api.controller.HomeController;
import mini.minishop.api.controller.goods.GoodsController;
import mini.minishop.api.controller.order.OrderController;
import mini.minishop.api.controller.user.UserController;
import mini.minishop.api.service.auth.AuthService;
import mini.minishop.api.service.auth.RefreshTokenService;
import mini.minishop.api.service.goods.GoodsService;
import mini.minishop.api.service.order.OrderFacade;
import mini.minishop.api.service.order.OrderService;
import mini.minishop.api.service.user.UserService;
import mini.minishop.config.JpaAuditingConfig;
import mini.minishop.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        value = {
                HomeController.class,
                GoodsController.class,
                OrderController.class,
                UserController.class,
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JpaAuditingConfig.class
                )
        })
@AutoConfigureRestDocs
public abstract class MockIntegrationTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
    
    // Mock Beans

    @MockitoBean
    protected GoodsService goodsService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected OrderFacade orderFacade;

    @MockitoBean
    protected AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    protected RefreshTokenService refreshTokenService;

    @MockitoBean
    protected AuthService authService;
}
