package mini.minishop.api.controller.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @Positive(message = "주문 상품 수량은 양수여야 합니다.")
    private int orderGoodsQuantity;


}
