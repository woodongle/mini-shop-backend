package mini.minishop.exception.order;

import static org.springframework.http.HttpStatus.CONFLICT;
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
    NO_PERMISSION_MODIFY_ORDER("o002", "주문을 취소할 권한이 없습니다.", FORBIDDEN),
    OPTIMISTIC_LOCK_RETRY_FAILED("003", "주문을 처리하는 과정에서 충돌이 발생했습니다. 잠시 후 다시 시도해 주세요.", CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
