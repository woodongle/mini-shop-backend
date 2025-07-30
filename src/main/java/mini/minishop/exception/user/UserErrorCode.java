package mini.minishop.exception.user;


import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    ALREADY_EXISTS_EMAIL("U001", "이미 사용 중인 이메일입니다.", CONFLICT),
    USER_NOT_FOUND("U002", "존재하지 않는 회원입니다.", NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
