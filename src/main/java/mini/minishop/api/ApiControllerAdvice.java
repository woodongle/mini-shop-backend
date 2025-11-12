package mini.minishop.api;

import mini.minishop.exception.BusinessException;
import mini.minishop.exception.CommonErrorCode;
import mini.minishop.exception.ErrorCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    protected ApiResponse<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);

        return ApiResponse.of(response.getStatus(), response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        ErrorResponse response = ErrorResponse.of(errorCode, message);

        return ApiResponse.of(response.getStatus(), response);
    }

    @ExceptionHandler(Exception.class)
    protected ApiResponse<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR);

        return ApiResponse.of(response.getStatus(), response);
    }
}

