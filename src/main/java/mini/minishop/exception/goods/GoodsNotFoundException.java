package mini.minishop.exception.goods;

public class GoodsNotFoundException extends RuntimeException {
    public GoodsNotFoundException() {
        super(GoodsErrorCode.GOODS_NOT_FOUND.getMessage());
    }
}
