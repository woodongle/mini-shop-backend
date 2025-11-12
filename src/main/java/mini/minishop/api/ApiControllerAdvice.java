package mini.minishop.api;

import mini.minishop.exception.BusinessException;
import mini.minishop.exception.CommonErrorCode;
import mini.minishop.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);

        return new ResponseEntity<>(ApiResponse.of(response.getStatus(), response), errorCode.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        ErrorResponse response = ErrorResponse.of(errorCode, message);

        return new ResponseEntity<>(ApiResponse.of(response.getStatus(), response), errorCode.getStatus());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR);

        return ApiResponse.of(response.getStatus(), response);
    }
}

