package mini.minishop.exception.order;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("o001", "존재하지 않는 주문입니다.", NOT_FOUND),
    NO_PERMISSION_MODIFY_ORDER("o002", "주문을 취소할 권한이 없습니다.", FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
