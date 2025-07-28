package mini.minishop.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GoodsErrorCode {
    GOODS_NOT_FOUND("G001", "존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    GoodsErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
