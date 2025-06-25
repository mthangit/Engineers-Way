package zalopay.fresher.CouponManagement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zalopay.fresher.CouponManagement.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ApiResponse response = new ApiResponse();
        response.setMessage(e.getMessage());
        response.setCode(e.getResponseCode().getCode());
        response.setData(null);
        return ResponseEntity.badRequest().body(response);
    }
}
