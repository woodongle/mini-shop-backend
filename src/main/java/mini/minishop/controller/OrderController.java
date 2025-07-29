package mini.minishop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mini.minishop.domain.User;
import mini.minishop.dto.order.CreateOrderRequest;
import mini.minishop.service.OrderService;
import mini.minishop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
}
