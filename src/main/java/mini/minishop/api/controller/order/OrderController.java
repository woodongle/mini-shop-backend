package mini.minishop.api.controller.order;

import static org.springframework.http.HttpStatus.CREATED;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.api.ApiResponse;
import mini.minishop.api.controller.order.request.CreateOrderRequest;
import mini.minishop.api.service.order.OrderService;
import mini.minishop.api.service.order.response.CancelOrderResponse;
import mini.minishop.api.service.order.response.CreateOrderResponse;
import mini.minishop.api.service.order.response.FindOrderHistoryResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/{goodsId}")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                                        @PathVariable Long goodsId) {

        User user = userService.findUser(userDetails.getUsername());
        CreateOrderResponse response = orderService.createOrder(request.toServiceRequest(), user, goodsId);

        return ResponseEntity.status(CREATED).body(ApiResponse.created("상품 주문이 완료되었습니다.", response));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<ApiResponse<List<FindOrderHistoryResponse>>> findOrderHistory(@PathVariable Long userId) {
        List<FindOrderHistoryResponse> response = orderService.findOrderHistory(userId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<CancelOrderResponse>> cancelOrder(@PathVariable Long orderId,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        CancelOrderResponse response = orderService.cancelOrder(orderId, user.getId());

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
