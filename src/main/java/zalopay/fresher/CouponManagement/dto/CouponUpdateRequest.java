package zalopay.fresher.CouponManagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import zalopay.fresher.CouponManagement.util.ErrorMessages;

import java.time.LocalDateTime;

@Data
public class CouponUpdateRequest {

    private String id;
    
    @Size(max = 255, message = ErrorMessages.TITLE_MAX_LENGTH)
    @Pattern(regexp = "^[^<>\"'&]*$", message = ErrorMessages.TITLE_INVALID_CONTENT)
    private String title;
    
    @Size(max = 50, message = ErrorMessages.COUPON_CODE_MAX_LENGTH)
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = ErrorMessages.COUPON_CODE_INVALID_FORMAT)
    private String code;
    
    @Size(max = 2000, message = ErrorMessages.DESCRIPTION_MAX_LENGTH)
    @Pattern(regexp = "^[^<>\"'&]*$", message = ErrorMessages.DESCRIPTION_INVALID_CONTENT)
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private Boolean isActive;
}
