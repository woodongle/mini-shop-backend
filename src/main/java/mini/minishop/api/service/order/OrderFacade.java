package mini.minishop.api.service.order;

import lombok.RequiredArgsConstructor;
import mini.minishop.api.service.order.request.CreateOrderServiceRequest;
import mini.minishop.api.service.order.response.CreateOrderResponse;
import mini.minishop.api.service.user.UserService;
import mini.minishop.domain.user.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final UserService userService;

    public CreateOrderResponse createOrder(CreateOrderServiceRequest request,
                                           String userEmail,
                                           Long goodsId) {
        User currnetUser = userService.findUserByUserEmail(userEmail);

        return orderService.createOrderWithOptimisticLock(goodsId, request.getAddress(), currnetUser,
                request.getOrderGoodsQuantity());
    }
}
