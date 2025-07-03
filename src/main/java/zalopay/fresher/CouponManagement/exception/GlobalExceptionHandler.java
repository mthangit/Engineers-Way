package zalopay.fresher.CouponManagement.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zalopay.fresher.CouponManagement.dto.ApiResponse;
import zalopay.fresher.CouponManagement.dto.ResponseCode;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ApiResponse response = new ApiResponse();
        response.setMessage(e.getMessage());
        response.setCode(e.getResponseCode().getCode());
        response.setData(null);
        return ResponseEntity.status(e.getResponseCode().getCode()).body(response);
    }
    
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ApiResponse response = new ApiResponse();
        response.setMessage(errorMessage);
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse response = new ApiResponse();
        response.setMessage(e.getMessage());
        response.setCode(ResponseCode.INVALID_INPUT.getCode());
        response.setData(null);
        
        return ResponseEntity.status(ResponseCode.INVALID_INPUT.getCode()).body(response);
    }
}
