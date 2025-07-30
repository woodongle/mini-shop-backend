package mini.minishop.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode {
    ALREADY_EXISTS_EMAIL("U001", "이미 사용 중인 이메일입니다.", null),
    USER_NOT_FOUND("U002", "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
