package mini.minishop.exception.order;

public class NoPermissionModifyOrderException extends RuntimeException {
    public NoPermissionModifyOrderException() {
        super(OrderErrorCode.NO_PERMISSION_MODIFY_ORDER.getMessage());
    }
}
