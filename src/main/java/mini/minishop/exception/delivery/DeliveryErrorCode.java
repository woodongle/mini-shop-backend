package mini.minishop.exception.delivery;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {

    DELIVERY_CANCEL_NOT_ALLOWED("D001", "배송이 진행 중이거나, 이미 배송 완료된 상품은 취소가 불가능합니다.", BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
