package zalopay.fresher.CouponManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponModify{
    private String title;
    private String code;
    private String description;
    private String discountType;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private Boolean isActive;
}
