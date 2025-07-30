package mini.minishop.exception.goods;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GoodsErrorCode implements ErrorCode {
    GOODS_NOT_FOUND("G001", "존재하지 않는 상품입니다.", NOT_FOUND),
    NO_PERMISSION_MODIFY_GOODS("G002", "상품을 수정할 권한이 없습니다.", FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
