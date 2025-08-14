package mini.minishop.handler;

import mini.minishop.api.ApiResponse;
import mini.minishop.api.ErrorResponse;
import mini.minishop.exception.BusinessException;
import mini.minishop.exception.CommonErrorCode;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);

        return new ResponseEntity<>(ApiResponse.of(response.getStatus(), response), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(ApiResponse.of(response.getStatus(), response), response.getStatus());
    }
}

