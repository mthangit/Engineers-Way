package zalopay.fresher.CouponManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponResponse {
    String couponCode;
    String title;
    String description;
    LocalDateTime startDate;
    LocalDateTime expireDate;
}
