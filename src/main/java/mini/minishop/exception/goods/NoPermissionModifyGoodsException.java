package mini.minishop.exception.goods;

public class NoPermissionModifyGoodsException extends RuntimeException {
    public NoPermissionModifyGoodsException() {
        super(GoodsErrorCode.NO_PERMISSION_MODIFY_GOODS.getMessage());
    }
}
