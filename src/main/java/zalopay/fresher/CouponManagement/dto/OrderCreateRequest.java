package zalopay.fresher.CouponManagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import zalopay.fresher.CouponManagement.util.ErrorMessages;

import java.time.LocalDateTime;

@Data
public class OrderCreateRequest {
    @NotNull(message = ErrorMessages.ORDER_AMOUNT_REQUIRED)
    @DecimalMin(value = "1.0", message = ErrorMessages.ORDER_AMOUNT_MIN)
    private double orderTotalAmount;

    @Size(max = 50, message = ErrorMessages.COUPON_CODE_MAX_LENGTH)
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = ErrorMessages.COUPON_CODE_INVALID_FORMAT)
    private String couponCode;

    @NotNull(message = ErrorMessages.ORDER_DATE_REQUIRED)
    private LocalDateTime orderDate;
}
