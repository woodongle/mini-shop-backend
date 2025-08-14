package mini.minishop.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mini.minishop.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final int status;
    private final String message;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus().value(), errorCode.getMessage());
    }
}
