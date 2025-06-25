package zalopay.fresher.CouponManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderCreateRequest {
    private double totalAmount;
    private String couponCode;
    private LocalDateTime orderDate;
}
