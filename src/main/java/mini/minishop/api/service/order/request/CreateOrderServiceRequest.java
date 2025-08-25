package mini.minishop.api.service.order.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderServiceRequest {

    private String address;
    private int orderGoodsQuantity;

    @Builder
    public CreateOrderServiceRequest(String address, int orderGoodsQuantity) {
        this.address = address;
        this.orderGoodsQuantity = orderGoodsQuantity;
    }
}
