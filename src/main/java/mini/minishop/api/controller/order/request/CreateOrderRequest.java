package mini.minishop.api.controller.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Min(value = 1, message = "주문 상품 수량은 1 이상이어야 합니다.")
    private int orderGoodsQuantity;
}
