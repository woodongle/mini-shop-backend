package mini.minishop.exception.order;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super(OrderErrorCode.ORDER_NOT_FOUND.getMessage());
    }
}
