package mini.minishop.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.User;
import mini.minishop.dto.order.CancelOrderResponse;
import mini.minishop.dto.order.CreateOrderRequest;
import mini.minishop.dto.order.FindOrderHistoryResponse;
import mini.minishop.service.OrderService;
import mini.minishop.service.UserService;
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
    public ResponseEntity<String> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long goodsId) {

        User user = userService.findUser(userDetails.getUsername());
        orderService.createOrder(request, user, goodsId);

        return ResponseEntity.ok("상품 주문이 완료되었습니다.");
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<FindOrderHistoryResponse>> findOrderHistory(@PathVariable Long userId) {
        List<FindOrderHistoryResponse> orderHistory = orderService.findOrderHistory(userId);

        return ResponseEntity.ok(orderHistory);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@PathVariable Long orderId,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUser(userDetails.getUsername());
        CancelOrderResponse response = orderService.cancelOrder(orderId, user.getId());

        return ResponseEntity.ok(response);
    }
}
